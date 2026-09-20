package com.roudane.commerce.order.domain.model;

public class User {

    private final UserId id;
    private final String name;
    private final String email;

    public User(UserId id, String name, String email) {
        // RG : un email doit contenir un @
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email invalide : " + email);
        }
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static User register(String name, String email) {
        return new User(UserId.generate(), name, email);
    }

    public UserId getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
