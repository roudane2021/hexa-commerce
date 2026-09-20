package com.roudane.commerce.payment.config.beans;


import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.payment.application.usecase.*;
import com.roudane.commerce.payment.domain.port.out.PaymentPersistencePort;
import com.roudane.commerce.payment.domain.port.out.PaymentRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public AuthorizePaymentUseCaseImpl authorizePaymentUseCase(PaymentRepositoryPort paymentRepositoryPort) {
        return new AuthorizePaymentUseCaseImpl(paymentRepositoryPort);
    }

    @Bean
    public SettlePaymentUseCaseImpl settlePaymentUseCase(PaymentRepositoryPort paymentRepositoryPort,
                                                         PaymentPersistencePort paymentPersistencePort,
                                                         EventSerializerPort eventSerializerPort) {
        return new SettlePaymentUseCaseImpl(paymentRepositoryPort, paymentPersistencePort, eventSerializerPort);
    }

    @Bean
    public FailPaymentUseCaseImpl failPaymentUseCase(PaymentRepositoryPort paymentRepositoryPort,
                                                     PaymentPersistencePort paymentPersistencePort,
                                                     EventSerializerPort eventSerializerPort) {
        return new FailPaymentUseCaseImpl(paymentRepositoryPort, paymentPersistencePort, eventSerializerPort);
    }
}
