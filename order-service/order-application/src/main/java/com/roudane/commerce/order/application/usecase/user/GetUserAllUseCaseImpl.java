package com.roudane.commerce.order.application.usecase.user;

import com.roudane.commerce.order.application.port.in.user.GetUserAllUseCase;
import com.roudane.commerce.order.domain.model.User;
import com.roudane.commerce.order.domain.port.out.UserRepositoryPort;

import java.util.Set;

public class GetUserAllUseCaseImpl implements GetUserAllUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public GetUserAllUseCaseImpl(final UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Set<User> handle() {
        return userRepositoryPort.findAllUsers();
    }
}
