package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.user.jwt.JwtUtil;
import com.example.randomdriveproject.user.repository.UserRepository;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
import com.example.randomdriveproject.user.service.RandomUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j(topic = "Home Controller")
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final RandomUserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("message", "안녕하세요!");
        return "index"; // 뷰 이름은 templates 폴더에 있는 템플릿 파일명과 일치해야 합니다.
    }

    @GetMapping("/navigation")
    public String showNavigation() {
        return "navigation";
    }



    @GetMapping("/histories")
    public String showHistories(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return "history/histories";
    }

    @GetMapping("/history/{routeId}")
    public String showHistoryDetail(@PathVariable Long routeId, Model model) {
        model.addAttribute("routeId", routeId);

        return "history/historyDetail";
    }
}
