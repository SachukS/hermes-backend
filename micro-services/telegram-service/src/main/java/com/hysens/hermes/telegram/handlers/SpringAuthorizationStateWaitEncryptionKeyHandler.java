package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.client.TelegramError;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;

public final class SpringAuthorizationStateWaitEncryptionKeyHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final TelegramClient client;
    private final ExceptionHandler exceptionHandler;

    public SpringAuthorizationStateWaitEncryptionKeyHandler(TelegramClient client, ExceptionHandler exceptionHandler) {
        this.client = client;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR) {
            client.send(new TdApi.CheckDatabaseEncryptionKey(), ok -> {
                if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                    throw new TelegramError((TdApi.Error) ok);
                }
            }, exceptionHandler);
        }
    }
}