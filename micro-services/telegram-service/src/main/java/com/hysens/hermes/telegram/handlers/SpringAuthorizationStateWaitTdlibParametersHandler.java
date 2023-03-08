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
        if (update.authorizationState.getConstructor() == 904720988) {
            TdApi.SetTdlibParameters params = new TdApi.SetTdlibParameters();
            params.useTestDc = this.settings.isUsingTestDatacenter();
            params.databaseDirectory = this.settings.getDatabaseDirectoryPath().toString();
            params.filesDirectory = this.settings.getDownloadedFilesDirectoryPath().toString();
            params.useFileDatabase = this.settings.isFileDatabaseEnabled();
            params.useChatInfoDatabase = this.settings.isChatInfoDatabaseEnabled();
            params.useMessageDatabase = this.settings.isMessageDatabaseEnabled();
            params.useSecretChats = false;
            params.apiId = this.settings.getApiToken().getApiID();
            params.apiHash = this.settings.getApiToken().getApiHash();
            params.systemLanguageCode = this.settings.getSystemLanguageCode();
            params.deviceModel = this.settings.getDeviceModel();
            params.systemVersion = this.settings.getSystemVersion();
            params.applicationVersion = this.settings.getApplicationVersion();
            params.enableStorageOptimizer = this.settings.isStorageOptimizerEnabled();
            params.ignoreFileNames = this.settings.isIgnoreFileNames();
            params.databaseEncryptionKey = null;
            this.client.send(params, (ok) -> {
                if (ok.getConstructor() == -1679978726) {
                    throw new TelegramError((TdApi.Error)ok);
                }
            }, this.exceptionHandler);
        }
    }
}
