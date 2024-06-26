package com.hysens.hermes.whatsapp.auth;

import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.listener.WhatsAppListener;
import com.hysens.hermes.whatsapp.services.MessageSender;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WhatsAppLogin {

    public static String WhatsappQr = "";

    public static void login(SimpleMessageService simpleMessageService) {
        String homePath = System.getProperty("user.home");
        Path web4jDirectory = Paths.get(homePath + "\\.whatsappweb4j");
        Whatsapp api;

        if (Files.exists(web4jDirectory)) {
            api = Whatsapp.webBuilder()
                    .lastConnection()
                    .registered().get();
        } else {
            api = Whatsapp.webBuilder()
                    .lastConnection()
                    .unregistered(onQRCode());
        }
        api.addListener(new WhatsAppListener(simpleMessageService));
        new MessageSender(api);

        api.connect().getNow(null);
    }

    public static QrHandler onQRCode() {
        return (qr) -> {
            WhatsappQr = qr;
        };
    }
}
