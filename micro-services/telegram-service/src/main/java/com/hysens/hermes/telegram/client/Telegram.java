package com.hysens.hermes.telegram.client;

import com.hysens.hermes.common.pojo.MessageRecipientInfo;
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
import java.util.ArrayList;
import java.util.List;

public class Telegram {

    private static final TdApi.MessageSender ADMIN_ID = new TdApi.MessageSenderUser(489214541);
    public static final Logger LOG = LoggerFactory.getLogger(Telegram.class);

    public static final JFrame QRCodeFrame = new JFrame();

    private static SpringTelegramClient client;
    private static List<String> notSended;

    public Telegram() {
        try {
            Init.start();
        } catch (CantLoadLibrary cantLoadLibrary) {
            cantLoadLibrary.printStackTrace();
        }

        // Obtain the API token
        APIToken apiToken = new APIToken(9234724, "990cded7571d97f83502e39b1793b63b");

        // Configure the com.hysens.hermes.telegram.client
        TDLibSettings settings = TDLibSettings.create(apiToken);

        // Configure the session directory
        Path sessionPath = Paths.get("hermes-tdlight-session");
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

        // Create a com.hysens.hermes.telegram.client
        client = new SpringTelegramClient(settings);
        notSended = new ArrayList<>();
//        SpringClientInteraction springClientInteraction =
//                new SpringClientInteraction(SpringTelegramClient.blockingExecutor, client);
//
//        client.setClientInteraction(springClientInteraction);
        // Configure the authentication info
        //ConsoleInteractiveAuthenticationData authenticationData = AuthenticationData.consoleLogin();
//        AuthenticationData authenticationData = AuthenticationData.user(Long.parseLong(phoneNumber));
        AuthenticationData authenticationData = AuthenticationData.qrCode();

        // Add an example update handler that prints when the bot is started
        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, Telegram::onUpdateAuthorizationState);

        // Add an example update handler that prints every received message
        //client.addUpdateHandler(TdApi.UpdateNewMessage.class, Telegram::onUpdateNewMessage);

        // Add an example command handler that stops the bot
        client.addCommandHandler("stop", new StopCommandHandler());

        // Start the com.hysens.hermes.telegram.client
        client.start(authenticationData);

        try {
            client.waitForExit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
        // Get the message content
//        TdApi.MessageContent messageContent = update.message.content;
//
//        // Get the message text
//        String text;
//        if (messageContent instanceof TdApi.MessageText) {
//            // Get the text of the text message
//            text = ((TdApi.MessageText) messageContent).text.text;
//        } else {
//            // We handle only text messages, the other messages will be printed as their type
//            text = String.format("(%s)", messageContent.getClass().getSimpleName());
//        }
//
//        // Get the chat title
//        com.hysens.hermes.telegram.client.send(new TdApi.GetChat(update.message.chatId), chatIdResult -> {
//            // Get the chat response
//            TdApi.Chat chat = chatIdResult.get();
//            // Get the chat name
//            String chatName = chat.title;
//
//            // Print the message
//            System.out.printf("Received new message from chat %s: %s%n", chatName, text);
//        });


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
            sendMessage(Long.parseLong(userId), message, info, isChatExistAndSended);
            info.setChatWithUserExist(true);
        }, Telegram::springHandleResultHandlingException);
    }
    public static void createChatAndSend(String userId, String message) {
        client.send(new TdApi.CreatePrivateChat(Long.parseLong(userId), false), result1 -> {
            sendMessage(result1.get().id, message);
        });
    }

    private static void sendMessage(long id, String message) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        client.send(new TdApi.SendMessage(id, 0, 0, null, null, content), result -> {
            TdApi.Message chat = result.get();
            LOG.info("Message: " + message + " to " + id + " SENDED using Telegram");
        });
    }

    private static void sendMessage(long id, String message, MessageRecipientInfo info, CommunicateMethod communicateMethod) {
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        client.send(new TdApi.SendMessage(id, 0, 0, null, null, content), result -> {
            TdApi.Message chat = result.get();
            info.setMessageSended(true);
            communicateMethod.setResult(info);
            LOG.info("Message: " + message + " to " + id + " SENDED using Telegram");
        });
    }

    private static void springHandleResultHandlingException(Throwable ex) {
        notSended.add(ex.getMessage());
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

