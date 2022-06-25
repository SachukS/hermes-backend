package com.hysens.hermes.controller;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.telegram.service.TelegramService;
import com.hysens.hermes.whatsapp.WhatsAppService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    WhatsAppService service = new WhatsAppService();
    @PostMapping("")
    public void postMessage() {

        service.sendMessage("37126824892", "testFromSpring");
    }

    @PostMapping("/login")
    public void login() {
        service.loginInMessanger();
    }

}
