package com.roudane.commerce.common.web;

import com.roudane.commerce.common.exceptions.BusinessException;
import com.roudane.commerce.common.exceptions.ConflictException;
import com.roudane.commerce.common.exceptions.NotFoundException;
import com.roudane.commerce.common.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.warn("Ressource introuvable [{}] : {}", ex.getErrorCode(), ex.getMessage());
        return buildProblem(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidation(ValidationException ex, HttpServletRequest request) {
        log.warn("Violation de règle métier [{}] : {}", ex.getErrorCode(), ex.getMessage());
        return buildProblem(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex, HttpServletRequest request) {
        log.warn("Conflit d'état [{}] : {}", ex.getErrorCode(), ex.getMessage());
        return buildProblem(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleGenericBusiness(BusinessException ex, HttpServletRequest request) {
        log.error("Erreur métier non catégorisée [{}] : {}", ex.getErrorCode(), ex.getMessage());
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getErrorCode(), ex.getMessage(), request);
    }

    // ---------- Validation @Valid sur @RequestBody ----------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        HttpServletRequest httpRequest = servletWebRequest.getRequest();

        List<Map<String, String>> violations = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", Objects.requireNonNullElse(fe.getDefaultMessage(), "invalide")))
                .toList();

        log.warn("Validation échouée sur {} : {} erreur(s)", httpRequest.getRequestURI(), violations.size());

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Un ou plusieurs champs sont invalides", httpRequest);
        problem.setTitle("Requête invalide");
        problem.setProperty("violations", violations);

        return ResponseEntity.badRequest().body(problem);
    }

    // ---------- Filet de sécurité ----------

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Une erreur interne est survenue. Contactez le support en citant le traceId.",
                request);

        log.error("Erreur inattendue [traceId={}] sur {}", problem.getProperties().get("traceId"),
                request.getRequestURI(), ex);

        return problem;
    }

    // ---------- Fabrique commune ----------

    private ProblemDetail buildProblem(HttpStatus status, String errorCode,
                                       String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", errorCode);
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", currentTraceId());

        return problem;
    }

    private String currentTraceId() {
        return Optional.ofNullable(MDC.get("traceId"))
                .filter(id -> !id.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());
    }
}