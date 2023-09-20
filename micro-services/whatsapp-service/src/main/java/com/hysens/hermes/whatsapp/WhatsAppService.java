package com.hysens.hermes.whatsapp;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.auth.WhatsAppLogin;
import com.hysens.hermes.whatsapp.services.MessageSender;
import com.hysens.hermes.whatsapp.utils.CommunicateMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;

@Service
public class WhatsAppService implements MessageService {
    public static final Logger LOG = LoggerFactory.getLogger(WhatsAppService.class);
    public static SynchronousQueue<CommunicateMethod> communicateMethods;
    public static boolean isLogined = false;

    @Autowired
    public SimpleMessageService simpleMessageService;

    @Override
    public boolean sendMessage(String phoneNumber, SimpleMessage simpleMessage) {
        simpleMessageService.save(MessageSender.sendMessage(simpleMessage));
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
    public String logout() {
        return MessageSender.logout();
    }

    @Override
    public String getQR() {
        if (!isLogined)
            return WhatsAppLogin.WhatsappQr;
        return "Logged in WhatsApp";
    }
    @Override
    public MessageRecipientInfo sendIfChatWithUserExists(SimpleMessage simpleMessage) {
        MessageRecipientInfo info = new MessageRecipientInfo();
        if (MessageSender.isChatExists(simpleMessage.getReceiverPhone())) {
            info.setUserExist(true);
            info.setChatWithUserExist(true);
            LOG.info("Message: " + simpleMessage.getMessage() + " to " + simpleMessage.getReceiverPhone() + " SENDED using WhatsApp");
            sendMessage(simpleMessage.getReceiverPhone(), simpleMessage);
            info.setMessageSended(true);
        } else {
            if (MessageSender.checkIfUserExist(simpleMessage.getReceiverPhone())) {
                info.setUserExist(true);
                info.setChatWithUserExist(true);
                info.setMessageSended(false);
            }
        }
        return info;
    }
}
