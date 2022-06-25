package com.hysens.hermes.whatsapp.auth;

import com.hysens.hermes.whatsapp.listener.WhatsAppListener;
import com.hysens.hermes.whatsapp.services.MessageSender;
import it.auties.whatsapp.api.Whatsapp;

import java.util.concurrent.ExecutionException;

public class WhatsAppLogin {
    public static void login() {
        var api = Whatsapp.lastConnection();
        api.registerListener(new WhatsAppListener());

        new MessageSender(api);
        try {
            api.connect().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
