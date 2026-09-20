package com.roudane.commerce.order.infrastructure.persistence.user.entity;


import com.roudane.commerce.common.persistence.BaseJpaEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity  extends BaseJpaEntity {



    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    protected UserJpaEntity() {
        // requis par JPA
    }

    public UserJpaEntity(UUID id, String name, String email) {
        super(id);
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
}
