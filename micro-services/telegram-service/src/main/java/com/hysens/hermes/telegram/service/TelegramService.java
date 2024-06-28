package com.hysens.hermes.telegram.service;

import com.hysens.hermes.common.model.Partner;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.telegram.client.SpringClientInteraction;
import com.hysens.hermes.telegram.client.Telegram;
import com.hysens.hermes.telegram.config.CommunicateMethod;
import com.hysens.hermes.telegram.exception.TelegramNotConnectedException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;

@Service
public class TelegramService implements MessageService {
    public static SynchronousQueue<CommunicateMethod> communicateMethods;
    public static boolean isLogined = false;
    public static SimpMessagingTemplate messagingTemplate;
    private static SimpleMessageService simpleMessageService;
    @Override
    public void initWs(SimpMessagingTemplate simpMessagingTemplate, SimpleMessageService messageService) {
        messagingTemplate = simpMessagingTemplate;
        simpleMessageService = messageService;
        messagingTemplate.convertAndSend("/messenger/telegram/isLoginned", isLogined);
        new Telegram(messageService);
    }

    public static void sendLoginStatus(boolean loginStatus) {
        messagingTemplate.convertAndSend("/messenger/telegram/isLoginned", loginStatus);
    }

    @Override
    public boolean sendMessage(String userId, SimpleMessage simpleMessage) {
        Telegram.createChatAndSend(userId, simpleMessage);
        return true;
    }

    @Override
    public boolean isMessengerLogined() {
        return isLogined;
    }

    @Override
    public boolean loginInMessenger(Partner partner) {
        Telegram.loginClient(partner);
        return true;
    }

    @Override
    public String logout() {
        return Telegram.logout();
    }

    @Override
    public String getQR() {
        return SpringClientInteraction.qr;
    }

    @Override
    public boolean sendIfChatWithUserExists(SimpleMessage simpleMessage) {
        if (!isMessengerLogined())
            throw new TelegramNotConnectedException("NOT_CONNECTED", "Telegram not connected", HttpStatus.INTERNAL_SERVER_ERROR);
        Telegram.findUser(simpleMessage);
        long userId = 0L;
        communicateMethods = new SynchronousQueue<CommunicateMethod>();
        CommunicateMethod isUserExist = new CommunicateMethod();
        try {
            communicateMethods.put(isUserExist);
            userId = (long) isUserExist.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (userId != 0L) {
            simpleMessageService.setTelegramIdByPhone(userId, simpleMessage.getReceiverPhone());
            sendMessage(String.valueOf(userId), simpleMessage);
            return true;
        }
        return false;
    }
}
