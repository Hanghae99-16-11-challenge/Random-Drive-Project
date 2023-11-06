package com.example.randomdriveproject.controller;

import com.example.randomdriveproject.history.dto.AllHistoryResponseDto;
import com.example.randomdriveproject.history.dto.HistoryRequestDto;
import com.example.randomdriveproject.history.dto.HistoryResponseDto;
import com.example.randomdriveproject.history.service.HistoryService;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "History Controller", description = "경로 저장 및 조회")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@ControllerAdvice
public class HistoryController {
    private final HistoryService historyService;
    @PostMapping("/routes")
    @Operation(summary = "경로 저장", description = "경로를 저장합니다.")
    public ResponseEntity<String> saveRoutes(@RequestBody HistoryRequestDto requestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        long startTime = System.currentTimeMillis();
        historyService.saveHistory(requestDto.getRequestData(),
                requestDto.getOriginAddress(), requestDto.getDestinationAddress(), requestDto.getMapType(),
                userDetails.getUser());
        long endTime = System.currentTimeMillis();
        System.out.println("\n\n\n\n\n\n\n실행 시간" + (endTime - startTime));
        return ResponseEntity.ok("Data received successfully"); // 성공 응답
    }

    @GetMapping("/routes")
    @Operation(summary = "경로 조회", description = "저장한 경로를 전체 조회 합니다.")
    public ResponseEntity<List<AllHistoryResponseDto>> getAllRoutes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<AllHistoryResponseDto> historyList = historyService.getAllHistories(userDetails.getUser().getId());
        return ResponseEntity.ok(historyList);
    }

    @GetMapping("/route/{routeId}")
    @Operation(summary = "선택한 경로 조회", description = "저장한 경로를 선택하여 조회합니다.")
    public ResponseEntity<HistoryResponseDto> getRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(historyService.getHistory(routeId));
    }
}
