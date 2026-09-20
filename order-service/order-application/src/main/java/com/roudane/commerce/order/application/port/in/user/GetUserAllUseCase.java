package com.roudane.commerce.order.application.port.in.user;


import com.roudane.commerce.order.domain.model.User;

import java.util.Set;

public interface GetUserAllUseCase {
    Set<User> handle();
}
