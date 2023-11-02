package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.navigation.random.entity.RouteResult;
import com.example.randomdriveproject.navigation.random.service.RandomAlgorithmsService;
import com.example.randomdriveproject.request.dto.DocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AlgorithmsController {

    private final RandomAlgorithmsService randomAlgorithmsService;

    @GetMapping("/getRoute")
    public ResponseEntity<RouteResult> getRoute(@RequestParam String origin, @RequestParam Integer radius) {
        RouteResult result = randomAlgorithmsService.requestAllRandomWay(origin, radius);
        return ResponseEntity.ok(result);
    }
}
