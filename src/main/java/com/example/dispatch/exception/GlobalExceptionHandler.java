package com.example.dispatch.exception;

import com.example.dispatch.exception.DispatchExceptions.DuplicateResourceException;
import com.example.dispatch.exception.DispatchExceptions.InvalidDispatchStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorResponse(String status, String code, String message, Instant timestamp) { }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(this::formatFieldError)
                .orElse("Request validation failed");
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> duplicate(DuplicateResourceException exception) {
        return response(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", exception.getMessage());
    }

    @ExceptionHandler(InvalidDispatchStateException.class)
    public ResponseEntity<ErrorResponse> invalidState(InvalidDispatchStateException exception) {
        return response(HttpStatus.NOT_FOUND, "DISPATCH_STATE_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpected(Exception exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error");
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse("error", code, message, Instant.now()));
    }
}
