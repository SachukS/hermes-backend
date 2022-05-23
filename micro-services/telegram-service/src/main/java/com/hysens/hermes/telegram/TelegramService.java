package com.hysens.hermes.telegram;

import com.hysens.hermes.common.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class TelegramService implements MessageService {

    @Override
    public boolean sendMessage(String phoneNumber, String message) {
        return false;
    }
}
