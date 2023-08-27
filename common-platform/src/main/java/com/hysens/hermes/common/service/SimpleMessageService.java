package com.hysens.hermes.common.service;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.ChatStatusEnum;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        client.setLastMessage(simpleMessage);
        if (!client.getMessengers().contains(simpleMessage.getMessenger())) {
            List<String> exist = new ArrayList<>();
            exist.addAll(client.getMessengers());
            exist.add(simpleMessage.getMessenger());
            client.setMessengers(exist);
        }
        clientRepository.save(client);
    }

    public void setTelegramIdByPhone(long telegramId, String phone) {
        Client client = clientRepository.findByPhone(phone);
        client.setTelegramId(telegramId);
        clientRepository.save(client);
    }

}
