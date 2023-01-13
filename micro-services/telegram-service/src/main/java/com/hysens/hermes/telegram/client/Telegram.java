package com.hysens.hermes.telegram.client;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.telegram.config.CommunicateMethod;
import com.hysens.hermes.telegram.exception.TelegramChatWithUserNotFoundException;
import com.hysens.hermes.telegram.exception.TelegramPhoneNumberNotFoundException;
import com.hysens.hermes.telegram.service.TelegramService;
import it.tdlight.client.APIToken;
import it.tdlight.client.AuthenticationData;
import it.tdlight.client.CommandHandler;
import it.tdlight.client.TDLibSettings;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

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

        client.start(authenticationData);


            try {
                client.waitForExit();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
                TdApi.User user = result.get();
                LOG.warn("Received new message from " + user.id + ": " + text);

                SimpleMessage simpleMessage = new SimpleMessage();
                simpleMessage.setMessage(text);
                simpleMessage.setSenderPhone(user.phoneNumber);
                simpleMessage.setFromMe(false);
                simpleMessage.setMessenger("Telegram");
                simpleMessage.setMessageStatus("Received");
                simpleMessageService.saveWithoutClientId(simpleMessage, user.id);
            });
        }
    }

    private static void onUpdateChatReadOutbox(TdApi.UpdateChatReadOutbox update) {
        long chatId = update.chatId;
        long messageId = update.lastReadOutboxMessageId;

        client.send(new TdApi.GetMessage(chatId, messageId), messageResult -> {
            TdApi.Message message = messageResult.get();
            TdApi.MessageContent messageContent = message.content;
            String text;
            if (messageContent instanceof TdApi.MessageText) {
                text = ((TdApi.MessageText) messageContent).text.text;
            } else {
                text = String.format("(%s)", messageContent.getClass().getSimpleName());
            }
            client.send(new TdApi.GetChat(chatId), chatIdResult -> {
                TdApi.Chat chat = chatIdResult.get();
                String chatName = chat.title;
                LOG.info("Message: " + text + " to " + chatName + " - READ");
            });

        });

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
//            simpleMessageService.setTelegramIdByPhone(result.get().id, number);
            info.setUserExist(true);
            info.setUserId(String.valueOf(result.get().id));
            isUserExist.setResult(info);
        }, Telegram::springHandleResultHandlingException);
    }

    public static void isChatExist(String userId, String message, MessageRecipientInfo info) {
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
            sendMessage(Long.parseLong(userId), result.get().title, message, info, isChatExistAndSended);
            info.setChatWithUserExist(true);
        }, Telegram::springHandleResultHandlingException);
    }
    public static void createChatAndSend(String userId, String message) {
        client.send(new TdApi.CreatePrivateChat(Long.parseLong(userId), false), result -> {
            TdApi.Chat chat = result.get();
            sendMessage(chat.id, chat.title, message);
        });
    }

    private static void sendMessage(long id, String title, String message) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        client.send(new TdApi.SendMessage(id, 0, 0, null, null, content), result -> {
            TdApi.Message sendedMessage = result.get();
            LOG.info("Message: " + message + " to " + title + " SENDED using Telegram");
        });
    }

    private static void sendMessage(long id, String title, String message, MessageRecipientInfo info, CommunicateMethod communicateMethod) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        client.send(new TdApi.SendMessage(id, 0, 0, null, null, content), result -> {
            TdApi.Message sendedMessage = result.get();
            info.setMessageSended(true);
            communicateMethod.setResult(info);
            LOG.info("Message: " + message + " to " + title + " SENDED using Telegram");
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
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            LOG.info("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            LOG.info("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            LOG.info("Logging out...");
        }
    }

    private static boolean isAdmin(TdApi.MessageSender sender) {
        return sender.equals(ADMIN_ID);
    }
}

