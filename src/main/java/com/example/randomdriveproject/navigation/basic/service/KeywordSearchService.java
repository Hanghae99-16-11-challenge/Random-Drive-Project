package com.example.randomdriveproject.navigation.basic.service;

import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.service.KakaoKeywordSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "KeywordSearchService")
@Service
@RequiredArgsConstructor
public class KeywordSearchService {

    private final KakaoKeywordSearchService kakaoKeywordSearchService;

    public List<List<String>> requestKeywordRandomWay(String query){
        if (ObjectUtils.isEmpty(query)) {
            return Collections.emptyList();  // 비어 있는 리스트 반환
        }

        KakaoApiResponseDto tourResponses = kakaoKeywordSearchService.requestAttractionKeywordSearch(query);
        // 주소명 로그로 체크
        List<String> tourAddressNames = tourResponses.getDocumentDtoList().stream()
                .map(DocumentDto::getAddressName)
                .collect(Collectors.toList());

        log.info("getDocumentDtoList addressNames : {} ", tourAddressNames.toString());

        // 장소명
        List<String> tourPlaceNames = tourResponses.getDocumentDtoList().stream()
                .map(DocumentDto::getPlaceName)
                .collect(Collectors.toList());
        log.info("getDocumentDtoList placeNames : {} ", tourPlaceNames.toString());

        // 이 두 리스트를 2개의 원소로 가지는 최종 리스트 생성
        List<List<String>> result = new ArrayList<>();
        result.add(tourPlaceNames);
        result.add(tourAddressNames);

        return result;
    }

}
