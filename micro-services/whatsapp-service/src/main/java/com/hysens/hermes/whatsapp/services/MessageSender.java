package com.hysens.hermes.whatsapp.services;


import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.chat.Chat;
import it.auties.whatsapp.model.contact.ContactJid;

public class MessageSender {
    private static Whatsapp api;

    public MessageSender(Whatsapp api) {
        this.api = api;
    }

    public static void sendMessage(String message, String number) {
        String contactJID = number + "@s.whatsapp.net";
        Chat chat = Chat.ofJid(ContactJid.of(contactJID));
        api.sendMessage(chat, message);
    }
}
