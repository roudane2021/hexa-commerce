package com.roudane.commerce.order.infrastructure.rest.user;


import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.order.application.port.in.user.CreateUserUseCase;
import com.roudane.commerce.order.application.port.in.user.GetUserAllUseCase;
import com.roudane.commerce.order.infrastructure.rest.user.dto.CreateUserRequest;
import com.roudane.commerce.order.infrastructure.rest.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @LogTechnicalCall("Controller : Creation User")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var user = createUserUseCase.handle(request.name(), request.email());
        return ResponseEntity.ok(UserResponse.from(user));
    }


    @GetMapping
    @LogTechnicalCall("Controller : Récuperer Users")
    public ResponseEntity<Set<UserResponse>> getAllUser() {
        var users = getUserAllUseCase.handle().stream().map(UserResponse::from).collect(Collectors.toSet());
        return ResponseEntity.ok(users);
    }
}