package com.elec_business.exception;

import com.elec_business.business.exception.BookingNotFoundException;
import com.elec_business.business.exception.InvalidBookingDurationException;
import com.elec_business.service.exception.EmailNotVerifiedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ProblemDetail handleEmailNotVerified(EmailNotVerifiedException ex, HttpServletRequest req) {
        log.warn("Email not verified: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Email not verified", ex.getMessage(), req);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", "Email service unavailable",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Validation error", message, req);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        log.error("Entity Not found", ex);
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Entity not found", ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        log.error("Accces Denied error", ex.getMessage());
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        String message = ex.getMostSpecificCause().getMessage();
        log.error("Unexpected error occurred", ex.getMessage() ,message);
        return buildProblemDetail(HttpStatus.CONFLICT, "Data integrity violation", message, req);
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        log.error("bad request", ex);
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error occurred", ex);
        return buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "Une erreur inattendue est survenue." + ex.getMessage(), req);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ProblemDetail handleBookingNotFoundException(BookingNotFoundException ex,HttpServletRequest req) {
        log.error("BookingNotFoundError occured", ex);
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Booking Not Found",
                "La Réservation est introuvable:" + ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidBookingDurationException.class)
    public ProblemDetail handleInvalidBookingDurationException(InvalidBookingDurationException ex,HttpServletRequest req) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST,"Invalid duration booking" + ex,
                "Il y a une erreur dans la durée de la réservation",req);
    }

    // Utilitaire centralisé pour construire un ProblemDetail
    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail, HttpServletRequest req) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(req.getRequestURI()));
        problem.setProperty("code", status.value());
        return problem;
    }
}
