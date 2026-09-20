package com.roudane.commerce.order.application.usecase.user;



import com.roudane.commerce.order.application.port.in.user.CreateUserUseCase;
import com.roudane.commerce.order.domain.model.User;
import com.roudane.commerce.order.domain.port.out.UserRepositoryPort;

public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public CreateUserUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User handle(String name, String email) {
        User user = User.register(name, email);
        return userRepositoryPort.save(user);
    }
}
