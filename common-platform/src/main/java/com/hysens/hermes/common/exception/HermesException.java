package com.hysens.hermes.common.exception;

import org.springframework.http.HttpStatus;

public class HermesException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatusCode;

    public HermesException(String code, HttpStatus httpStatusCode) {
        this(code, httpStatusCode.getReasonPhrase(), httpStatusCode);
    }

    public HermesException(String code, String message, HttpStatus httpStatusCode) {
        super(message);
        this.errorCode = code;
        this.httpStatusCode = httpStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

}