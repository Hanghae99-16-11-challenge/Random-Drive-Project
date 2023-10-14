package com.example.randomdriveproject.service;

import com.example.randomdriveproject.dto.kakao_navigation.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.kakaoapi.dto.DocumentDto;
import com.example.randomdriveproject.kakaoapi.service.KakaoAddressSearchService;
import com.example.randomdriveproject.kakaoapi.service.KakaoUriBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomkakaoRouteSearchService {

    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService kakaoUriBuilderService;
    private final KakaoAddressSearchService kakaoAddressSearchService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;


    public KakaoRouteAllResponseDto requestRouteSearch(String originAddress, String destinationAddress) {
        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(destinationAddress)) return null;

        // 출발지와 도착지 주소를 각각 좌표로 변환
        DocumentDto origin = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList().get(0);
        DocumentDto destination = kakaoAddressSearchService.requestAddressSearch(destinationAddress).getDocumentDtoList().get(0);

        // "위도,경도" 형식의 문자열 생성
        String originCoord = origin.getLongitude() + "," + origin.getLatitude();
        String destinationCoord = destination.getLongitude() + "," + destination.getLatitude();

        URI uri = kakaoUriBuilderService.buildUriByRouteSearch(originCoord, destinationCoord);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoRouteAllResponseDto.class).getBody();
    }
}