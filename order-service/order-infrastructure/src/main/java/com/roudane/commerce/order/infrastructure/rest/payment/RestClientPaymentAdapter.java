package com.roudane.commerce.order.infrastructure.rest.payment;


import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.order.domain.model.OrderId;
import com.roudane.commerce.order.domain.port.out.PaymentClientPort;
import com.roudane.commerce.order.infrastructure.rest.payment.exception.PaymentServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class RestClientPaymentAdapter implements PaymentClientPort {

    private static final Logger log = LoggerFactory.getLogger(RestClientPaymentAdapter.class);

    private final RestClient restClient;
    private final String authorizePath;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public RestClientPaymentAdapter(RestClient paymentServiceRestClient,
                                    @Value("${payment-service.authorize-path:/api/payments/authorize}") String authorizePath,
                                    Retry paymentAuthorizeRetry,
                                    CircuitBreaker paymentAuthorizeCircuitBreaker) {
        this.restClient = paymentServiceRestClient;
        this.authorizePath = authorizePath;
        this.retry = paymentAuthorizeRetry;
        this.circuitBreaker = paymentAuthorizeCircuitBreaker;
        log.info("### Retry injecté : name={}, maxAttempts={}",
                retry.getName(),
                retry.getRetryConfig().getMaxAttempts());
    }

    private record AuthorizeRequest(UUID orderId, BigDecimal amount) {
    }

    private record AuthorizeResponse(UUID paymentId, String status) {
    }

    @Override
    @LogTechnicalCall("Appel synchrone PaymentService authorize")
    public PaymentAuthorizationResult authorize(OrderId orderId, BigDecimal amount) {

        Supplier<PaymentAuthorizationResult> callSupplier = () -> doAuthorize(orderId, amount);

        // Ordre explicite et volontaire : CircuitBreaker À L'EXTÉRIEUR, Retry À L'INTÉRIEUR.
        // Raison : si le circuit est OUVERT, on ne veut PAS retenter 3 fois pour rien —
        // le CircuitBreaker doit intercepter l'appel AVANT que Retry ne s'exécute.
        Supplier<PaymentAuthorizationResult> decorated = Decorators.ofSupplier(callSupplier)
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry)
                .decorate();

        try {
            return decorated.get();

        } catch (CallNotPermittedException e) {
            // Le circuit est OUVERT : on ne tente même pas l'appel réseau
            log.warn("Circuit breaker OUVERT pour PaymentService — rejet immédiat orderId={}", orderId.value());
            return fallback(orderId, e);

        } catch (Exception e) {
            // Toutes les tentatives de retry ont échoué
            log.error("Échec définitif après retries pour orderId={} : {}", orderId.value(), e.getMessage());
            return fallback(orderId, e);
        }
    }

    private PaymentAuthorizationResult doAuthorize(OrderId orderId, BigDecimal amount) {
        try {
            AuthorizeResponse response = restClient.post()
                    .uri(authorizePath)
                    .body(new AuthorizeRequest(orderId.value(), amount))
                    .retrieve()
                    .body(AuthorizeResponse.class);

            boolean accepted = response != null && "AUTHORIZED".equals(response.status());
            return new PaymentAuthorizationResult(accepted, response != null ? response.paymentId().toString() : null);

        } catch (ResourceAccessException e) {
            // Timeout, connexion refusée — transitoire, doit déclencher le retry
            throw new PaymentServiceUnavailableException("PaymentService injoignable", e);
        }
    }

    // Fallback : ne bloque JAMAIS la création de commande — décision métier assumée
    private PaymentAuthorizationResult fallback(OrderId orderId, Throwable throwable) {
        log.error("Fallback déclenché pour orderId={} — raison: {}", orderId.value(), throwable.getClass().getSimpleName());
        return new PaymentAuthorizationResult(false, null);
    }
}