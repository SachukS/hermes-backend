package com.hysens.hermes.whatsapp.services;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.whatsapp.WhatsAppService;
import com.hysens.hermes.whatsapp.exceptions.NotInMemoryException;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.chat.Chat;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.info.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSender {
    private static Whatsapp api;

    public static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    public MessageSender(Whatsapp api) {
        this.api = api;
    }

    public static SimpleMessage sendMessage(SimpleMessage simpleMessage) {
        String contactJID = simpleMessage.getReceiverPhone() + "@s.whatsapp.net";
        Chat chat = Chat.ofJid(ContactJid.of(contactJID));
        MessageInfo messageInfo = api.sendMessage(chat, simpleMessage.getMessage()).join();
        simpleMessage.setMessageSpecId(messageInfo.key().id());
        simpleMessage.setMessenger(MessengerEnum.WHATSAPP);
        LOG.info("Message: " + simpleMessage.getMessage() + " to " + simpleMessage.getReceiverPhone() + " SENDED using WhatsApp");
        return simpleMessage;
    }

    public static boolean isChatExists(String number) {
        String contactJID = number + "@s.whatsapp.net";
        try {
            boolean isChatExist = !api.store().findChatByJid(ContactJid.of(contactJID))
                    .orElseThrow(() -> new NotInMemoryException("Current chat doesn't exist: " + contactJID)).messages().isEmpty();
            return isChatExist;
        } catch (NotInMemoryException e) {
            return false;

        }
    }

    public static boolean checkIfUserExist(String number) {
        try
        {
            String contactJID = number + "@s.whatsapp.net";
            Chat chat = Chat.ofJid(ContactJid.of(contactJID));
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    public static String logout() {
        api.logout();
        //api.disconnect();
        WhatsAppService.isLogined = false;
        return "Whatsapp logged out";
    }
}