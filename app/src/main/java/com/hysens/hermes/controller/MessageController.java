package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import com.hysens.hermes.service.message.MessageServiceFactory;
import com.hysens.hermes.service.message.Messenger;
import com.hysens.hermes.telegram.client.Telegram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public void postMessage(@RequestBody SimpleMessage message) {
        message.setFromMe(true);
        message.setMessageStatus(MessageStatusEnum.PROCESSING);
        message = simpleMessageRepository.save(message);
        LOG.info("---------------------------------------NEW REQUEST-------------------------------------------");

        LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using Telegram if chat with user exists");
        MessageRecipientInfo infoTelegram = new MessageRecipientInfo();
        try {
             infoTelegram = new MessageServiceFactory().from(Messenger.TELEGRAM)
                    .sendIfChatWithUserExists(message);
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
            infoTelegram.setMessageSended(false);
            infoTelegram.setUserExist(false);
            infoTelegram.setChatWithUserExist(false);
        }
//
        MessageRecipientInfo infoWhatsapp = new MessageRecipientInfo();
        if (!infoTelegram.isMessageSended()){
            LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using WhatsApp if chat with user exists");
            try {
            infoWhatsapp = new MessageServiceFactory().from(Messenger.WHATSAPP)
                    .sendIfChatWithUserExists(message);
            } catch (Exception e) {
                LOG.error(e.getMessage());
                infoWhatsapp.setMessageSended(false);
                infoWhatsapp.setUserExist(false);
                infoWhatsapp.setChatWithUserExist(false);
            }
            if (!infoWhatsapp.isMessageSended()){
                if (infoTelegram.isUserExist()){
                    LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using Telegram");
                    new MessageServiceFactory().from(Messenger.TELEGRAM)
                            .sendMessage(infoTelegram.getUserId(), message);
                    infoTelegram.setMessageSended(true);
                } else if (infoWhatsapp.isUserExist()) {
                    LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using WhatsApp");
                    new MessageServiceFactory().from(Messenger.WHATSAPP)
                            .sendMessage(message.getReceiverPhone(), message);
                    infoWhatsapp.setMessageSended(true);
                }
            }

        }
        Client client = clientRepository.findByPhone(message.getReceiverPhone());
        if (infoTelegram.isMessageSended()) {
            client.setTelegramId(Long.parseLong(infoTelegram.getUserId()));
        }
        if (!infoTelegram.isMessageSended() && !infoWhatsapp.isMessageSended()) {
            message.setMessageStatus(MessageStatusEnum.FAILED);
            LOG.error("The user with number: " + message.getReceiverPhone() + " don't have accounts in telegram and whatsapp");
        }
        else {
            if (!client.getMessengers().contains(message.getMessenger())) {
                List<String> exist = new ArrayList<>();
                exist.addAll(client.getMessengers());
                exist.add(message.getMessenger());
                client.setMessengers(exist);
            }
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
