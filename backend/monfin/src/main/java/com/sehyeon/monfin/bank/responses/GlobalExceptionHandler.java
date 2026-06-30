package com.sehyeon.monfin.bank.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sehyeon.monfin.bank.exceptions.UserNotFoundException;

@RestControllerAdvice // applies to all controllers, no imports or wiring needed
public class GlobalExceptionHandler {

    // The controller tries to send a response back to the client and runs into UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponses> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponses(401, e.getMessage()));
    }
    
}
