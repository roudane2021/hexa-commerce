package com.roudane.commerce.order.domain.port.out;

import com.roudane.commerce.order.domain.model.User;
import com.roudane.commerce.order.domain.model.UserId;

import java.util.Optional;
import java.util.Set;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UserId id);
    Set<User> findAllUsers();
}