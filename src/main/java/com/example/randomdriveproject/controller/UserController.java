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
//https://adjh54.tistory.com/m/72
//http://localhost:8080/swagger-ui/index.html#/
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @GetMapping("/user/login")
    public String login() {
        return "home";
    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

//    @PostMapping("/user/signup")
//    @Operation(summary = "회원가입", description = "회원가입 화면을 출력합니다.")
//    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
//        userService.signup(signupRequestDto);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/user/signup")
    @Operation(summary = "회원가입", description = "회원가입 화면을 출력합니다.")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
       return userService.signup(signupRequestDto);
    }

//    @PostMapping("/user/signup")
//    @Operation(summary = "회원가입", description = "회원가입 화면을 출력합니다.")
//    public ResponseEntity<?> signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            List<String> errors = bindingResult.getFieldErrors().stream()
//                    .map(fieldError -> fieldError.getField() + " 필드: " + fieldError.getDefaultMessage())
//                    .collect(Collectors.toList());
//            return ResponseEntity.badRequest().body(errors); // 오류 메시지를 포함한 400 Bad Request 응답 반환
//        }
//
//        userService.signup(requestDto);
//
//        // 회원가입 성공 응답
//        return ResponseEntity.ok().build();
//    }

//    @PostMapping("/user/signup")
//    @Operation(summary = "회원가입", description = "회원가입 화면을 출력합니다.")
//    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
//        // Validation 예외처리
//        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//        if(fieldErrors.size() > 0) {
//            for (FieldError fieldError : bindingResult.getFieldErrors()) {
//                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
//            }
//            return "redirect:/api/user/signup";
//        }
//
//        userService.signup(requestDto);
//
//        return "redirect:/api/user/login-page";
//    }

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
//
//    @GetMapping("/user-folder")
//    public String getUserInfo(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        // 회원별로 폴더들을 가져와 index.html에 데이터를 전달해줌
//        model.addAttribute("folders", folderService.getFolders(userDetails.getUser()));
//
//        return "index :: #fragment";// 그냥 index.html에 정보 보내는 것
//    }

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