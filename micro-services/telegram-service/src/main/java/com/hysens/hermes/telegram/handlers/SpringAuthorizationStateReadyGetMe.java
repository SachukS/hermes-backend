package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.client.TelegramError;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public final class SpringAuthorizationStateReadyGetMe implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private static final Logger logger = LoggerFactory.getLogger(SpringAuthorizationStateReadyGetMe.class);

    private final TelegramClient client;
    private final AtomicReference<TdApi.User> me;

    public SpringAuthorizationStateReadyGetMe(TelegramClient client, AtomicReference<TdApi.User> me) {
        this.client = client;
        this.me = me;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateReady.CONSTRUCTOR) {
            client.send(new TdApi.GetMe(), me -> {
                if (me.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                    throw new TelegramError((TdApi.Error) me);
                }
                this.me.set((TdApi.User) me);
            }, error -> logger.warn("Failed to execute TdApi.GetMe()"));
        }
    }
}
