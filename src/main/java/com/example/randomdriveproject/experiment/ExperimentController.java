package com.example.randomdriveproject.experiment;

import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Experiment Controller", description = "실험용 컨트롤러")
@Controller
@RequiredArgsConstructor
@RequestMapping("/exp")
@Slf4j(topic = "ExperimentController")
public class ExperimentController {
    private final RandomKakaoCategorySearchService randomKakaoCategorySearchService;
    private final KakaoAddressSearchService kakaoAddressSearchService;

    @GetMapping("/coordinate")
    public KakaoApiResponseDto getCoordinate() {
        return kakaoAddressSearchService.requestAddressSearch("경기 군포시 수리산로 244");
    }

    @GetMapping("/attractions")
    public KakaoApiResponseDto getAttractions() {
        return randomKakaoCategorySearchService.requestAttractionCategorySearch(37.566847, 126.994114, 2);
    }
}
