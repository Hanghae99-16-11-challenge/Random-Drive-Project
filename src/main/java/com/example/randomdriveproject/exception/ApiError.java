package com.example.randomdriveproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {
    private HttpStatus status;
    private String message;

    ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
