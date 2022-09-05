package com.hysens.hermes.whatsapp.auth;

import com.hysens.hermes.whatsapp.listener.WhatsAppListener;
import com.hysens.hermes.whatsapp.services.MessageSender;
import it.auties.whatsapp.api.HistoryLength;
import it.auties.whatsapp.api.SerializationStrategy;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappOptions;
import it.auties.whatsapp.model.signal.auth.Version;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class WhatsAppLogin {
    public static void login() {
        String homePath = System.getProperty("user.home");
        Path web4jDirectory = Paths.get(homePath + "\\.whatsappweb4j");
        Whatsapp api;

        if (Files.exists(web4jDirectory)) {
            api = Whatsapp.lastConnection();
        }
        else {
//            api = Whatsapp.newConnection();
            WhatsappOptions configuration= WhatsappOptions.newOptions()
                    .version(new Version(2, 2212, 7))
                    .url("wss://web.whatsapp.com/ws/chat")
                    .serialization(true)
                    .historyLength(HistoryLength.THREE_MONTHS)
                    .description("HERMES")
                    .serializationStrategy(SerializationStrategy.periodically(10, true))
                    .serializationStrategies(Set.of(SerializationStrategy.periodically(10, true)))
                    .create();
            api=Whatsapp.newConnection(configuration);
        }

        api.registerListener(new WhatsAppListener());

        new MessageSender(api);

        try {
            api.connect().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
