package com.hysens.hermes.telegram.handlers;

import it.tdlight.client.GenericUpdateHandler;
import it.tdlight.client.TDLibSettings;
import it.tdlight.client.TelegramError;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;

public final class SpringAuthorizationStateWaitTdlibParametersHandler implements GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final TelegramClient client;
    private final TDLibSettings settings;
    private final ExceptionHandler exceptionHandler;

    public SpringAuthorizationStateWaitTdlibParametersHandler(TelegramClient client,
                                                              TDLibSettings settings,
                                                              ExceptionHandler exceptionHandler) {
        this.client = client;
        this.settings = settings;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR) {
            TdApi.TdlibParameters params = new TdApi.TdlibParameters();
            params.useTestDc = settings.isUsingTestDatacenter();
            params.databaseDirectory = settings.getDatabaseDirectoryPath().toString();
            params.filesDirectory = settings.getDownloadedFilesDirectoryPath().toString();
            params.useFileDatabase = settings.isFileDatabaseEnabled();
            params.useChatInfoDatabase = settings.isChatInfoDatabaseEnabled();
            params.useMessageDatabase = settings.isMessageDatabaseEnabled();
            params.useSecretChats = false;
            params.apiId = settings.getApiToken().getApiID();
            params.apiHash = settings.getApiToken().getApiHash();
            params.systemLanguageCode = settings.getSystemLanguageCode();
            params.deviceModel = settings.getDeviceModel();
            params.systemVersion = settings.getSystemVersion();
            params.applicationVersion = settings.getApplicationVersion();
            params.enableStorageOptimizer = settings.isStorageOptimizerEnabled();
            params.ignoreFileNames = settings.isIgnoreFileNames();
            client.send(new TdApi.SetTdlibParameters(params), ok -> {
                if (ok.getConstructor() == TdApi.Error.CONSTRUCTOR) {
                    throw new TelegramError((TdApi.Error) ok);
                }
            }, exceptionHandler);
        }
    }
}
