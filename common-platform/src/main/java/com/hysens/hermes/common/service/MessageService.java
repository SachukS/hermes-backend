package com.hysens.hermes.common.service;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface MessageService {
    boolean sendMessage(String phoneNumberOrId, SimpleMessage simpleMessage);

    boolean loginInMessenger(SimpleMessageService simpleMessageService);

    boolean isMessengerLogined();

    void initWs(SimpMessagingTemplate messagingTemplate);

    String getQR();

    String logout();

    boolean sendIfChatWithUserExists(SimpleMessage simpleMessage);
}
