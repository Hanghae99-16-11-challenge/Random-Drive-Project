//package com.example.randomdriveproject.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class CustomExceptionHandler {
//    @ExceptionHandler({IllegalArgumentException.class})
//    public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
//        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
//        return new ResponseEntity<>(
//                // HTTP body
//                restApiException,
//                // HTTP status code
//                HttpStatus.BAD_REQUEST
//        );
//    }
//    @ExceptionHandler({MethodArgumentNotValidException.class})
//    protected ResponseEntity<RestApiException> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
//        FieldError fieldError = ex.getBindingResult().getFieldError();
//        String errorMessage = fieldError.getDefaultMessage(); // 지정한 메세지
//        RestApiException restApiException = new RestApiException(errorMessage, HttpStatus.BAD_REQUEST.value());
//
//        return new ResponseEntity<>(
//                // HTTP body
//                restApiException,
//                // HTTP status code
//                HttpStatus.BAD_REQUEST
//        );
//    }
//}
