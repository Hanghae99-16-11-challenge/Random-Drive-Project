package com.example.randomdriveproject.controller;


import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.navigation.random.service.RandomKakaoRouteSearchService;
import com.example.randomdriveproject.util.PathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "RouteController")
public class RandomRouteController {

    private final RandomKakaoRouteSearchService kakaoRouteSearchService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @GetMapping("/random-map")
    public String showMap(Model model) {
        model.addAttribute("kakaoRestApiKey", kakaoRestApiKey);
        return "index";
    }

    @GetMapping("/all-random-route")
    public ResponseEntity<KakaoRouteAllResponseDto> getRandomWays(@RequestParam String originAddress, @RequestParam Integer redius) {
        KakaoRouteAllResponseDto response = kakaoRouteSearchService.requestRandomWays(originAddress,redius);

        PathUtil.PathInfo(response, "RandomRouteController - all-random-route");

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/random-route")
    public ResponseEntity<KakaoRouteAllResponseDto> getRandomWay(@RequestParam String originAddress,@RequestParam String destinationAddress, @RequestParam Integer redius) {
        KakaoRouteAllResponseDto response = kakaoRouteSearchService.requestRamdomWay(originAddress,destinationAddress,redius);

        PathUtil.PathInfo(response , "RandomRouteController - random-route");

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
