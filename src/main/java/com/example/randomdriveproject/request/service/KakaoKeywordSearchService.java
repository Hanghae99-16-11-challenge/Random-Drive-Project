package com.example.randomdriveproject.request.service;

import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.exception.KakaoApiExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoKeywordSearchService {

    private final KakaoUriBuilderService kakaoUriBuilderService;
    private final RestTemplate restTemplate;

    private static final String PARK_CATEGORY = "AT4";

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;



    // 관광명소 기반
    public KakaoApiResponseDto requestAttractionKeywordSearch(String query) {

        URI uri = kakaoUriBuilderService.buildUriByKeywordSearch(query);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity httpEntity = new HttpEntity(headers);
        log.info("*** 키워드 주소 api 호출");

        // kakao api 호출
        KakaoApiResponseDto response = KakaoApiExceptionHandler.handleApiCall(() ->
                        restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class).getBody(),uri);
        return response;
    }

}
