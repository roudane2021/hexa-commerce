package com.roudane.commerce.order.infrastructure.persistence.user.repository;

import com.roudane.commerce.order.infrastructure.persistence.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
}