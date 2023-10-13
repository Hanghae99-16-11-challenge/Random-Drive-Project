package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
@Slf4j(topic = "Home Controller")
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    @GetMapping("/")
    public String hello(@CookieValue(required = false, value = "Authorization_Access")String access,
                        Model model,
                        HttpServletResponse response) throws Exception {
        var user = userService.getUserInfoWithToken(access, response);

        model.addAttribute("nickname", user.getKakao_account().getProfile().getNickname());
        return "index";
    }
}
