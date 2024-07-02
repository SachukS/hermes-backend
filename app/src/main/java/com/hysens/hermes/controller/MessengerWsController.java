package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.service.message.MessageServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import jakarta.annotation.PostConstruct;
// controller for handling ws (in future)
@Controller
public class MessengerWsController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private SimpleMessageService simpleMessageService;

    @PostConstruct
    public void initWsInMessengers() {
        new MessageServiceFactory().from(MessengerEnum.TELEGRAM).initWs(messagingTemplate, simpleMessageService);
        new MessageServiceFactory().from(MessengerEnum.WHATSAPP).initWs(messagingTemplate, simpleMessageService);
    }
}
