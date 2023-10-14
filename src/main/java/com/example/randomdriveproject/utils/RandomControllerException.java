package com.example.randomdriveproject.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RandomControllerException {



    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e)
    {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
