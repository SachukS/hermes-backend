package com.hysens.hermes.common.service;

import com.hysens.hermes.common.pojo.MessageRecipientInfo;

public interface MessageService {
    boolean sendMessage(String phoneNumberOrId, String message);

    boolean loginInMessenger(SimpleMessageService simpleMessageService);

    MessageRecipientInfo sendIfChatWithUserExists(String phoneNumber, String message);
}
