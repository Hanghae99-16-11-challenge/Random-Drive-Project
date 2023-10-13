package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;


    //https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=0c3c82e2bab1baa630c741b2c9f72e3c&redirect_uri=http://localhost:8080/api/auth/login
    // 위 주소로 접속하면 , 아래 kakaoLogin() 호출
    @GetMapping("/login")
    public String loginCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {

        userService.getKakaoLogin(request.getParameter("code"), response);

        response.sendRedirect("/");
        return "redirect:/";//ResponseEntity.ok(request.getParameter("code"));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = UserService.AccessToken)String access,
                                         HttpServletResponse response) throws Exception {
        userService.doLogout(access, true , response);

        return ResponseEntity.ok("Logout");
    }

    @GetMapping("/regenerate")
    public ResponseEntity<String> regenerateToken(@CookieValue(value = UserService.RefreshToken)String refresh,
                                                  HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        userService.getAccessFormRefresh(refresh, response);

        return ResponseEntity.ok("Regenerate Token");
    }//액세스 토큰 재발급

}
