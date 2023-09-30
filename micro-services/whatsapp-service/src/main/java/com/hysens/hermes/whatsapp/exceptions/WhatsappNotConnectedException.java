package com.hysens.hermes.whatsapp.exceptions;

import com.hysens.hermes.common.exception.HermesException;
import org.springframework.http.HttpStatus;

public class WhatsappNotConnectedException extends HermesException {
    public WhatsappNotConnectedException(String code, String message, HttpStatus httpStatusCode) {
        super(code, message, httpStatusCode);
    }
}
