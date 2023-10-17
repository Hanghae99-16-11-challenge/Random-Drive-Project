package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.history.dto.AllHistoryResponseDto;
import com.example.randomdriveproject.history.dto.HistoryRequestDto;
import com.example.randomdriveproject.history.service.HistoryService;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class HistoryController {
    private final HistoryService historyService;
    @PostMapping("/routes")
    public ResponseEntity<String> saveRoutes(@RequestBody HistoryRequestDto requestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        historyService.saveHistory(requestDto.getRequestData(),
                requestDto.getOriginAddress(), requestDto.getDestinationAddress(),
                userDetails.getUser());
        return ResponseEntity.ok("Data received successfully"); // 성공 응답
    }

    @GetMapping("/routes")
    public ResponseEntity<List<AllHistoryResponseDto>> getAllRoutes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<AllHistoryResponseDto> historyList = historyService.getAllHistories(userDetails.getUser().getId());
        return ResponseEntity.ok(historyList);
    }
}
