package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.*;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;

public final class SpringAuthorizationStateWaitCodeHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final TelegramClient client;
    private final ClientInteraction clientInteraction;
    private final ExceptionHandler exceptionHandler;

    public SpringAuthorizationStateWaitCodeHandler(TelegramClient client,
                                                   ClientInteraction clientInteraction,
                                                   ExceptionHandler exceptionHandler) {
        this.client = client;
        this.clientInteraction = clientInteraction;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitCode.CONSTRUCTOR) {
            TdApi.AuthorizationStateWaitCode authorizationState = (TdApi.AuthorizationStateWaitCode) update.authorizationState;
            ParameterInfo parameterInfo = new ParameterInfoCode(authorizationState.codeInfo.phoneNumber,
                    authorizationState.codeInfo.nextType,
                    authorizationState.codeInfo.timeout,
                    authorizationState.codeInfo.type
            );
            clientInteraction.onParameterRequest(InputParameter.ASK_CODE, parameterInfo, code -> {
                TdApi.CheckAuthenticationCode response = new TdApi.CheckAuthenticationCode(code);
                client.send(response, ok -> {
                    if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                        throw new TelegramError((TdApi.Error) ok);
                    }
                }, exceptionHandler);
            });
        }
    }
}
