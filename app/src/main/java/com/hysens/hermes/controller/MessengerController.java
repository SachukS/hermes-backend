package com.hysens.hermes.controller;

import com.hysens.hermes.service.message.MessageServiceFactory;
import com.hysens.hermes.service.message.Messenger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messenger")
public class MessengerController {

    @PostMapping("/whatsapp/login")
    public void loginWhatsapp() {
        new MessageServiceFactory().from(Messenger.WHATSAPP).loginInMessenger();
    }

    @PostMapping("/telegram/login")
    public void loginTelegram() {
        new MessageServiceFactory().from(Messenger.TELEGRAM).loginInMessenger();
    }
}
