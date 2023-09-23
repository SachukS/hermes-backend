package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import com.hysens.hermes.service.message.MessageRequest;
import com.hysens.hermes.service.message.MessageServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    public static final Logger LOG = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    public SimpleMessageRepository simpleMessageRepository;
    @Autowired
    public ClientRepository clientRepository;

//    @Async("taskExecutor")
    @PostMapping("/send")
    public void postMessage(@RequestBody MessageRequest request) {
        SimpleMessage message = request.getSimpleMessage();
        List<MessengerEnum> messengerPriority = request.getMessengerPriority();

        message.setFromMe(true);
        message.setMessageStatus(MessageStatusEnum.PROCESSING);
        message = simpleMessageRepository.save(message);
        LOG.info("---------------------------------------NEW REQUEST-------------------------------------------");

        LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using Telegram if chat with user exists");
        boolean isMessageSended = false;
        for (MessengerEnum messenger : messengerPriority) {
            System.out.println(messenger);
            isMessageSended = new MessageServiceFactory().from(messenger).sendIfChatWithUserExists(message);
            if (isMessageSended) {
                return;
            }
        }

        Client client = clientRepository.findByPhone(message.getReceiverPhone());

        if (!isMessageSended) {
            message.setMessageStatus(MessageStatusEnum.FAILED);
            LOG.error("The user with number: " + message.getReceiverPhone() + " don't have accounts in telegram and whatsapp");
        }
        client.setLastMessage(message);
        clientRepository.save(client);
    }
    @GetMapping("/load/{id}")
    public List<SimpleMessage> getMessages(@PathVariable("id") long id) {
        return simpleMessageRepository.findAllByClientIdOrderByCreatedDate(id);
    }

    @PostMapping("/read")
    public void setMessageStatusRead(@RequestBody List<SimpleMessage> messages) {
        messages.forEach(message -> message.setMessageStatus(MessageStatusEnum.OPENED));
        simpleMessageRepository.saveAll(messages);
    }
}
