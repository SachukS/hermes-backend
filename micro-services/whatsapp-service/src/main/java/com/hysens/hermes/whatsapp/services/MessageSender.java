package com.hysens.hermes.whatsapp.services;


import com.hysens.hermes.whatsapp.listener.WhatsAppListener;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.chat.Chat;
import it.auties.whatsapp.model.contact.ContactJid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSender {
    private static Whatsapp api;

    public static final Logger LOG = LoggerFactory.getLogger(WhatsAppListener.class);

    public MessageSender(Whatsapp api) {
        this.api = api;
    }

    public static void sendMessage(String message, String number) {
        String contactJID = number + "@s.whatsapp.net";
        Chat chat = Chat.ofJid(ContactJid.of(contactJID));
        api.sendMessage(chat, message);
        LOG.info("Message: " + message + " to " + number + " SENDED");
    }
}