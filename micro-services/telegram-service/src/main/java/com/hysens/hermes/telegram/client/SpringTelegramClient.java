package com.hysens.hermes.telegram.client;

import com.hysens.hermes.telegram.handlers.*;
import it.tdlight.client.*;
import it.tdlight.common.*;
import it.tdlight.common.internal.CommonClientManager;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.common.utils.LibraryVersion;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SpringTelegramClient implements Authenticable {

    public static final Logger LOG = LoggerFactory.getLogger(SpringTelegramClient.class);
    public static ExecutorService blockingExecutor = Executors.newSingleThreadExecutor();

    static {
        try {
            Init.start();
        } catch (CantLoadLibrary e) {
            throw new RuntimeException("Can't load native libraries", e);
        }
    }

    private final TelegramClient client;
    private ClientInteraction clientInteraction = new SpringClientInteraction(blockingExecutor, this);
    private final TDLibSettings settings;
    private AuthenticationData authenticationData;

    private final Map<String, Set<CommandHandler>> commandHandlers = new ConcurrentHashMap<>();
    private final Set<ResultHandler<TdApi.Update>> updateHandlers = new ConcurrentHashMap<ResultHandler<TdApi.Update>, Object>().keySet(
            new Object());
    private final Set<ExceptionHandler> updateExceptionHandlers = new ConcurrentHashMap<ExceptionHandler, Object>().keySet(
            new Object());
    private final Set<ExceptionHandler> defaultExceptionHandlers = new ConcurrentHashMap<ExceptionHandler, Object>().keySet(
            new Object());

    private final CountDownLatch closed = new CountDownLatch(1);

    public SpringTelegramClient(TDLibSettings settings) {
        this.client = CommonClientManager.create(LibraryVersion.IMPLEMENTATION_NAME);
        this.settings = settings;
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new SpringAuthorizationStateWaitTdlibParametersHandler(client, settings, this::handleDefaultException)
        );
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new SpringAuthorizationStateWaitEncryptionKeyHandler(client, this::handleDefaultException)
        );
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new SpringAuthorizationStateWaitAuthenticationDataHandler(client, this, this::handleDefaultException)
        );
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new SpringAuthorizationStateWaitRegistrationHandler(client,
                        new SpringTelegramClient.SpringTelegramClientInteraction(blockingExecutor),
                        this::handleDefaultException
                )
        );
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new SpringAuthorizationStateWaitPasswordHandler(client,
                        new SpringTelegramClient.SpringTelegramClientInteraction(blockingExecutor),
                        this::handleDefaultException
                )
        );
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new SpringAuthorizationStateWaitOtherDeviceConfirmationHandler(new SpringTelegramClient.SpringTelegramClientInteraction(blockingExecutor))
        );
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new SpringAuthorizationStateWaitCodeHandler(client,
                        new SpringTelegramClient.SpringTelegramClientInteraction(blockingExecutor),
                        this::handleDefaultException
                )
        );
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class, new SpringAuthorizationStateWaitForExit(this.closed));
        AtomicReference<TdApi.User> me = new AtomicReference<>();
        this.addUpdateHandler(TdApi.UpdateAuthorizationState.class, new SpringAuthorizationStateReadyGetMe(client, me));
        this.addUpdateHandler(TdApi.UpdateNewMessage.class, new SpringCommandsHandler(client, this.commandHandlers, me));
    }

    private void handleUpdate(TdApi.Object update) {
        boolean handled = false;
        for (ResultHandler<TdApi.Update> updateHandler : updateHandlers) {
            updateHandler.onResult(update);
            handled = true;
        }
        if (!handled) {
            LOG.warn("An update was not handled, please use addUpdateHandler(handler) before starting the com.hysens.hermes.telegram.client!");
        }
    }

    private void handleUpdateException(Throwable ex) {
        boolean handled = false;
        for (ExceptionHandler updateExceptionHandler : updateExceptionHandlers) {
            updateExceptionHandler.onException(ex);
            handled = true;
        }
        if (!handled) {
            LOG.warn("Error received from Telegram!", ex);
        }
    }

    private void handleDefaultException(Throwable ex) {
        boolean handled = false;
        for (ExceptionHandler exceptionHandler : defaultExceptionHandlers) {
            exceptionHandler.onException(ex);
            handled = true;
        }
        if (!handled) {
            LOG.warn("Unhandled com.hysens.hermes.telegram.exception!", ex);
        }
    }

    private void handleResultHandlingException(Throwable ex) {
        LOG.error("Failed to handle the request result", ex);
    }

    @Override
    public void getAuthenticationData(Consumer<AuthenticationData> result) {
        if (authenticationData instanceof ConsoleInteractiveAuthenticationData) {
            ConsoleInteractiveAuthenticationData consoleInteractiveAuthenticationData
                    = (ConsoleInteractiveAuthenticationData) authenticationData;
            try {
                blockingExecutor.execute(() -> {
                    consoleInteractiveAuthenticationData.askData();
                    result.accept(consoleInteractiveAuthenticationData);
                });
            } catch (RejectedExecutionException | NullPointerException ex) {
                LOG.error("Failed to execute askData. Returning an empty string", ex);
                result.accept(consoleInteractiveAuthenticationData);
            }
        } else {
            result.accept(authenticationData);
        }
    }

    public void setClientInteraction(ClientInteraction clientInteraction) {
        this.clientInteraction = clientInteraction;
    }

    public <T extends TdApi.Update> void addCommandHandler(String commandName, CommandHandler handler) {
        Set<CommandHandler> handlers = this.commandHandlers.computeIfAbsent(commandName,
                k -> new ConcurrentHashMap<CommandHandler, Object>().keySet(new Object())
        );
        handlers.add(handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends TdApi.Update> void addUpdateHandler(Class<T> updateType, GenericUpdateHandler<T> handler) {
        int updateConstructor = ConstructorDetector.getConstructor(updateType);
        this.updateHandlers.add(update -> {
            if (update.getConstructor() == updateConstructor) {
                handler.onUpdate((T) update);
            }
        });
    }

    public void addUpdatesHandler(GenericUpdateHandler<TdApi.Update> handler) {
        this.updateHandlers.add(update -> {
            if (update instanceof TdApi.Update) {
                handler.onUpdate((TdApi.Update) update);
            } else {
                LOG.warn("Unknown update type: {}", update);
            }
        });
    }

    /**
     * Optional handler to handle errors received from TDLib
     */
    public void addUpdateExceptionHandler(ExceptionHandler updateExceptionHandler) {
        this.updateExceptionHandlers.add(updateExceptionHandler);
    }

    /**
     * Optional handler to handle uncaught errors (when using send without an appropriate error handler)
     */
    public void addDefaultExceptionHandler(ExceptionHandler defaultExceptionHandlers) {
        this.defaultExceptionHandlers.add(defaultExceptionHandlers);
    }

    /**
     * Start the com.hysens.hermes.telegram.client
     */
    public void start(AuthenticationData authenticationData) {
        this.authenticationData = authenticationData;
        createDirectories();
        client.initialize(this::handleUpdate, this::handleUpdateException, this::handleDefaultException);
    }

    private void createDirectories() {
        try {
            if (Files.notExists(settings.getDatabaseDirectoryPath())) {
                Files.createDirectories(settings.getDatabaseDirectoryPath());
            }
            if (Files.notExists(settings.getDownloadedFilesDirectoryPath())) {
                Files.createDirectories(settings.getDownloadedFilesDirectoryPath());
            }
        } catch (IOException ex) {
            throw new UncheckedIOException("Can't create TDLight directories", ex);
        }
    }

    /**
     * Send a function and get the result
     */
    public <R extends TdApi.Object> void send(TdApi.Function<R> function, GenericResultHandler<R> resultHandler) {
        client.send(function, result -> resultHandler.onResult(Result.of(result)), this::handleResultHandlingException);
    }

    public <R extends TdApi.Object> void send(TdApi.Function<R> function, GenericResultHandler<R> resultHandler, ExceptionHandler spring) {
        client.send(function, result -> resultHandler.onResult(Result.of(result)), spring);
    }


    public <R extends TdApi.Object> Result<R> execute(TdApi.Function<R> function) {
        return Result.of(client.execute(function));
    }

    /**
     * Send the close signal but don't wait
     */
    public void sendClose() {
        client.send(new TdApi.Close(), ok -> {
            if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                throw new TelegramError((TdApi.Error) ok);
            }
        });
    }

    /**
     * Send the close signal and wait for exit
     */
    public void closeAndWait() throws InterruptedException {
        client.send(new TdApi.Close(), ok -> {
            if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                throw new TelegramError((TdApi.Error) ok);
            }
        });
        this.waitForExit();
    }

    /**
     * Wait until TDLight is closed
     */
    public void waitForExit() throws InterruptedException {
        closed.await();
    }

    private final class SpringTelegramClientInteraction implements ClientInteraction {

        private final ExecutorService blockingExecutor;

        public SpringTelegramClientInteraction(ExecutorService blockingExecutor) {
            this.blockingExecutor = blockingExecutor;
        }

        @Override
        public void onParameterRequest(InputParameter parameter, ParameterInfo parameterInfo, Consumer<String> result) {
            try {
                blockingExecutor.execute(() -> clientInteraction.onParameterRequest(parameter, parameterInfo, result));
            } catch (RejectedExecutionException | NullPointerException ex) {
                LOG.error("Failed to execute onParameterRequest. Returning an empty string", ex);
                result.accept("");
            }
        }
    }
}
