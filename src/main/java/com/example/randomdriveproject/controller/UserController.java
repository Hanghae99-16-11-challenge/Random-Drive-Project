package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String hello() {
        return "Hello";
    }

    //https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=0c3c82e2bab1baa630c741b2c9f72e3c&redirect_uri=http://localhost:8080/api/auth/login
    // 위 주소로 접속하면 , 아래 kakaoLogin 호출
    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity<String> loginCallback(HttpServletRequest request) throws Exception {
        userService.getKakaoInfo(request.getParameter("code"));
        return ResponseEntity.ok(request.getParameter("code"));
    }//참고 : https://shxrecord.tistory.com/290
}
