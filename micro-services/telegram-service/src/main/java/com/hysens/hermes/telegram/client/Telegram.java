package com.hysens.hermes.telegram.client;

import com.hysens.hermes.common.model.Partner;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.telegram.config.CommunicateMethod;
import com.hysens.hermes.telegram.exception.TelegramPhoneNumberNotFoundException;
import com.hysens.hermes.telegram.service.TelegramService;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import it.tdlight.util.UnsupportedNativeLibraryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Telegram implements AutoCloseable {
    public static final Logger LOG = LoggerFactory.getLogger(Telegram.class);
    private static SimpleTelegramClient client;
    public static SimpleTelegramClientFactory simpleTelegramClientFactory = new SimpleTelegramClientFactory();
    private static SimpleMessageService simpleMessageService;
    public static ExecutorService blockingExecutor = Executors.newSingleThreadExecutor();
    private static ClientInteraction clientInteraction;


    public Telegram(SimpleMessageService messageService) {
        try {
            Init.init();
        } catch (UnsupportedNativeLibraryException e) {
            throw new RuntimeException(e);
        }
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

        simpleMessageService = messageService;


    }

    public static void loginClient(Partner partner) {
        APIToken apiToken = new APIToken(9234724, "990cded7571d97f83502e39b1793b63b");
        TDLibSettings settings = TDLibSettings.create(apiToken);

        Path sessionPath = Paths.get("tdlight-session-"+partner.getPhone());
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

        SimpleTelegramClientBuilder clientBuilder = simpleTelegramClientFactory.builder(settings);

        // Configure the authentication info
        // Replace with AuthenticationSupplier.consoleLogin(), or .user(xxx), or .bot(xxx);
        SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.qrCode();


        // Add an example update handler that prints when the bot is started
        clientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class, Telegram::onUpdateAuthorizationState);

        clientBuilder.addUpdateHandler(TdApi.UpdateChatReadOutbox.class, Telegram::onUpdateChatReadOutbox);

        clientBuilder.addUpdateHandler(TdApi.UpdateMessageSendSucceeded.class, Telegram::onUpdateMessageSendSucceeded);

        clientBuilder.addUpdateHandler(TdApi.UpdateMessageSendFailed.class, Telegram::onUpdateMessageSendFailed);
        // Add an example update handler that prints every received message
        clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, Telegram::onUpdateNewMessage);

        //clientBuilder.setClientInteraction(clientInteraction);

        // Build the client
        client = clientBuilder.build(authenticationData);
        clientInteraction = new SpringClientInteraction(blockingExecutor, client);
        client.setClientInteraction(clientInteraction);
    }

    @Override
    public void close() throws Exception {
        client.close();
    }

    public SimpleTelegramClient getClient() {
        return client;
    }

    private static void onUpdateChatReadOutbox(TdApi.UpdateChatReadOutbox update) {
        long chatId = update.chatId;
        long messageId = update.lastReadOutboxMessageId;

        simpleMessageService.setReadStatusInTelegram(chatId);
    }
    private static void onUpdateMessageSendFailed(TdApi.UpdateMessageSendFailed update) {
        SimpleMessage simpleMessage = simpleMessageService.findByMessageSpecId(String.valueOf(update.oldMessageId));
        simpleMessage.setMessageStatus(MessageStatusEnum.FAILED);
        simpleMessageService.save(simpleMessage);
    }
    private static void onUpdateMessageSendSucceeded(TdApi.UpdateMessageSendSucceeded update) {
        SimpleMessage simpleMessage = simpleMessageService.findByMessageSpecId(String.valueOf(update.oldMessageId));
        simpleMessage.setMessageSpecId(String.valueOf(update.message.id));
        simpleMessage.setMessageStatus(MessageStatusEnum.SENT);
        simpleMessageService.save(simpleMessage);
    }

    private static void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        TdApi.AuthorizationState authorizationState = update.authorizationState;

        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            LOG.info("Logged in Telegram");
            TelegramService.isLogined = true;
            TelegramService.sendLoginStatus(true);

        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            LOG.info("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            TelegramService.isLogined = false;
            TelegramService.sendLoginStatus(false);
            LOG.info("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            TelegramService.isLogined = false;
            TelegramService.sendLoginStatus(false);
            LOG.info("Logging out...");
        }
    }

    public static TdApi.User getMe() {
        return client.getMe();
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
                    simpleMessage.setMessenger(MessengerEnum.TELEGRAM);
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

    public static void findUser(SimpleMessage simpleMessage){
        client.send(new TdApi.SearchUserByPhoneNumber("+" + simpleMessage.getReceiverPhone()), user -> {
            CommunicateMethod isSendedCommunicateMethod = null;
            try {
                isSendedCommunicateMethod = TelegramService.communicateMethods.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (user.isError()) {
                if (user.getError().code==404){
                    isSendedCommunicateMethod.setResult(0L);
                    throw new TelegramPhoneNumberNotFoundException("+" + simpleMessage.getReceiverPhone());
                }
            }
            isSendedCommunicateMethod.setResult(user.get().id);
        });
    }

    public static void createChatAndSend(String userId, SimpleMessage simpleMessage) {
        client.send(new TdApi.CreatePrivateChat(Long.parseLong(userId), false), result -> {
            TdApi.Chat chat = result.get();
            sendMessage(chat.id, chat.title, simpleMessage);
        });
    }

    private static void sendMessage(long id, String title, SimpleMessage simpleMessage) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(simpleMessage.getMessage(), null), new TdApi.LinkPreviewOptions(), true);
        client.send(new TdApi.SendMessage(id, 0, new TdApi.InputMessageReplyToMessage(), null, null, content), result -> {
            TdApi.Message sendedMessage = result.get();
            simpleMessage.setMessageSpecId(String.valueOf(sendedMessage.id));
            simpleMessage.setMessenger(MessengerEnum.TELEGRAM);
            simpleMessageService.save(simpleMessage);
            LOG.info("Message: " + simpleMessage.getMessage() + " to " + title + " SENDED using Telegram" + sendedMessage.id);
        });
    }

}
