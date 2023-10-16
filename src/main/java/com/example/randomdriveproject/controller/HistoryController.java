package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class HistoryController {
    @PostMapping("/routes")
    public ResponseEntity<String> saveRoutes(@RequestBody KakaoRouteAllResponseDto requestData) {
        System.out.println(requestData.getTransId());
        return ResponseEntity.ok("Data received successfully"); // 성공 응답
    }
}
