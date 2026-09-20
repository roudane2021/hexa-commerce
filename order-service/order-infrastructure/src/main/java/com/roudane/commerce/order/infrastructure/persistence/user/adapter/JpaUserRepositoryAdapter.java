package com.roudane.commerce.order.infrastructure.persistence.user.adapter;

import com.roudane.commerce.order.domain.model.User;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.domain.port.out.UserRepositoryPort;
import com.roudane.commerce.order.infrastructure.persistence.user.entity.UserJpaEntity;
import com.roudane.commerce.order.infrastructure.persistence.user.mapper.UserMapper;
import com.roudane.commerce.order.infrastructure.persistence.user.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public JpaUserRepositoryAdapter(final UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(final User user) {
        UserJpaEntity saved = jpaRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(final UserId id) {
        return jpaRepository.findById(id.value()).map(UserMapper::toDomain);
    }

    @Override
    public Set<User> findAllUsers() {
        return jpaRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toSet());
    }
}
