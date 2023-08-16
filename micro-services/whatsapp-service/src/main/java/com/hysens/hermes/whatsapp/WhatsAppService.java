package com.hysens.hermes.whatsapp;

import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.auth.WhatsAppLogin;
import com.hysens.hermes.whatsapp.services.MessageSender;
import com.hysens.hermes.whatsapp.utils.CommunicateMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;

@Service
public class WhatsAppService implements MessageService {
    public static final Logger LOG = LoggerFactory.getLogger(WhatsAppService.class);
    public static SynchronousQueue<CommunicateMethod> communicateMethods;
    public static boolean isLogined = false;

    @Override
    public boolean sendMessage(String phoneNumber, String message) {
        MessageSender.sendMessage(message, phoneNumber);
        return true;
    }

    @Override
    public boolean isMessengerLogined() {
        return isLogined;
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
            LOG.info("Message: " + message + " to " + phoneNumber + " SENDED using WhatsApp");
            sendMessage(phoneNumber, message);
            info.setMessageSended(true);
        } else {
            if (MessageSender.checkIfUserExist(phoneNumber)) {
                info.setUserExist(true);
                info.setChatWithUserExist(true);
                info.setMessageSended(false);
            }
        }
        return info;
    }
}
