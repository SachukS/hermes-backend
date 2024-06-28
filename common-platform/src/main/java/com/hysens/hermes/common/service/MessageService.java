package com.hysens.hermes.common.service;

import com.hysens.hermes.common.model.Partner;
import com.hysens.hermes.common.model.SimpleMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface MessageService {
    void initWs(SimpMessagingTemplate messagingTemplate, SimpleMessageService messageService);

    boolean sendMessage(String phoneNumberOrId, SimpleMessage simpleMessage);

    boolean loginInMessenger(Partner partner);

    boolean isMessengerLogined();

    String getQR();

    String logout();

    boolean sendIfChatWithUserExists(SimpleMessage simpleMessage);
}
