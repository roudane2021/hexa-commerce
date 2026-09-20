package com.roudane.commerce.order.infrastructure.persistence.user.adapter;

import com.roudane.commerce.common.domain.annotation.LogTechnicalCall;
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
    @LogTechnicalCall("Sauvegarde utilisateur en base")
    public User save(final User user) {
        UserJpaEntity saved = jpaRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(saved);
    }

    @Override
    @LogTechnicalCall("Récupération utilisateur par ID")
    public Optional<User> findById(final UserId id) {
        return jpaRepository.findById(id.value()).map(UserMapper::toDomain);
    }

    @Override
    @LogTechnicalCall("Récupération de tous les utilisateurs")
    public Set<User> findAllUsers() {
        return jpaRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toSet());
    }
}
