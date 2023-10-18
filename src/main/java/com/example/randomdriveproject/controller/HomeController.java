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

//    @GetMapping("/")
//    public String hello(@CookieValue(required = false, value = "Authorization_Access") String access,
//                        Model model,
//                        HttpServletResponse response) throws Exception {
//        var user = userService.getUserInfoWithToken(access, response);
//
//        model.addAttribute("nickname", user.getKakao_account().getProfile().getNickname());
//        return "index";
//
//    }
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("message", "안녕하세요!");
        return "index"; // 뷰 이름은 templates 폴더에 있는 템플릿 파일명과 일치해야 합니다.
    }

    @GetMapping("/navigation")
    public String showNavigation() {
        return "navigation";
    }

//    @GetMapping("/navigation")
//    public String showNavigation(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String jwtToken = authHeader.substring(7);  // Extract the token from the header
//
//            // Here you should verify the token and authenticate the user
//            // This depends on your specific authentication mechanism
//
//            // If the token is valid, continue to show navigation page
//            if (jwtUtil.validateToken(jwtToken)) {
//                return "navigation";
//            }
//        }
//
//        // If no Authorization header or invalid JWT token, redirect to login page or error page
//        return "redirect:/login";
//    }


    @GetMapping("/basic")
    public String showNavigation(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null && userDetails.getUser() != null) {
            String username = userDetails.getUser().getUsername();
            System.out.println("111여기로 들어옴?");

            // 여기서 사용자의 인증 여부에 따라 처리를 수행할 수 있습니다.
            // 예를 들어, 특정 조건을 만족하면 "navigation" 페이지로 이동하고, 그렇지 않으면 로그인 페이지로 리디렉션합니다.
            // 여기서는 사용자의 인증 여부만 확인하도록 하겠습니다.

            if (username != null) {
                System.out.println("여기로 들어옴?");
                return "navigation";
            }
        }

        // If no user or not authenticated, redirect to login page
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
