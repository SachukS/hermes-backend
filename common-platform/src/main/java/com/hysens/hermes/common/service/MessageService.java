package com.hysens.hermes.common.service;

import com.hysens.hermes.common.pojo.MessageRecipientInfo;

public interface MessageService {
    boolean sendMessage(String phoneNumberOrId, String message);

    String loginInMessenger(SimpleMessageService simpleMessageService);

    boolean isMessengerLogined();

    MessageRecipientInfo sendIfChatWithUserExists(String phoneNumber, String message);
}
