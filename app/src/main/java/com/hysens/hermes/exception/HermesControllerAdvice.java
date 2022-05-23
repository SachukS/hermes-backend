package com.hysens.hermes.exception;

import com.hysens.hermes.common.exception.HermesException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HermesControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(HermesException hermesException) {
        ErrorResponse errorResponse = new ErrorResponse(
                hermesException.getErrorCode(),
                hermesException.getMessage());
        return new ResponseEntity<>(errorResponse, hermesException.getHttpStatusCode());
    }

}