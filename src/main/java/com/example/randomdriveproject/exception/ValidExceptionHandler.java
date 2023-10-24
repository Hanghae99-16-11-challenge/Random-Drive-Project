package com.example.randomdriveproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidExceptionHandler {


    // @Valid 유효성 걸렸을 때 MethodArgumentNotValidException 가 발생
    // @NotBlank, @Size, @Digits, @Pattern 등의 검증 어노테이션들은 모두 @Valid에 의해 검증
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            // BindeResult 에서 모든 에러를 가지고 온다.
            // 해당 필드의 이름과 메시지를 담는다.
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
//        return ResponseEntity.badRequest().body(errors);
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errors);
    }
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
//        // 예외 메시지를 그대로 클라이언트에 반환
//        return ResponseEntity.badRequest().body(ex.getMessage());
////        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); // 상태 코드 설정
//    }
}
