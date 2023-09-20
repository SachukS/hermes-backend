package com.hysens.hermes.common.service;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.pojo.MessageRecipientInfo;

public interface MessageService {
    boolean sendMessage(String phoneNumberOrId, SimpleMessage simpleMessage);

    boolean loginInMessenger(SimpleMessageService simpleMessageService);

    boolean isMessengerLogined();

    String getQR();

    String logout();

    MessageRecipientInfo sendIfChatWithUserExists(SimpleMessage simpleMessage);
}
