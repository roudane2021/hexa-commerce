package com.roudane.commerce.order.config.beans;


import com.roudane.commerce.order.application.port.in.user.GetUserAllUseCase;
import com.roudane.commerce.order.application.usecase.order.CreateOrderUseCaseImpl;
import com.roudane.commerce.order.application.usecase.order.GetOrdersByUserUseCaseImpl;
import com.roudane.commerce.order.application.usecase.user.CreateUserUseCaseImpl;
import com.roudane.commerce.order.application.usecase.user.GetUserAllUseCaseImpl;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;
import com.roudane.commerce.order.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public CreateOrderUseCaseImpl createOrderUseCase(
            OrderRepositoryPort orderRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        return new CreateOrderUseCaseImpl(orderRepositoryPort, userRepositoryPort);
    }

    @Bean
    public GetOrdersByUserUseCaseImpl getOrdersByUserUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrdersByUserUseCaseImpl(orderRepositoryPort);
    }

    @Bean
    public CreateUserUseCaseImpl createUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new CreateUserUseCaseImpl(userRepositoryPort);
    }

    @Bean
    public GetUserAllUseCase getUserAllUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserAllUseCaseImpl(userRepositoryPort);
    }
}

