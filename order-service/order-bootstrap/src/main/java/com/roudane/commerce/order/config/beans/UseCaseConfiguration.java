package com.roudane.commerce.order.config.beans;


import com.roudane.commerce.common.messaging.serialization.EventSerializerPort;
import com.roudane.commerce.order.application.port.in.order.CancelOrderUseCase;
import com.roudane.commerce.order.application.port.in.order.ConfirmOrderUseCase;
import com.roudane.commerce.order.application.port.in.order.CreateOrderUseCase;
import com.roudane.commerce.order.application.port.in.order.GetOrdersByUserUseCase;
import com.roudane.commerce.order.application.port.in.user.CreateUserUseCase;
import com.roudane.commerce.order.application.port.in.user.GetUserAllUseCase;
import com.roudane.commerce.order.application.usecase.order.CancelOrderUseCaseImpl;
import com.roudane.commerce.order.application.usecase.order.ConfirmOrderUseCaseImpl;
import com.roudane.commerce.order.application.usecase.order.CreateOrderUseCaseImpl;
import com.roudane.commerce.order.application.usecase.order.GetOrdersByUserUseCaseImpl;
import com.roudane.commerce.order.application.usecase.user.CreateUserUseCaseImpl;
import com.roudane.commerce.order.application.usecase.user.GetUserAllUseCaseImpl;
import com.roudane.commerce.order.domain.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepositoryPort orderRepositoryPort,
            UserRepositoryPort userRepositoryPort, PaymentClientPort paymentClientPort,
            EventSerializerPort eventSerializerPort, OrderPersistencePort orderPersistencePort) {

        return new CreateOrderUseCaseImpl(orderRepositoryPort, userRepositoryPort, paymentClientPort, eventSerializerPort, orderPersistencePort);
    }



    @Bean
    public GetOrdersByUserUseCase getOrdersByUserUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrdersByUserUseCaseImpl(orderRepositoryPort);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new CreateUserUseCaseImpl(userRepositoryPort);
    }

    @Bean
    public GetUserAllUseCase getUserAllUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserAllUseCaseImpl(userRepositoryPort);
    }

    @Bean
    public ConfirmOrderUseCase confirmOrderUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new ConfirmOrderUseCaseImpl(orderRepositoryPort);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new CancelOrderUseCaseImpl(orderRepositoryPort);
    }
}

