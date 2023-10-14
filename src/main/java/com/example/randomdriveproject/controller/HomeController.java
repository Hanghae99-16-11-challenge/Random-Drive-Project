package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.user.service.RandomUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Slf4j(topic = "Home Controller")
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final RandomUserService userService;

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
}
