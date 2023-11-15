package com.example.randomdriveproject.navigation.basic.service;

import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.service.KakaoKeywordSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "KeywordSearchService")
@Service
@RequiredArgsConstructor
public class KeywordSearchService {

    private final KakaoKeywordSearchService kakaoKeywordSearchService;

    public List<List<String>> requestKeywordRandomWay(String query){

        // 검색 키워드에 대한 유효성 검사 추가
        if (StringUtils.isEmpty(query) || query.length() < 2) {
            throw new IllegalArgumentException("검색 키워드는 2글자 이상이어야 합니다.");
        }

        KakaoApiResponseDto tourResponses = kakaoKeywordSearchService.requestAttractionKeywordSearch(query);
        // 외부 API 응답 유효성 검사 추가
        if (tourResponses == null || tourResponses.getDocumentDtoList() == null) {
            throw new RuntimeException("외부 API 응답 처리 중 오류가 발생하였습니다.");
        }

        // 주소명
        List<String> tourAddressNames = tourResponses.getDocumentDtoList().stream()
                .map(DocumentDto::getAddressName)
                .collect(Collectors.toList());
        // 장소명
        List<String> tourPlaceNames = tourResponses.getDocumentDtoList().stream()
                .map(DocumentDto::getPlaceName)
                .collect(Collectors.toList());

//        log.info("getDocumentDtoList addressNames : {} ", tourAddressNames.toString());
//        log.info("getDocumentDtoList placeNames : {} ", tourPlaceNames.toString());

        // 이 두 리스트를 2개의 원소로 가지는 최종 리스트 생성
        List<List<String>> result = new ArrayList<>();
        result.add(tourPlaceNames);
        result.add(tourAddressNames);

        return result;
    }

}
