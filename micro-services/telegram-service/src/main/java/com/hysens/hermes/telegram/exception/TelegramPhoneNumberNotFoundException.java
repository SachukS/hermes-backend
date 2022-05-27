package com.hysens.hermes.telegram.exception;

import com.hysens.hermes.common.exception.HermesException;
import org.springframework.http.HttpStatus;

public class TelegramPhoneNumberNotFoundException extends HermesException {
    public TelegramPhoneNumberNotFoundException(String message) {
        this("TELEGRAM_UNABLE_TO_FIND_USER","Could not found telegram account with number: "+ message, HttpStatus.NOT_FOUND);
    }

    public TelegramPhoneNumberNotFoundException(String code, String message, HttpStatus httpStatusCode) {
        super(code, message, httpStatusCode);
    }
}
