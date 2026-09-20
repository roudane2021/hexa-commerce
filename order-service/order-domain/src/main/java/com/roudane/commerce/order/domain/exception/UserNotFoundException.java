package com.roudane.commerce.order.domain.exception;

import com.roudane.commerce.common.exceptions.NotFoundException;
import com.roudane.commerce.order.domain.model.UserId;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(UserId userId) {
        super("USER_NOT_FOUND", "Utilisateur introuvable : " + userId.value());
    }
}
