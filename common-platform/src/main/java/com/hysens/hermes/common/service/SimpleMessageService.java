package com.hysens.hermes.common.service;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleMessageService {
    @Autowired
    SimpleMessageRepository simpleMessageRepository;
    @Autowired
    ClientRepository clientRepository;

    public void saveWithoutClientId (SimpleMessage simpleMessage, long telegramId) {
        Client client;
        if (telegramId == 0L)
            client = clientRepository.findByPhone(simpleMessage.getSenderPhone());
        else
            client = clientRepository.findByTelegramId(telegramId);
        simpleMessage.setClientId(client.getId());
        client.setLastMessage(simpleMessage.getMessage());
        client.setLastMessageDate(simpleMessage.getCreatedDate());
        simpleMessageRepository.save(simpleMessage);
        clientRepository.save(client);
    }

    public void setTelegramIdByPhone(long telegramId, String phone) {
        Client client = clientRepository.findByPhone(phone);
        client.setTelegramId(telegramId);
        clientRepository.save(client);
    }

}
