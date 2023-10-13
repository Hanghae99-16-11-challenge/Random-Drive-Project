package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;

@Slf4j(topic = "User Controller")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String hello(@CookieValue(required = false, value = "Authorization_Access")String access,
                        Model model) throws Exception {
        var user = userService.getUserInfoWithToken(access);

        model.addAttribute("nickname", user.getKakao_account().getProfile().getNickname());
        return "index";
    }

    //https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=0c3c82e2bab1baa630c741b2c9f72e3c&redirect_uri=http://localhost:8080/api/auth/login
    // 위 주소로 접속하면 , 아래 kakaoLogin() 호출
    @GetMapping("/api/auth/login")
    public String loginCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {

        userService.getKakaoLogin(request.getParameter("code"), response);


        return "redirect:/";//ResponseEntity.ok(request.getParameter("code"));
    }
}
