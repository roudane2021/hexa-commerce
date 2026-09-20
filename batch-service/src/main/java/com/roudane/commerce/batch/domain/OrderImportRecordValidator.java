package com.roudane.commerce.batch.domain;

import com.roudane.commerce.batch.domain.exception.InvalidOrderImportLineException;
import com.roudane.commerce.batch.job.importorder.beans.OrderImportRecord;

import java.util.UUID;

public class OrderImportRecordValidator {

    public static UUID validateAndExtractUserId(OrderImportRecord record) {
        try {
            return UUID.fromString(record.getUserId());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderImportLineException("userId invalide, pas un UUID : " + record.getUserId());
        }
    }

    public static void validateQuantity(OrderImportRecord record) {
        if (record.getQuantity() <= 0) {
            throw new InvalidOrderImportLineException("Quantité invalide : " + record.getQuantity());
        }
    }
}
