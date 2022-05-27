package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.jni.TdApi;

import java.util.concurrent.CountDownLatch;

public final class SpringAuthorizationStateWaitForExit implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final CountDownLatch closed;

    public SpringAuthorizationStateWaitForExit(CountDownLatch closed) {
        this.closed = closed;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateClosed.CONSTRUCTOR) {
            closed.countDown();
        }
    }
}
