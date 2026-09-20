package com.roudane.commerce.batch.job.importorder;

import com.roudane.commerce.batch.domain.OrderImportRecordValidator;
import com.roudane.commerce.batch.domain.exception.InvalidOrderImportLineException;
import com.roudane.commerce.batch.job.importorder.beans.CreateOrderApiRequest;
import com.roudane.commerce.batch.job.importorder.beans.OrderImportRecord;
import com.roudane.commerce.common.annotation.LogTechnicalCall;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class OrderImportItemProcessor implements ItemProcessor<OrderImportRecord, CreateOrderApiRequest> {

    @Override
    @LogTechnicalCall("Process batch commandes vers order-service")
    public CreateOrderApiRequest process(OrderImportRecord record) throws Exception {
        try {
            var userId = OrderImportRecordValidator.validateAndExtractUserId(record);
            OrderImportRecordValidator.validateQuantity(record);

            return new CreateOrderApiRequest(userId, record.getProductId(), record.getQuantity(), record.getUnitPrice());

        } catch (InvalidOrderImportLineException e) {
            // Traduit l'exception MÉTIER (domain) vers l'exception TECHNIQUE que Spring Batch sait gérer
            throw new ValidationException(e.getMessage(), e);
        }
    }
}