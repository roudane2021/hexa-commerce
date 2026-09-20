package com.roudane.commerce.batch.job.importorder;

import com.roudane.commerce.batch.job.importorder.beans.CreateOrderApiRequest;
import com.roudane.commerce.common.annotation.LogTechnicalCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class OrderImportItemWriter implements ItemWriter<CreateOrderApiRequest> {

    private static final Logger log = LoggerFactory.getLogger(OrderImportItemWriter.class);

    private final RestClient restClient;

    public OrderImportItemWriter(@Value("${order-service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    @LogTechnicalCall("Écriture batch commandes vers order-service")
    public void write(Chunk<? extends CreateOrderApiRequest> chunk) {
        for (CreateOrderApiRequest request : chunk) {
            var body = new CreateOrderHttpBody(
                    request.userId(),
                    List.of(new CreateOrderHttpBody.Line(request.productId(), request.quantity(), request.unitPrice()))
            );

            restClient.post()
                    .uri("/api/orders")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.debug("Commande importée pour userId={}", request.userId());
        }
    }

    private record CreateOrderHttpBody(UUID userId, List<Line> lines) {
        record Line(String productId, int quantity, BigDecimal unitPrice) {
        }
    }
}