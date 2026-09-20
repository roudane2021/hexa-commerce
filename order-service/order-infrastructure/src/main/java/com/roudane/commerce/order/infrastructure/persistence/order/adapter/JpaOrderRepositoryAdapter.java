package com.roudane.commerce.order.infrastructure.persistence.order.adapter;


import com.roudane.commerce.order.domain.model.Order;
import com.roudane.commerce.order.domain.model.OrderId;
import com.roudane.commerce.order.domain.model.UserId;
import com.roudane.commerce.order.domain.port.out.OrderRepositoryPort;
import com.roudane.commerce.order.infrastructure.persistence.order.entity.OrderJpaEntity;
import com.roudane.commerce.order.infrastructure.persistence.order.mapper.OrderMapper;
import com.roudane.commerce.order.infrastructure.persistence.order.repository.OrderJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;

    public JpaOrderRepositoryAdapter(final OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(final Order order) {
        OrderJpaEntity saved = jpaRepository.save(OrderMapper.toEntity(order));
        return OrderMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(final OrderId id) {
        return jpaRepository.findById(id.value()).map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findByUserId(final UserId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(OrderMapper::toDomain)
                .toList();
    }
}
