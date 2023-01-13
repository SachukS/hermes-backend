package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.service.message.MessageServiceFactory;
import com.hysens.hermes.service.message.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<Client> getContacts() {
        return clientRepository.findAll();
    }
}
