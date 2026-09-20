package com.roudane.commerce.common.web;

import com.roudane.commerce.common.exceptions.BusinessException;
import com.roudane.commerce.common.exceptions.ConflictException;
import com.roudane.commerce.common.exceptions.NotFoundException;
import com.roudane.commerce.common.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
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
public class GlobalExceptionHandler  extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setProperty("errorCode", ex.getErrorCode());
        return problem;
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidation(ValidationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setProperty("errorCode", ex.getErrorCode());
        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setProperty("errorCode", ex.getErrorCode());
        return problem;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleGenericBusiness(BusinessException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setProperty("errorCode", ex.getErrorCode());
        return problem;
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

        //log.warn("Validation échouée sur {} : {} erreur(s)", httpRequest.getRequestURI(), violations.size());

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Un ou plusieurs champs sont invalides", httpRequest);
        problem.setTitle("Requête invalide");
        problem.setProperty("violations", violations);

        return ResponseEntity.badRequest().body(problem);
    }

    // ---------- Validation sur @RequestParam / @PathVariable ----------

//    @ExceptionHandler(ConstraintViolationException.class)
//    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex,
//                                                   HttpServletRequest request) {
//        List<Map<String, String>> violations = ex.getConstraintViolations().stream()
//                .map(v -> Map.of(
//                        "field", v.getPropertyPath().toString(),
//                        "message", v.getMessage()))
//                .toList();
//
//        log.warn("Contrainte violée sur {} : {}", request.getRequestURI(), violations);
//
//        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
//                "Paramètres invalides", request);
//        problem.setProperty("violations", violations);
//
//        return problem;
//    }
//
//    // ---------- Intégrité base de données ----------
//
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex,
//                                             HttpServletRequest request) {
//        log.error("Violation d'intégrité sur {}", request.getRequestURI(), ex);
//
//        return buildProblem(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION",
//                "L'opération viole une contrainte de données", request);
//    }

    // ---------- Filet de sécurité ----------

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        // Un seul appel à buildProblem garantit que le traceId logué et renvoyé est le même
        ProblemDetail problem = buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Une erreur interne est survenue. Contactez le support en citant le traceId.",
                request);

        //log.error("Erreur inattendue [traceId={}] sur {}", problem.getProperties().get("traceId"), request.getRequestURI(), ex);

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