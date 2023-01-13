package com.hysens.hermes.whatsapp.auth;

import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.listener.WhatsAppListener;
import com.hysens.hermes.whatsapp.services.MessageSender;
import it.auties.whatsapp.api.HistoryLength;
//import it.auties.whatsapp.api.SerializationStrategy;
import it.auties.whatsapp.api.Whatsapp;
//import it.auties.whatsapp.api.WhatsappOptions;
import it.auties.whatsapp.model.signal.auth.Version;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class WhatsAppLogin {
    public static void login(SimpleMessageService simpleMessageService) {
        String homePath = System.getProperty("user.home");
        Path web4jDirectory = Paths.get(homePath + "\\.whatsappweb4j");
        Whatsapp api;

        if (Files.exists(web4jDirectory)) {
            api = Whatsapp.lastConnection();
        }
        else {
            api = Whatsapp.newConnection();
        }
        //Whatsapp api = Whatsapp.newConnection();
        //Whatsapp api = Whatsapp.lastConnection();
        api.addListener(new WhatsAppListener(simpleMessageService));
        new MessageSender(api);
        api.connect().getNow(null);
    }
}
