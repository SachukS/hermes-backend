package com.hysens.hermes.common.payload.request;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessengerEnum;

import java.util.List;

public class MessageRequest {
    public SimpleMessage message;
    public List<MessengerEnum> messengerPriority;

    public SimpleMessage getSimpleMessage() {
        return message;
    }

    public void setSimpleMessage(SimpleMessage simpleMessage) {
        this.message = simpleMessage;
    }

    public List<MessengerEnum> getMessengerPriority() {
        return messengerPriority;
    }

    public void setMessengerPriority(List<MessengerEnum> messengerPriority) {
        this.messengerPriority = messengerPriority;
    }
}
