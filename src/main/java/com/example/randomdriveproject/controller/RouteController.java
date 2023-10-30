package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.navigation.basic.service.KakaoRouteSearchService;
import com.example.randomdriveproject.navigation.basic.service.KeywordSearchService;
import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.request.service.KakaoKeywordSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Route Controller", description = "일반 네비게이션")
@Controller
@RequiredArgsConstructor

public class RouteController {

    private final KakaoRouteSearchService kakaoRouteSearchService;
    private final KeywordSearchService keywordSearchService;

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

    // 키워드 기반 이름 -> 리스트 -> 도로명 주소로
//    @GetMapping("/keyword-random-route")
//    @Operation(summary = "랜덤경로", description = "목적지와 반경을 설정한 후 랜덤 경로를 가져옵니다.")
//    public ResponseEntity<KakaoApiResponseDto> getRandom(@RequestParam String query) {
//        KakaoApiResponseDto response = keywordSearchService.requestKeywordRandomWay(query);
//
//        if (response == null || response.getDocumentDtoList().isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("/keyword-random-route")
    @Operation(summary = "랜덤경로", description = "목적지와 반경을 설정한 후 랜덤 경로를 가져옵니다.")
    public ResponseEntity<List<List<String>>> getRandom(@RequestParam String query) {
        List<List<String>> response = keywordSearchService.requestKeywordRandomWay(query);

        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
