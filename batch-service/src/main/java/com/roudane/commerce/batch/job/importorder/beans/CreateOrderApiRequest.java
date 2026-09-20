package com.roudane.commerce.batch.job.importorder.beans;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderApiRequest(UUID userId, String productId, int quantity, BigDecimal unitPrice) {
}
