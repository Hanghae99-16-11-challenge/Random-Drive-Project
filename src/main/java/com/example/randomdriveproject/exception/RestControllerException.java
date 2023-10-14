package com.example.randomdriveproject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerException {



    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e)
    {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
