package com.hysens.hermes.whatsapp.services;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.whatsapp.WhatsAppService;
import com.hysens.hermes.whatsapp.exceptions.NotInMemoryException;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.chat.Chat;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.response.HasWhatsappResponse;
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
        MessageInfo messageInfo = api.sendMessage(Jid.of(contactJID), simpleMessage.getMessage()).join();
        simpleMessage.setMessageSpecId(messageInfo.id());
        simpleMessage.setMessenger(MessengerEnum.WHATSAPP);
        LOG.info("Message: " + simpleMessage.getMessage() + " to " + simpleMessage.getReceiverPhone() + " SENDED using WhatsApp");
        return simpleMessage;
    }

    public static boolean isLoginned(String phone) {
        try {
            return api.store().phoneNumber().orElseThrow().toString().equals(phone);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isChatExists(String number) {
        String contactJID = number + "@s.whatsapp.net";
        try {
            boolean isChatExist = !api.store().findChatByJid(Jid.of(contactJID))
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
            HasWhatsappResponse response = api.hasWhatsapp(Jid.of(contactJID)).get();
            return response.hasWhatsapp();
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static String logout() {
        api.logout();
        //api.disconnect();
        WhatsAppService.isLogined = false;
        WhatsAppService.sendLoginStatus(false);
        return "Whatsapp logged out";
    }
}
