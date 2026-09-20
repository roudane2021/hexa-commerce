package com.roudane.commerce.common.exceptions;


public class ValidationException extends BusinessException {

    public ValidationException(String errorCode, String message) {
        super(errorCode, message);
    }
}