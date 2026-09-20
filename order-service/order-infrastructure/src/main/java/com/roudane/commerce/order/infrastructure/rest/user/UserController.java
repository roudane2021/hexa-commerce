package com.roudane.commerce.order.infrastructure.rest.user;


import com.roudane.commerce.common.domain.annotation.LogTechnicalCall;
import com.roudane.commerce.order.application.port.in.user.CreateUserUseCase;
import com.roudane.commerce.order.application.port.in.user.GetUserAllUseCase;
import com.roudane.commerce.order.infrastructure.rest.user.dto.CreateUserRequest;
import com.roudane.commerce.order.infrastructure.rest.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserAllUseCase getUserAllUseCase;

    public UserController(final CreateUserUseCase createUserUseCase, final GetUserAllUseCase getUserAllUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserAllUseCase = getUserAllUseCase;
    }

    @PostMapping
    @LogTechnicalCall("Création d'un utilisateur via l'API REST")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var user = createUserUseCase.handle(request.name(), request.email());
        return ResponseEntity.ok(UserResponse.from(user));
    }


    @GetMapping
    @LogTechnicalCall("Récupération de tous les utilisateurs")
    public ResponseEntity<Set<UserResponse>> getAllUser() {
        var users = getUserAllUseCase.handle().stream().map(UserResponse::from).collect(Collectors.toSet());
        return ResponseEntity.ok(users);
    }
}