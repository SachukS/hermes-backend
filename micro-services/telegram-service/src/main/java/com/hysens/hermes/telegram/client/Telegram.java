package com.hysens.hermes.telegram.client;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.telegram.config.CommunicateMethod;
import com.hysens.hermes.telegram.exception.TelegramChatWithUserNotFoundException;
import com.hysens.hermes.telegram.exception.TelegramPhoneNumberNotFoundException;
import com.hysens.hermes.telegram.service.TelegramService;
import it.tdlight.client.*;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Telegram {
    private static SimpleMessageService simpleMessageService;
    private static final TdApi.MessageSender ADMIN_ID = new TdApi.MessageSenderUser(489214541);

    public static final Logger LOG = LoggerFactory.getLogger(Telegram.class);

    public static final JFrame QRCodeFrame = new JFrame();

    private static SpringTelegramClient client;

    public Telegram(SimpleMessageService messageService) {
        try {
            Init.start();
        } catch (CantLoadLibrary cantLoadLibrary) {
            cantLoadLibrary.printStackTrace();
        }

        simpleMessageService = messageService;

        APIToken apiToken = new APIToken(9234724, "990cded7571d97f83502e39b1793b63b");

        TDLibSettings settings = TDLibSettings.create(apiToken);

        Path sessionPath = Paths.get("hermes-tdlight-session");
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

        client = new SpringTelegramClient(settings);

        AuthenticationData authenticationData = AuthenticationData.qrCode();

        // Add an example update handler that prints when the bot is started
        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, Telegram::onUpdateAuthorizationState);

        // Add an example update handler that prints every received message
        client.addUpdateHandler(TdApi.UpdateChatReadOutbox.class, Telegram::onUpdateChatReadOutbox);

        client.addUpdateHandler(TdApi.UpdateNewMessage.class, Telegram::onUpdateNewMessage);

        client.addUpdateHandler(TdApi.UpdateMessageSendSucceeded.class, Telegram::onUpdateMessageSendSucceeded);

        client.addUpdateHandler(TdApi.UpdateMessageSendFailed.class, Telegram::onUpdateMessageSendFailed);

        client.start(authenticationData);

        TdApi.SetLogVerbosityLevel level = new TdApi.SetLogVerbosityLevel(1);
        client.send(level, (ok) -> {
            LOG.info("Verbosity level setted to 1");
        }, throwable -> {});
//        try {
//            client.waitForExit();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
    private static void onUpdateMessageSendFailed(TdApi.UpdateMessageSendFailed update) {
        SimpleMessage simpleMessage = simpleMessageService.findByMessageSpecId(update.oldMessageId);
        simpleMessage.setMessageStatus(MessageStatusEnum.FAILED);
        simpleMessageService.save(simpleMessage);
    }
    private static void onUpdateMessageSendSucceeded(TdApi.UpdateMessageSendSucceeded update) {
        SimpleMessage simpleMessage = simpleMessageService.findByMessageSpecId(update.oldMessageId);
        simpleMessage.setMessageSpecId(update.message.id);
        simpleMessage.setMessageStatus(MessageStatusEnum.SENT);
        simpleMessageService.save(simpleMessage);
    }

    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
        // Get the message content
        var messageContent = update.message.content;
        if (!update.message.isOutgoing)
        {
            // Get the message text
            String text;
            if (messageContent instanceof TdApi.MessageText) {
                // Get the text of the text message
                text = ((TdApi.MessageText) messageContent).text.text;
            } else {
                // We handle only text messages, the other messages will be printed as their type
                text = String.format("(%s)", messageContent.getClass().getSimpleName());
            }
            client.send(new TdApi.GetUser(update.message.chatId), result -> {
                try {
                    TdApi.User user = result.get();
                    LOG.warn("Received new message from " + user.id + ": " + text);

                    SimpleMessage simpleMessage = new SimpleMessage();
                    simpleMessage.setMessage(text);
                    simpleMessage.setSenderPhone(user.phoneNumber);
                    simpleMessage.setFromMe(false);
                    simpleMessage.setMessenger("Telegram");
                    simpleMessage.setMessageStatus(MessageStatusEnum.NEW);
                    simpleMessageService.saveWithoutClientId(simpleMessage, user.id);
                }
                catch (TelegramError e) {
                    LOG.error("Received message from group chat");
                }
            });
        }
    }

    public static String logout() {
        client.send(new TdApi.LogOut(), result -> {
            LOG.info("Telegram Logged Out");
        });
        client.sendClose();
        return "Telegram Logged Out";
    }

    private static void onUpdateChatReadOutbox(TdApi.UpdateChatReadOutbox update) {
        long chatId = update.chatId;
        long messageId = update.lastReadOutboxMessageId;

        simpleMessageService.setReadStatusInTelegram(chatId);
    }

    private static class StopCommandHandler implements CommandHandler {

        @Override
        public void onCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
            // Check if the sender is the admin
            if (isAdmin(commandSender)) {
                // Stop the com.hysens.hermes.telegram.client
                System.out.println("Received stop command. closing...");
                client.sendClose();
            }
        }
    }
    public static void findUser(String number){
        MessageRecipientInfo info = new MessageRecipientInfo();
        client.send(new TdApi.SearchUserByPhoneNumber(number), result -> {
            CommunicateMethod isUserExist = null;
            try {
                isUserExist = TelegramService.communicateMethods.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (result.isError()) {
                if (result.getError().code==404){
                    isUserExist.setResult(info);
                    throw new TelegramPhoneNumberNotFoundException(number);
                }
            }
            simpleMessageService.setTelegramIdByPhone(result.get().id, number.substring(1));
            info.setUserExist(true);
            info.setUserId(String.valueOf(result.get().id));
            isUserExist.setResult(info);
        }, Telegram::springHandleResultHandlingException);
    }

    public static void isChatExist(String userId, SimpleMessage simpleMessage, MessageRecipientInfo info) {
        client.send(new TdApi.GetChat(Long.parseLong(userId)), result -> {
            CommunicateMethod isChatExistAndSended = null;
            try {
                isChatExistAndSended = TelegramService.communicateMethods.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (result.isError()) {
                if (result.getError().code==400){
                    isChatExistAndSended.setResult(info);
                    throw new TelegramChatWithUserNotFoundException(userId);
                }
            }
            sendMessage(Long.parseLong(userId), result.get().title, simpleMessage, info, isChatExistAndSended);
            info.setChatWithUserExist(true);
        }, Telegram::springHandleResultHandlingException);
    }
    public static void createChatAndSend(String userId, SimpleMessage simpleMessage) {
        client.send(new TdApi.CreatePrivateChat(Long.parseLong(userId), false), result -> {
            TdApi.Chat chat = result.get();
            sendMessage(chat.id, chat.title, simpleMessage);
        });
    }

    private static void sendMessage(long id, String title, SimpleMessage simpleMessage) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(simpleMessage.getMessage(), null), false, true);
        client.send(new TdApi.SendMessage(id, 0, 0, null, null, content), result -> {
            TdApi.Message sendedMessage = result.get();
            simpleMessage.setMessageSpecId(sendedMessage.id);
            simpleMessage.setMessenger("Telegram");
            simpleMessageService.save(simpleMessage);
            LOG.info("Message: " + simpleMessage.getMessage() + " to " + title + " SENDED using Telegram" + sendedMessage.id);
        });
    }

    private static void sendMessage(long id, String title, SimpleMessage simpleMessage, MessageRecipientInfo info, CommunicateMethod communicateMethod) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(simpleMessage.getMessage(), null), false, true);
        client.send(new TdApi.SendMessage(id, 0, 0, null, null, content), result -> {
            TdApi.Message sendedMessage = result.get();
            info.setMessageSended(true);
            communicateMethod.setResult(info);
            simpleMessage.setMessageSpecId(sendedMessage.id);
            simpleMessage.setMessenger("Telegram");
            simpleMessageService.save(simpleMessage);
            LOG.info("Message: " + simpleMessage.getMessage() + " to " + title + " SENDED using Telegram"+ sendedMessage.id);
        });
    }

    private static void springHandleResultHandlingException(Throwable ex) {
        LOG.error(ex.getMessage());
    }

    private static void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        TdApi.AuthorizationState authorizationState = update.authorizationState;

        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            if (QRCodeFrame.isEnabled())
                QRCodeFrame.dispose();
            LOG.info("Logged in Telegram");
            TelegramService.isLogined = true;
//            CommunicateMethod authState = null;
//            try {
//                authState = TelegramService.communicateMethods.take();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            authState.setResult(authorizationState);
//        } else if (authorizationState instanceof TdApi.AuthorizationStateWaitOtherDeviceConfirmation) {
//            if (QRCodeFrame.isEnabled())
//                QRCodeFrame.dispose();
//            LOG.info("Waiting QR");
//            TelegramService.isLogined = false;
//            CommunicateMethod authState = null;
//            try {
//                authState = TelegramService.communicateMethods.take();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            authState.setResult(authorizationState);
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            LOG.info("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            TelegramService.isLogined = false;
            LOG.info("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            TelegramService.isLogined = false;
            LOG.info("Logging out...");
        }
    }

    private static boolean isAdmin(TdApi.MessageSender sender) {
        return sender.equals(ADMIN_ID);
    }
}

