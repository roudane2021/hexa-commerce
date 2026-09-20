package com.roudane.commerce.common.exceptions;


public class ConflictException extends BusinessException {

    public ConflictException(String errorCode, String message) {
        super(errorCode, message);
    }
}
