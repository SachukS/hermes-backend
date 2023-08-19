package com.hysens.hermes.telegram.service;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.telegram.client.SpringClientInteraction;
import com.hysens.hermes.telegram.client.Telegram;
import com.hysens.hermes.telegram.config.CommunicateMethod;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;

@Service
public class TelegramService implements MessageService {
    public static SynchronousQueue<CommunicateMethod> communicateMethods;
    public static boolean isLogined = false;

    @Override
    public boolean sendMessage(String userId, String message) {
        Telegram.createChatAndSend(userId, message);
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
    public MessageRecipientInfo sendIfChatWithUserExists(String phoneNumber, String message) {
        Telegram.findUser(phoneNumber);
        MessageRecipientInfo info = new MessageRecipientInfo();
        communicateMethods = new SynchronousQueue<CommunicateMethod>();
        CommunicateMethod isUserExist = new CommunicateMethod();
        try {
            communicateMethods.put(isUserExist);
            info = (MessageRecipientInfo) isUserExist.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (info.isUserExist()) {
            Telegram.isChatExist(String.valueOf(info.getUserId()), message, info);
            CommunicateMethod isChatExistAndSended = new CommunicateMethod();
            try {
                communicateMethods.put(isChatExistAndSended);
                info = (MessageRecipientInfo) isChatExistAndSended.getResult();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return info;
    }
}
