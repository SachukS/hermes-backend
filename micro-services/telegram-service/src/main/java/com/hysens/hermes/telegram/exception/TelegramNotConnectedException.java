package com.hysens.hermes.telegram.exception;

import com.hysens.hermes.common.exception.HermesException;
import org.springframework.http.HttpStatus;

public class TelegramNotConnectedException extends HermesException {

    public TelegramNotConnectedException(String code, String message, HttpStatus httpStatusCode) {
        super(code, message, httpStatusCode);
    }
}
