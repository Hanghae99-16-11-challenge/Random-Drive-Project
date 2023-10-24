package com.example.randomdriveproject.navigation.basic.service;

import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.KakaoUriBuilderService;
import com.example.randomdriveproject.user.entity.User;
import com.example.randomdriveproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoRouteSearchService {

    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService kakaoUriBuilderService;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final UserRepository userRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;


    public KakaoRouteAllResponseDto requestRouteSearch(String originAddress, String destinationAddress) {
//        String username = user.getUsername();

        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(destinationAddress)) return null;
        System.out.println("호출준비");

        // 사용자 인증 정보 확인
//        if(!username.equals(userRepository.findByUsername(username))){
//            throw new IllegalArgumentException("잘못된토큰 정보");
//        }

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