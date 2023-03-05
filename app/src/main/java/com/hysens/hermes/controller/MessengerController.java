package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.service.message.MessageServiceFactory;
import com.hysens.hermes.service.message.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messenger")
public class MessengerController {
    @Autowired
    public ClientRepository clientRepository;
    @Autowired
    public SimpleMessageService simpleMessageService;

    @PostMapping("/whatsapp/login")
    public void loginWhatsapp() {
        new MessageServiceFactory().from(Messenger.WHATSAPP).loginInMessenger(simpleMessageService);
    }

    @PostMapping("/telegram/login")
    public void loginTelegram() {
        new MessageServiceFactory().from(Messenger.TELEGRAM).loginInMessenger(simpleMessageService);
    }

    @PostMapping("/contacts")
    public void addContacts(@RequestBody List<Client> clients) {
        clientRepository.saveAll(clients);
    }

    @GetMapping("/contacts/load")
    public Page<Client> getContacts(
            @RequestParam(required = false) String chatStatus,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String lastMessage,
            @RequestParam(required = false) String clientName
    ) {
        MessageStatusEnum messageStatusEnum = null;
        if (chatStatus != null && !chatStatus.equals("all")) {
            messageStatusEnum = MessageStatusEnum.valueOf(chatStatus.toUpperCase());
        }
        return clientRepository.findAllByCriteria(messageStatusEnum, phone, lastMessage, clientName, pageable);
    }
}
