package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.Authenticable;
import it.tdlight.client.AuthenticationData;
import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.client.TelegramError;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;

public final class SpringAuthorizationStateWaitAuthenticationDataHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final TelegramClient client;
    private final Authenticable authenticable;
    private final ExceptionHandler exceptionHandler;

    public SpringAuthorizationStateWaitAuthenticationDataHandler(TelegramClient client,
                                                                 Authenticable authenticable,
                                                                 ExceptionHandler exceptionHandler) {
        this.client = client;
        this.authenticable = authenticable;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR) {
            authenticable.getAuthenticationData(this::onAuthData);
        }
    }

    public void onAuthData(AuthenticationData authenticationData) {
        if (authenticationData.isBot()) {
            String botToken = authenticationData.getBotToken();
            TdApi.CheckAuthenticationBotToken response = new TdApi.CheckAuthenticationBotToken(botToken);
            client.send(response, ok -> {
                if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                    throw new TelegramError((TdApi.Error) ok);
                }
            }, exceptionHandler);
        } else if (authenticationData.isQrCode()) {
            TdApi.RequestQrCodeAuthentication response = new TdApi.RequestQrCodeAuthentication();
            client.send(response, ok -> {
                if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                    throw new TelegramError((TdApi.Error) ok);
                }
            }, exceptionHandler);
        } else {
            TdApi.PhoneNumberAuthenticationSettings phoneSettings = new TdApi.PhoneNumberAuthenticationSettings(false, false, false, false, null);

            String phoneNumber = String.valueOf(authenticationData.getUserPhoneNumber());
            TdApi.SetAuthenticationPhoneNumber response = new TdApi.SetAuthenticationPhoneNumber(phoneNumber, phoneSettings);
            client.send(response, ok -> {
                if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                    throw new TelegramError((TdApi.Error) ok);
                }
            }, exceptionHandler);
        }
    }
}