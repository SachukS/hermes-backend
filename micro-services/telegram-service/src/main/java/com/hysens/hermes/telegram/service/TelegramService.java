package com.hysens.hermes.telegram.service;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.telegram.client.SpringClientInteraction;
import com.hysens.hermes.telegram.client.Telegram;
import com.hysens.hermes.telegram.config.CommunicateMethod;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;

@Service
public class TelegramService implements MessageService {
    public static SynchronousQueue<CommunicateMethod> communicateMethods;
    public static boolean isLogined = false;

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
    public boolean loginInMessenger(SimpleMessageService simpleMessageService) {
        new Telegram(simpleMessageService);
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
            sendMessage(String.valueOf(userId), simpleMessage);
            return true;
        }
        return false;
    }
}
