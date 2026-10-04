package com.roudane.commerce.order.infrastructure.config;

import com.roudane.commerce.order.infrastructure.rest.payment.exception.PaymentServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Duration;

@Configuration
public class PaymentClientResilienceConfig {

    private static final Logger log = LoggerFactory.getLogger(PaymentClientResilienceConfig.class);
    public static final String PAYMENT_AUTHORIZE = "paymentAuthorize";

    @Bean
    public Retry paymentAuthorizeRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                // Backoff exponentiel avec jitter : évite que plusieurs instances d'OrderService
                // retentent TOUTES au même instant exact (effet de troupeau / thundering herd)
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        Duration.ofMillis(300), 2.0, 0.3))
                .retryExceptions(
                        PaymentServiceUnavailableException.class

                )
                // Exclusion explicite : même si c'est un sous-type de HttpServerErrorException,
                // un 501 Not Implemented ne doit JAMAIS être retenté
                .ignoreExceptions(
                        HttpServerErrorException.NotImplemented.class
                )
                .failAfterMaxAttempts(true)
                .build();

        Retry retry = registry.retry(PAYMENT_AUTHORIZE, config);

        // Log structuré à CHAQUE tentative — essentiel pour debug en prod
        retry.getEventPublisher().onRetry(event ->
                log.warn("Tentative {} après échec sur {} : {}",
                        event.getNumberOfRetryAttempts(),
                        PAYMENT_AUTHORIZE,
                        event.getLastThrowable().getMessage())
        );

        return retry;
    }

    @Bean
    public CircuitBreaker paymentAuthorizeCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // Fenêtre glissante des 10 derniers appels pour calculer le taux d'échec
                .slidingWindowSize(10)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                // Ouvre le circuit si 50% des appels échouent sur cette fenêtre
                .failureRateThreshold(50)
                // Reste ouvert 30s avant de retenter (état "half-open")
                .waitDurationInOpenState(Duration.ofSeconds(30))
                // En half-open, autorise 3 appels test pour juger si le service est revenu
                .permittedNumberOfCallsInHalfOpenState(3)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .slowCallRateThreshold(50)  // 50% d'appels lents = considéré comme dégradé
                .recordExceptions(
                        PaymentServiceUnavailableException.class
                )
                .build();

        CircuitBreaker circuitBreaker = registry.circuitBreaker(PAYMENT_AUTHORIZE, config);

        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                        log.warn("Circuit breaker {} : transition {} → {}",
                                PAYMENT_AUTHORIZE,
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()));

        return circuitBreaker;
    }

    @Bean
    public TimeLimiter paymentAuthorizeTimeLimiter(TimeLimiterRegistry registry) {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                // Timeout dur par tentative : si PaymentService ne répond pas en 2s, on abandonne CETTE tentative
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        return registry.timeLimiter(PAYMENT_AUTHORIZE, config);
    }
}