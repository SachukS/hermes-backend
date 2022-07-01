package com.hysens.hermes.telegram.exception;

import com.hysens.hermes.common.exception.HermesException;
import org.springframework.http.HttpStatus;

public class TelegramChatWithUserNotFoundException extends HermesException {
    public TelegramChatWithUserNotFoundException(String message) {
        this("TELEGRAM_UNABLE_TO_FIND_CHAT","Could not found telegram chat with id: "+ message, HttpStatus.NOT_FOUND);
    }

    public TelegramChatWithUserNotFoundException(String code, String message, HttpStatus httpStatusCode) {
        super(code, message, httpStatusCode);
    }
}
