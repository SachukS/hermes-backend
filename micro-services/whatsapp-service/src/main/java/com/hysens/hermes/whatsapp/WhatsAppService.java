package com.hysens.hermes.whatsapp;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.whatsapp.auth.WhatsAppLogin;
import com.hysens.hermes.whatsapp.services.MessageSender;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WhatsAppService implements MessageService {

    @Override
    public boolean sendMessage(String phoneNumber, String message) {
        MessageSender.sendMessage(message, phoneNumber);
        return true;
    }

    @Override
    public boolean loginInMessanger() {
        WhatsAppLogin.login();
        return true;
    }

}
