package com.example.dispatch.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
 record ErrorResponse(String status,String code,String message,Instant timestamp) {}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Invalid request");return response(HttpStatus.BAD_REQUEST,"INVALID_REQUEST",m);}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ResponseEntity<ErrorResponse> bad(RuntimeException e){return response(e instanceof IllegalStateException?HttpStatus.NOT_FOUND:HttpStatus.CONFLICT,"INVALID_STATE",e.getMessage());}
 @ExceptionHandler(Exception.class) ResponseEntity<ErrorResponse> unexpected(Exception e){return response(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_ERROR","Unexpected server error");}
 private ResponseEntity<ErrorResponse> response(HttpStatus s,String c,String m){return ResponseEntity.status(s).body(new ErrorResponse("error",c,m,Instant.now()));}
}
