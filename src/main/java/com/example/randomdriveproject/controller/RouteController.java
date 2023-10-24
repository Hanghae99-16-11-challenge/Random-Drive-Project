package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.navigation.basic.service.KakaoRouteSearchService;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Route Controller", description = "일반 네비게이션")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class RouteController {

    private final KakaoRouteSearchService kakaoRouteSearchService;

    @Value("${kakao.javascript.api.key}")
    private String kakaoJavascriptApiKey;

    @Deprecated// 미사용 중 임
    @GetMapping("/map")
    public String showMap(Model model) {
        model.addAttribute("kakaoJavascriptApiKey", kakaoJavascriptApiKey);
        return "navigation";
    }

    @GetMapping("/route")
    public ResponseEntity<KakaoRouteAllResponseDto> getRoute(@RequestParam String originAddress,
                                                             @RequestParam String destinationAddress) {
        KakaoRouteAllResponseDto response = kakaoRouteSearchService.requestRouteSearch(originAddress, destinationAddress);

//        PathUtil.PathInfo(response, "RouteController");

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        System.out.println(response.getTransId()); // 체크용
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
