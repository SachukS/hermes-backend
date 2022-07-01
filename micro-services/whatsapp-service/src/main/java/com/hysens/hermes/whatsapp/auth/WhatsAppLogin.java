package com.hysens.hermes.whatsapp.auth;

import com.hysens.hermes.whatsapp.listener.WhatsAppListener;
import com.hysens.hermes.whatsapp.services.MessageSender;
import it.auties.whatsapp.api.Whatsapp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class WhatsAppLogin {
    public static void login() {
        String homePath = System.getProperty("user.home");
        Path web4jDirectory = Paths.get(homePath + "\\.whatsappweb4j");
        Whatsapp api;

        if (Files.exists(web4jDirectory))
            api = Whatsapp.lastConnection();
        else
            api = Whatsapp.newConnection();

        api.registerListener(new WhatsAppListener());
        new MessageSender(api);

        try {
            api.connect().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
