package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.navigation.basic.service.KakaoRouteSearchService;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Route Controller", description = "일반 네비게이션")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class RouteController {

    private final KakaoRouteSearchService kakaoRouteSearchService;

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

//    // 기본 경로 재생성
    @GetMapping("/reroute")
    public ResponseEntity<KakaoRouteAllResponseDto> getReRoute(@RequestParam double lat, double lng) {
        KakaoRouteAllResponseDto response = kakaoRouteSearchService.requestRouteReSearch(lat, lng);

//        PathUtil.PathInfo(response, "RouteController");

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        System.out.println(response.getTransId()); // 체크용
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
