package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.user.jwt.JwtUtil;
import com.example.randomdriveproject.user.repository.UserRepository;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
//import com.example.randomdriveproject.user.service.RandomUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Home Controller", description = "Controller")
@Slf4j(topic = "Home Controller")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class HomeController {
//    private final RandomUserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("message", "안녕하세요!");
        return "index"; // 뷰 이름은 templates 폴더에 있는 템플릿 파일명과 일치해야 합니다.
    }

    @GetMapping("/search")
    public String search(Model model){
        model.addAttribute("message", "안녕하세요!");
        return "search"; // 뷰 이름은 templates 폴더에 있는 템플릿 파일명과 일치해야 합니다.
    }

    @GetMapping("/home")
    @Operation(summary = "HOME", description = "HOME 화면을 보여줍니다.")
    public String home(Model model){
        model.addAttribute("message", "안녕하세요!");
        return "home"; // 뷰 이름은 templates 폴더에 있는 템플릿 파일명과 일치해야 합니다.
    }

    @GetMapping("/navigation-search")
    @Operation(summary = "기본 네비게이션", description = "네비게이션 화면을 출력합니다.")
    public String searchNavigation() {
        return "search";
    }

    @GetMapping("/navigation-radius")
    @Operation(summary = "반경 네비게이션", description = "네비게이션 화면을 출력합니다.")
    public String radiusNavigation() {
        return "randomradius";
    }

    @GetMapping("/navigation-waypoints")
    @Operation(summary = "목적지 반경 네비게이션", description = "네비게이션 화면을 출력합니다.")
    public String waypointsNavigation() {
        return "randomwaypoints";
    }



    @GetMapping("/histories")
    @Operation(summary = "저장한 경로 조회", description = "저장한 경로를 조회 합니다.")
    public String showHistories(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return "history/histories";
    }

    @GetMapping("/history/{routeId}")
    @Operation(summary = "저장한 경로 선택 조회 ", description = "저장한 경로 중 원하는 경로를 조회합니다.")
    public String showHistoryDetail(@PathVariable Long routeId, Model model) {
        model.addAttribute("routeId", routeId);

        return "history/historyDetail";
    }
}
