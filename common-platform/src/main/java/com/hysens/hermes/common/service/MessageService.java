package com.hysens.hermes.common.service;

import com.hysens.hermes.common.pojo.MessageRecipientInfo;

public interface MessageService {
    boolean sendMessage(String phoneNumberOrId, String message);

    boolean loginInMessenger(String phoneNumber);

    MessageRecipientInfo sendIfChatWithUserExists(String phoneNumber, String message);
}
