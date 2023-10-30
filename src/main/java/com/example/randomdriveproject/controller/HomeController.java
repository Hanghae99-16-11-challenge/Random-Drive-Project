package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Home Controller", description = "Controller")
@Slf4j(topic = "Home Controller")
@Controller
@RequiredArgsConstructor
@RequestMapping("/view")
public class HomeController {
    //https://adjh54.tistory.com/m/72
    //http://localhost:8080/swagger-ui/index.html#/

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("message", "안녕하세요!");
        return "index"; // 뷰 이름은 templates 폴더에 있는 템플릿 파일명과 일치해야 합니다.
    }

//    @GetMapping("/user/login")
//    public String login() {
//        return "home";
//    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    } //

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    } //


    @GetMapping("/home") //
    @Operation(summary = "HOME", description = "HOME 화면을 보여줍니다.")
    public String home(Model model){
        model.addAttribute("message", "안녕하세요!");
        return "home"; // 뷰 이름은 templates 폴더에 있는 템플릿 파일명과 일치해야 합니다.
    }

    @GetMapping("/navigation-search") //
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
        return "histories";
    }

    @GetMapping("/navi/{type}/{routeId}/{originAddress}/{destinationAddress}/{redius}")
    @Operation(summary = "네비게이션", description = "네비게이션 화면을 출력합니다.")
    public String showNavi(@PathVariable Long routeId,@PathVariable String type,
                           @PathVariable String originAddress,@PathVariable String destinationAddress,
                           @PathVariable int redius, Model model) {
        model.addAttribute("routeId", routeId);
        model.addAttribute("type", type);
        model.addAttribute("originAddress", originAddress);
        model.addAttribute("destinationAddress", destinationAddress);
        model.addAttribute("redius", redius);
        return "navi";
    }
}
