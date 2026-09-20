package com.roudane.commerce.order.application.port.in.user;

import com.roudane.commerce.order.domain.model.User;

public interface CreateUserUseCase {
    User handle(String name, String email);
}
