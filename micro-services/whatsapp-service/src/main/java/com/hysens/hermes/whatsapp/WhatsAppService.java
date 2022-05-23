package com.hysens.hermes.whatsapp;

import com.hysens.hermes.common.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService implements MessageService {

    @Override
    public boolean sendMessage(String phoneNumber, String message) {
        return false;
    }

}
