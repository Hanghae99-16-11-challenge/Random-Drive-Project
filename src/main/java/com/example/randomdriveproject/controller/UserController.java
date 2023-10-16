package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.user.dto.SignupRequestDto;
import com.example.randomdriveproject.user.dto.UserInfoDto;
import com.example.randomdriveproject.user.entity.UserRoleEnum;
import com.example.randomdriveproject.user.jwt.JwtUtil;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
import com.example.randomdriveproject.user.service.KakaoService;
import com.example.randomdriveproject.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
    }

    // 회원 관련 정보 받기
    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        UserRoleEnum role = userDetails.getUser().getRole();
        boolean isAdmin = (role == UserRoleEnum.ADMIN);

        return new UserInfoDto(username, isAdmin);
    }
//
//    @GetMapping("/user-folder")
//    public String getUserInfo(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        // 회원별로 폴더들을 가져와 index.html에 데이터를 전달해줌
//        model.addAttribute("folders", folderService.getFolders(userDetails.getUser()));
//
//        return "index :: #fragment";// 그냥 index.html에 정보 보내는 것
//    }

    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7)); // 일단 앞의 Bearer 때어줌
        cookie.setPath("/");
        response.addCookie(cookie); // 브라우저에 자동적으로 setting 됨

        return  "redirect:/"; // 메인 페이지로 보내주기
    }
}