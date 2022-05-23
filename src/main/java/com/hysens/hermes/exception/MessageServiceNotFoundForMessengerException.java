package com.hysens.hermes.exception;

import com.hysens.hermes.common.exception.HermesException;
import com.hysens.hermes.service.message.Messenger;
import org.springframework.http.HttpStatus;

public class MessageServiceNotFoundForMessengerException extends HermesException {

    public MessageServiceNotFoundForMessengerException(Messenger messenger) {
        super("MESSAGE_SERVICE_NOT_FOUND",
                "Message service is not found for messenger: " + messenger.name(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}