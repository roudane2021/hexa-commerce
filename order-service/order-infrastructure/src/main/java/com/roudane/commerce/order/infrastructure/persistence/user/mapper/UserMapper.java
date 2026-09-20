package com.roudane.commerce.order.infrastructure.persistence.user.mapper;


import com.roudane.commerce.order.domain.model.User;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.infrastructure.persistence.user.entity.UserJpaEntity;

public class UserMapper {

    private UserMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId().value(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                new UserId(entity.getId()),
                entity.getName(),
                entity.getEmail()
        );
    }
}