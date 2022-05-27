package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.CommandHandler;
import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.client.TelegramError;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class SpringCommandsHandler implements GenericUpdateHandler<TdApi.UpdateNewMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SpringCommandsHandler.class);

    private final TelegramClient client;
    private final Map<String, Set<CommandHandler>> commandHandlers;
    private final AtomicReference<TdApi.User> me;

    public SpringCommandsHandler(TelegramClient client,
                                 Map<String, Set<CommandHandler>> commandHandlers,
                                 AtomicReference<TdApi.User> me) {
        this.client = client;
        this.commandHandlers = commandHandlers;
        this.me = me;
    }

    @Override
    public void onUpdate(TdApi.UpdateNewMessage update) {
        if (update.getConstructor() == TdApi.UpdateNewMessage.CONSTRUCTOR) {
            TdApi.Message message = update.message;
            if (message.forwardInfo == null && !message.isChannelPost && (message.authorSignature == null
                    || message.authorSignature.isEmpty()) && message.content.getConstructor() == TdApi.MessageText.CONSTRUCTOR) {
                TdApi.MessageText messageText = (TdApi.MessageText) message.content;
                String text = messageText.text.text;
                if (text.startsWith("/")) {
                    String[] parts = text.split(" ", 2);
                    if (parts.length == 1) {
                        parts = new String[]{parts[0], ""};
                    }
                    if (parts.length == 2) {
                        String currentUnsplittedCommandName = parts[0].substring(1);
                        String arguments = parts[1].trim();
                        String[] commandParts = currentUnsplittedCommandName.split("@", 2);
                        String currentCommandName = null;
                        boolean correctTarget = false;
                        if (commandParts.length == 2) {
                            String myUsername = this.getMyUsername().orElse(null);
                            if (myUsername != null) {
                                currentCommandName = commandParts[0].trim();
                                String currentTargetUsername = commandParts[1];
                                if (myUsername.equalsIgnoreCase(currentTargetUsername)) {
                                    correctTarget = true;
                                }
                            }
                        } else if (commandParts.length == 1) {
                            currentCommandName = commandParts[0].trim();
                            correctTarget = true;
                        }

                        if (correctTarget) {
                            String commandName = currentCommandName;
                            Set<CommandHandler> handlers = commandHandlers.getOrDefault(currentCommandName, Collections.emptySet());

                            for (CommandHandler handler : handlers) {
                                client.send(new TdApi.GetChat(message.chatId), response -> {
                                    if (response.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                                        throw new TelegramError((TdApi.Error) response);
                                    }
                                    handler.onCommand((TdApi.Chat) response, message.senderId, arguments);
                                }, error -> logger.warn("Error when handling the command {}", commandName, error));
                            }
                        }
                    }
                }
            }
        }
    }

    private Optional<String> getMyUsername() {
        TdApi.User user = this.me.get();
        if (user == null || user.username == null || user.username.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(user.username);
        }
    }
}
