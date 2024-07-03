package com.hysens.hermes.whatsapp;

import com.hysens.hermes.common.model.Partner;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.auth.WhatsAppLogin;
import com.hysens.hermes.whatsapp.exceptions.WhatsappNotConnectedException;
import com.hysens.hermes.whatsapp.services.MessageSender;
import com.hysens.hermes.whatsapp.utils.CommunicateMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;

import static com.hysens.hermes.whatsapp.listener.WhatsAppListener.simpleMessageService;

@Service
public class WhatsAppService implements MessageService {
    public static final Logger LOG = LoggerFactory.getLogger(WhatsAppService.class);
    public static SynchronousQueue<CommunicateMethod> communicateMethods;
    public static boolean isLogined = false;

    public static SimpMessagingTemplate messagingTemplate;

    @Override
    public void initWs(SimpMessagingTemplate messagingTemplate, SimpleMessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        simpleMessageService = messageService;
        messagingTemplate.convertAndSend("/messenger/whatsapp/isLoginned", isLogined);
    }

    public static void sendLoginStatus(boolean loginStatus) {
        messagingTemplate.convertAndSend("/messenger/whatsapp/isLoginned", loginStatus);
    }

    @Override
    public boolean sendMessage(String phoneNumber, SimpleMessage simpleMessage) {
        simpleMessageService.save(MessageSender.sendMessage(simpleMessage));
        return true;
    }

    @Override
    public boolean isMessengerLogined(String partnerPhone) {
        return MessageSender.isLoginned(partnerPhone);
    }

    @Override
    public boolean loginInMessenger(Partner partner) {
        WhatsAppLogin.login(simpleMessageService, partner);
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
    public boolean sendIfChatWithUserExists(SimpleMessage simpleMessage) {
        if (!isLogined)
            throw new WhatsappNotConnectedException("WHATSAPP_NOT_CONNECTED", "Whatsapp not connected", HttpStatus.INTERNAL_SERVER_ERROR);
        MessageRecipientInfo info = new MessageRecipientInfo();
        if (MessageSender.isChatExists(simpleMessage.getReceiverPhone())) {
            sendMessage(simpleMessage.getReceiverPhone(), simpleMessage);
            return true;
        } else {
            if (MessageSender.checkIfUserExist(simpleMessage.getReceiverPhone())) {
                sendMessage(simpleMessage.getReceiverPhone(), simpleMessage);
                return true;
            }
        }
        return false;
    }
}
