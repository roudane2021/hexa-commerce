package com.roudane.commerce.common.exceptions;


public class NotFoundException extends BusinessException {

    public NotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
