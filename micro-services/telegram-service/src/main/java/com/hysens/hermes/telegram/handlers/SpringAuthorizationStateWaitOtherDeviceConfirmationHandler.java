package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.*;
import it.tdlight.jni.TdApi;

public final class SpringAuthorizationStateWaitOtherDeviceConfirmationHandler implements
        GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final ClientInteraction clientInteraction;

    public SpringAuthorizationStateWaitOtherDeviceConfirmationHandler(ClientInteraction clientInteraction) {
        this.clientInteraction = clientInteraction;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR) {
            TdApi.AuthorizationStateWaitOtherDeviceConfirmation authorizationState = (TdApi.AuthorizationStateWaitOtherDeviceConfirmation) update.authorizationState;
            ParameterInfo parameterInfo = new ParameterInfoNotifyLink(authorizationState.link);
            clientInteraction.onParameterRequest(InputParameter.NOTIFY_LINK, parameterInfo, ignored -> {

            });
        }
    }
}

