package com.roudane.commerce.payment.infrastructure.rest.payment;

import com.roudane.commerce.common.annotation.LogTechnicalCall;
import com.roudane.commerce.payment.application.command.AuthorizePaymentCommand;
import com.roudane.commerce.payment.application.port.in.AuthorizePaymentUseCase;
import com.roudane.commerce.payment.infrastructure.rest.payment.dto.AuthorizePaymentRequest;
import com.roudane.commerce.payment.infrastructure.rest.payment.dto.AuthorizePaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final AuthorizePaymentUseCase authorizePaymentUseCase;

    public PaymentController(AuthorizePaymentUseCase authorizePaymentUseCase) {
        this.authorizePaymentUseCase = authorizePaymentUseCase;
    }

    // Point d'entrée SYNCHRONE appelé par OrderService
    @PostMapping("/authorize")
    @LogTechnicalCall("Controller : authorize payment")
    public ResponseEntity<AuthorizePaymentResponse> authorize(@Valid @RequestBody AuthorizePaymentRequest request) {
        var command = new AuthorizePaymentCommand(request.orderId(), request.amount());
        var payment = authorizePaymentUseCase.handle(command);
        return ResponseEntity.ok(AuthorizePaymentResponse.from(payment));
    }
}
