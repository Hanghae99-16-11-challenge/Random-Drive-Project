package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.user.dto.SignupRequestDto;
import com.example.randomdriveproject.user.dto.StatusResponseDto;
import com.example.randomdriveproject.user.dto.UserInfoDto;
import com.example.randomdriveproject.user.entity.UserRoleEnum;
import com.example.randomdriveproject.user.jwt.JwtUtil;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
import com.example.randomdriveproject.user.service.KakaoService;
import com.example.randomdriveproject.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User Controller", description = "카카오로 로그인 & 회원가입")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @PostMapping("/user/signup")
    @Operation(summary = "회원가입", description = "회원가입 화면을 출력합니다.")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
       return userService.signup(signupRequestDto);
    }


    // 회원 관련 정보 받기
    @GetMapping("/user-info")
    @Operation(summary = "회원 정보 가져오기", description = "회원 정보를 가져옵니다.")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        UserRoleEnum role = userDetails.getUser().getUserRole();
        boolean isAdmin = (role == UserRoleEnum.ADMIN);

        return new UserInfoDto(username, isAdmin);
    }


    @GetMapping("/user/kakao/callback")
    @Operation(summary = "카카오 로그인", description = "카카오로 로그인 합니다.")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7)); // 일단 앞의 Bearer 때어줌
        cookie.setPath("/");
        response.addCookie(cookie); // 브라우저에 자동적으로 setting 됨

        return  "redirect:/api/home"; // 메인 페이지로 보내주기
    }
}