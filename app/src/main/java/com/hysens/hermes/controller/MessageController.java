package com.hysens.hermes.controller;

import com.hysens.hermes.common.pojo.Message;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import com.hysens.hermes.service.message.MessageServiceFactory;
import com.hysens.hermes.service.message.Messenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    public static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @PostMapping("/send")
    public void postMessage(@RequestBody Message message) {
        LOG.info("---------------------------------------NEW REQUEST-------------------------------------------");
        LOG.info("Trying to send message to number: " + message.getPhoneNumber() + " using Telegram if chat with user exists");
        MessageRecipientInfo infoTelegram = new MessageServiceFactory().from(Messenger.TELEGRAM)
                .sendIfChatWithUserExists("+" + message.getPhoneNumber(), message.getText());

        MessageRecipientInfo infoWhatsapp = new MessageRecipientInfo();
        if (!infoTelegram.isMessageSended()){
            LOG.info("Trying to send message to number: " + message.getPhoneNumber() + " using WhatsApp if chat with user exists");
            infoWhatsapp = new MessageServiceFactory().from(Messenger.WHATSAPP)
                    .sendIfChatWithUserExists(message.getPhoneNumber(), message.getText());
            if (!infoWhatsapp.isMessageSended()){
                if (infoTelegram.isUserExist()){
                    LOG.info("Trying to send message to number: " + message.getPhoneNumber() + " using Telegram");
                    new MessageServiceFactory().from(Messenger.TELEGRAM)
                            .sendMessage(infoTelegram.getUserId(), message.getText());
                    infoTelegram.setMessageSended(true);
                } else if (infoWhatsapp.isUserExist()) {
                    LOG.info("Trying to send message to number: " + message.getPhoneNumber() + " using WhatsApp");
                    new MessageServiceFactory().from(Messenger.WHATSAPP)
                            .sendMessage(message.getPhoneNumber(), message.getText());
                    infoWhatsapp.setMessageSended(true);
                }
            }

        }
        if (!infoTelegram.isMessageSended() && !infoWhatsapp.isMessageSended()) {
            LOG.error("The user with number: " + message.getPhoneNumber() + " don't have accounts in telegram and whatsapp");
        }
    }

}
