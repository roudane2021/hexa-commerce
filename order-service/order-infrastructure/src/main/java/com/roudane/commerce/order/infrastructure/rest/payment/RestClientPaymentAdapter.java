package com.roudane.commerce.order.infrastructure.rest.payment;


import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.order.domain.model.OrderId;
import com.roudane.commerce.order.domain.port.out.PaymentClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class RestClientPaymentAdapter implements PaymentClientPort {

    private final RestClient restClient;
    private final String authorizePath;

    public RestClientPaymentAdapter(RestClient paymentServiceRestClient,
                                    @Value("${payment-service.authorize-path:/api/payments/authorize}") String authorizePath) {
        this.restClient = paymentServiceRestClient;
        this.authorizePath = authorizePath;
    }

    private record AuthorizeRequest(UUID orderId, BigDecimal amount) {
    }

    private record AuthorizeResponse(UUID paymentId, String status) {
    }

    @Override
    @LogTechnicalCall("Controller : authorize payment")
    public PaymentAuthorizationResult authorize(OrderId orderId, BigDecimal amount) {
        AuthorizeResponse response = restClient.post()
                .uri(authorizePath)
                .body(new AuthorizeRequest(orderId.value(), amount))
                .retrieve()
                .body(AuthorizeResponse.class);

        boolean accepted = response != null && "AUTHORIZED".equals(response.status());
        return new PaymentAuthorizationResult(accepted, response != null ? response.paymentId().toString() : null);
    }
}