package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.dto.kakao_navigation.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.service.RandomkakaoRouteSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RouteController {

    private final RandomkakaoRouteSearchService kakaoRouteSearchService;

    @Value("${kakao.javascript.api.key}")
    private String kakaoJavascriptApiKey;

    @GetMapping("/map")
    public String showMap(Model model) {
        model.addAttribute("kakaoJavascriptApiKey", kakaoJavascriptApiKey);
        return "navigation";
    }

    @GetMapping("/route")
    public ResponseEntity<KakaoRouteAllResponseDto> getRoute(@RequestParam String originAddress,
                                                             @RequestParam String destinationAddress) {
        KakaoRouteAllResponseDto response = kakaoRouteSearchService.requestRouteSearch(originAddress, destinationAddress);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
