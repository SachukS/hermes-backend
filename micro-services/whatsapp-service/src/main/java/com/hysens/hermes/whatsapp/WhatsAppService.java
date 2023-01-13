package com.hysens.hermes.whatsapp;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.auth.WhatsAppLogin;
import com.hysens.hermes.whatsapp.services.MessageSender;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService implements MessageService {

    @Override
    public boolean sendMessage(String phoneNumber, String message) {
        MessageSender.sendMessage(message, phoneNumber);
        return true;
    }

    @Override
    public boolean loginInMessenger(SimpleMessageService simpleMessageService) {
        WhatsAppLogin.login(simpleMessageService);
        return true;
    }

    @Override
    public MessageRecipientInfo sendIfChatWithUserExists(String phoneNumber, String message) {
        MessageRecipientInfo info = new MessageRecipientInfo();
        if (MessageSender.isChatExists(phoneNumber)) {
            info.setUserExist(true);
            info.setChatWithUserExist(true);
            sendMessage(phoneNumber, message);
            info.setMessageSended(true);
        }
        else {
            if (MessageSender.checkIfUserExist(phoneNumber)) {
                info.setUserExist(true);
                info.setChatWithUserExist(true);
                info.setMessageSended(false);
            }
        }
        return info;
    }
}
