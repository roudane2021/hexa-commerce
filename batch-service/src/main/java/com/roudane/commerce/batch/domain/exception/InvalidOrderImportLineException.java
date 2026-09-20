package com.roudane.commerce.batch.domain.exception;

import com.roudane.commerce.common.exceptions.ValidationException;

public class InvalidOrderImportLineException extends ValidationException {

    public InvalidOrderImportLineException(String message) {
        super("ORDER_IMPORT_INVALID_LINE", message);
    }
}
