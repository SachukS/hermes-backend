package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.*;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;

public final class SpringAuthorizationStateWaitRegistrationHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final TelegramClient client;
    private final ClientInteraction clientInteraction;
    private final ExceptionHandler exceptionHandler;

    public SpringAuthorizationStateWaitRegistrationHandler(TelegramClient client,
                                                           ClientInteraction clientInteraction,
                                                           ExceptionHandler exceptionHandler) {
        this.client = client;
        this.clientInteraction = clientInteraction;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR) {
            TdApi.AuthorizationStateWaitRegistration authorizationState = (TdApi.AuthorizationStateWaitRegistration) update.authorizationState;
            ParameterInfoTermsOfService tos = new ParameterInfoTermsOfService(authorizationState.termsOfService);
            clientInteraction.onParameterRequest(InputParameter.TERMS_OF_SERVICE, tos, ignored -> {
                clientInteraction.onParameterRequest(InputParameter.ASK_FIRST_NAME, new EmptyParameterInfo(), firstName -> {
                    clientInteraction.onParameterRequest(InputParameter.ASK_LAST_NAME, new EmptyParameterInfo(), lastName -> {
                        if (firstName == null || firstName.isEmpty()) {
                            exceptionHandler.onException(new IllegalArgumentException("First name must not be null or empty"));
                            return;
                        }
                        if (firstName.length() > 64) {
                            exceptionHandler.onException(new IllegalArgumentException("First name must be under 64 characters"));
                            return;
                        }
                        if (lastName == null) {
                            exceptionHandler.onException(new IllegalArgumentException("Last name must not be null"));
                            return;
                        }
                        if (lastName.length() > 64) {
                            exceptionHandler.onException(new IllegalArgumentException("Last name must be under 64 characters"));
                            return;
                        }
                        TdApi.RegisterUser response = new TdApi.RegisterUser(firstName, lastName);
                        client.send(response, ok -> {
                            if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                                throw new TelegramError((TdApi.Error) ok);
                            }
                        }, exceptionHandler);
                    });
                });
            });
        }
    }
}