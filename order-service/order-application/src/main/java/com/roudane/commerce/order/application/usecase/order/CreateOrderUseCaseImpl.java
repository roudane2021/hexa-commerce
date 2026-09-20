package com.roudane.commerce.order.application.usecase.order;

import com.roudane.commerce.common.domain.annotation.LogBusinessAction;
import com.roudane.commerce.order.application.command.CreateOrderCommand;
import com.roudane.commerce.order.application.port.in.order.CreateOrderUseCase;
import com.roudane.commerce.order.domain.exception.UserNotFoundException;
import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderLine;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;
import com.roudane.commerce.order.domain.port.out.UserRepositoryPort;

import java.util.List;

public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;   // port OUT n°1
    private final UserRepositoryPort userRepositoryPort;     // port OUT n°2

    public CreateOrderUseCaseImpl(final OrderRepositoryPort orderRepositoryPort, final UserRepositoryPort userRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @LogBusinessAction(value = "Création commande")
    public Order handle(CreateOrderCommand command) {
        UserId userId = new UserId(command.userId());

        // RG déplacée dans le use case car elle nécessite d'aller chercher un autre aggregate
        // (le domaine Order tout seul ne peut pas savoir si le User existe)
        userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<OrderLine> lines = command.lines().stream()
                .map(l -> new OrderLine(l.productId(), l.quantity(), l.unitPrice()))
                .toList();

        Order order = Order.create(userId, lines); // les RG internes sont vérifiées ici, dans le domaine
        return orderRepositoryPort.save(order);
    }
}