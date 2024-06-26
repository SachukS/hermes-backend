package com.hysens.hermes.whatsapp.listener;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.WhatsAppService;
import it.auties.whatsapp.api.DisconnectReason;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.model.action.Action;
import it.auties.whatsapp.model.chat.Chat;
import it.auties.whatsapp.model.contact.Contact;
import it.auties.whatsapp.model.info.MessageIndexInfo;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.model.MessageStatus;
import it.auties.whatsapp.model.message.standard.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhatsAppListener implements Listener {

    public static SimpleMessageService simpleMessageService;
    public static final Logger LOG = LoggerFactory.getLogger(WhatsAppListener.class);

    public WhatsAppListener(SimpleMessageService simpleMessageService) {
        this.simpleMessageService = simpleMessageService;
    }

    public WhatsAppListener() {
    }


    @Override
    public void onNewMessage(MessageInfo info) {
        if (!(info.message()
                .content() instanceof TextMessage textMessage)) {
            return;
        }
        if (!info.fromMe()) {
            LOG.warn("Received new message: " + textMessage.text() + " from:" + info.senderJid().toPhoneNumber().substring(1));

            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setMessage(textMessage.text());
            simpleMessage.setSenderPhone(info.senderJid().toPhoneNumber().substring(1));
            simpleMessage.setFromMe(false);
            simpleMessage.setMessenger(MessengerEnum.WHATSAPP);
            simpleMessage.setMessageStatus(MessageStatusEnum.NEW);
            simpleMessageService.saveWithoutClientId(simpleMessage, 0L);
        }
        Listener.super.onNewMessage(info);
    }

    @Override
    public void onLoggedIn() {
        LOG.info("Logged in WhatsApp");
        WhatsAppService.isLogined = true;
        WhatsAppService.sendLoginStatus(true);
        Listener.super.onLoggedIn();
    }

    @Override
    public void onDisconnected(DisconnectReason reason) {
        LOG.warn("Whatsapp disconnected.");
        WhatsAppService.isLogined = false;
        WhatsAppService.sendLoginStatus(false);
        Listener.super.onDisconnected(reason);
    }

    @Override
    public void onAnyMessageStatus(Chat chat, Contact contact, MessageInfo info, MessageStatus status) {
        if (status.toString().equals("DELIVERED")) {
            SimpleMessage simpleMessage = simpleMessageService.findByMessageSpecId(info.key().id());
            simpleMessage.setMessageStatus(MessageStatusEnum.SENT);
            simpleMessageService.save(simpleMessage);
        }
        if (status.toString().equals("READ")) {
            SimpleMessage simpleMessage = simpleMessageService.findByMessageSpecId(info.key().id());
            simpleMessage.setMessageStatus(MessageStatusEnum.READ);
            simpleMessageService.save(simpleMessage);
        }
        Listener.super.onAnyMessageStatus(chat, contact, info, status);
    }

    @Override
    public void onAction(Action action, MessageIndexInfo messageIndexInfo) {
        LOG.error(messageIndexInfo.toString());
        Listener.super.onAction(action, messageIndexInfo);
    }

}
