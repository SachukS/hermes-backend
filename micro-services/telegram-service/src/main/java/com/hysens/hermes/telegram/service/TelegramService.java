package com.hysens.hermes.telegram.service;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.telegram.client.Telegram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramService implements MessageService {

    @Override
    public boolean sendMessage(String phoneNumber, String message) {
        Telegram.findUserAndSend(phoneNumber, message);
        return true;
    }

    ///ToDo login to telegram client
    public void initAndLogin() {
        Telegram telegram = new Telegram();
    }
}
