package com.roudane.commerce.order.infrastructure.rest.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "name est obligatoire")
        String name,

        @NotBlank(message = "email est obligatoire")
        @Email(message = "email invalide")
        String email
) {
}