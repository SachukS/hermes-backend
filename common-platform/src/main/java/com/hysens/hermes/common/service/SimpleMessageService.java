package com.hysens.hermes.common.service;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleMessageService {
    public static final Logger LOG = LoggerFactory.getLogger(SimpleMessageService.class);
    @Autowired
    SimpleMessageRepository simpleMessageRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public SimpleMessage findByMessageSpecId(String specId) {
        return simpleMessageRepository.findByMessageSpecId(specId);
    }

    public void save(SimpleMessage simpleMessage) {
        simpleMessageRepository.save(simpleMessage);
        messagingTemplate.convertAndSend("/message", simpleMessage);
    }

    public void saveWithoutClientId (SimpleMessage simpleMessage, long telegramId) {
        Client client;
        if (telegramId == 0L)
            client = clientRepository.findByPhone(simpleMessage.getSenderPhone());
        else
            client = clientRepository.findByTelegramId(telegramId);
        try {
            simpleMessage.setClientId(client.getId());
        } catch (Exception e) {
            LOG.error("Message received from client which isn't in DB");
            return;
        }
        client.setLastMessage(simpleMessage);
//        if (!client.getMessengers().contains(simpleMessage.getMessenger())) {
//            List<MessengerEnum> exist = new ArrayList<>();
//            exist.addAll(client.getMessengers());
//            exist.add(simpleMessage.getMessenger());
//            client.setMessengers(exist);
//        }
        client = clientRepository.save(client);
        messagingTemplate.convertAndSend("/client", client);

    }

    public void setTelegramIdByPhone(long telegramId, String phone) {
        Client client = clientRepository.findByPhone(phone);
        client.setTelegramId(telegramId);
        clientRepository.save(client);
    }

    public void setReadStatusInTelegram(long telegramId) {
        Client client = clientRepository.findByTelegramId(telegramId);
        if (client == null)
            return;
        List<SimpleMessage> messagesOfClient = simpleMessageRepository.findAllByClientIdAndMessageStatus(client.getId(), MessageStatusEnum.SENT);
        if (!messagesOfClient.isEmpty()) {
            messagesOfClient.forEach(simpleMessage -> simpleMessage.setMessageStatus(MessageStatusEnum.READ));
            simpleMessageRepository.saveAll(messagesOfClient);
            messagingTemplate.convertAndSend("/messages/read", messagesOfClient);
        }
    }
}
