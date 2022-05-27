package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.*;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;

public final class SpringAuthorizationStateWaitPasswordHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final TelegramClient client;
    private final ClientInteraction clientInteraction;
    private final ExceptionHandler exceptionHandler;

    public SpringAuthorizationStateWaitPasswordHandler(TelegramClient client,
                                                       ClientInteraction clientInteraction,
                                                       ExceptionHandler exceptionHandler) {
        this.client = client;
        this.clientInteraction = clientInteraction;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR) {
            TdApi.AuthorizationStateWaitPassword authorizationState = (TdApi.AuthorizationStateWaitPassword) update.authorizationState;
            ParameterInfo parameterInfo = new ParameterInfoPasswordHint(authorizationState.passwordHint,
                    authorizationState.hasRecoveryEmailAddress,
                    authorizationState.recoveryEmailAddressPattern
            );
            clientInteraction.onParameterRequest(InputParameter.ASK_PASSWORD, parameterInfo, password -> {
                TdApi.CheckAuthenticationPassword response = new TdApi.CheckAuthenticationPassword(password);
                client.send(response, ok -> {
                    if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                        throw new TelegramError((TdApi.Error) ok);
                    }
                }, exceptionHandler);
            });
        }
    }
}
