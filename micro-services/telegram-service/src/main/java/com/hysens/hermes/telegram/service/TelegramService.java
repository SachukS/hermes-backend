package com.hysens.hermes.telegram.service;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.telegram.client.Telegram;
import com.hysens.hermes.telegram.config.CommunicateMethod;
import org.springframework.stereotype.Service;

import java.util.concurrent.SynchronousQueue;

@Service
public class TelegramService implements MessageService {

    public static SynchronousQueue<CommunicateMethod> communicateMethods;

    @Override
    public boolean sendMessage(String phoneNumber, String message) {
        Telegram.findUserAndSend(phoneNumber, message);
        communicateMethods = new SynchronousQueue<CommunicateMethod>();
        CommunicateMethod call = new CommunicateMethod();
        try {
            communicateMethods.put(call);
            return (boolean)call.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean loginInMessanger() {
        return false;
    }

    ///ToDo login to telegram client
    public void initAndLogin() {
        Telegram telegram = new Telegram();
    }
}
