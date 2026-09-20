package com.roudane.commerce.order.infrastructure.rest.user.dto;

import com.roudane.commerce.order.domain.model.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId().value(), user.getName(), user.getEmail());
    }
}
