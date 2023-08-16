package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.pojo.Message;
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

import java.util.List;

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
        LOG.info("---------------------------------------NEW REQUEST-------------------------------------------");

        LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using Telegram if chat with user exists");
        MessageRecipientInfo infoTelegram = new MessageServiceFactory().from(Messenger.TELEGRAM)
                .sendIfChatWithUserExists("+" + message.getReceiverPhone(), message.getMessage());
//        MessageRecipientInfo infoTelegram = new MessageRecipientInfo();
//        infoTelegram.setMessageSended(false);
//        infoTelegram.setUserExist(false);
//        infoTelegram.setChatWithUserExist(false);

        MessageRecipientInfo infoWhatsapp = new MessageRecipientInfo();
        if (!infoTelegram.isMessageSended()){
            LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using WhatsApp if chat with user exists");
            infoWhatsapp = new MessageServiceFactory().from(Messenger.WHATSAPP)
                    .sendIfChatWithUserExists(message.getReceiverPhone(), message.getMessage());
            if (!infoWhatsapp.isMessageSended()){
                if (infoTelegram.isUserExist()){
                    LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using Telegram");
                    new MessageServiceFactory().from(Messenger.TELEGRAM)
                            .sendMessage(infoTelegram.getUserId(), message.getMessage());
                    infoTelegram.setMessageSended(true);
                } else if (infoWhatsapp.isUserExist()) {
                    LOG.info("Trying to send message to number: " + message.getReceiverPhone() + " using WhatsApp");
                    new MessageServiceFactory().from(Messenger.WHATSAPP)
                            .sendMessage(message.getReceiverPhone(), message.getMessage());
                    infoWhatsapp.setMessageSended(true);
                }
            }

        }
        Client client = clientRepository.findByPhone(message.getReceiverPhone());
        if (infoTelegram.isMessageSended()) {
            client.setTelegramId(Long.parseLong(infoTelegram.getUserId()));
            message.setMessenger("Telegram");
        }
        if (infoWhatsapp.isMessageSended())
            message.setMessenger("Whatsapp");
        if (!infoTelegram.isMessageSended() && !infoWhatsapp.isMessageSended()) {
            LOG.error("The user with number: " + message.getReceiverPhone() + " don't have accounts in telegram and whatsapp");
        }
        else {
            message.setFromMe(true);
            message.setMessageStatus(MessageStatusEnum.SENT);
            client.setLastMessage(message);
            clientRepository.save(client);
        }

    }
    @GetMapping("/load/{id}")
    public List<SimpleMessage> getMessages(@PathVariable("id") long id) {
        return simpleMessageRepository.findAllByClientIdOrderByCreatedDate(id);
    }

    @PostMapping("/read")
    public void setMessageStatusRead(@RequestBody List<SimpleMessage> messages) {
        messages.forEach(message -> message.setMessageStatus(MessageStatusEnum.READ));
        simpleMessageRepository.saveAll(messages);
    }
}
