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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j(topic ="KakaoRouteSearchService" )
@Service
@RequiredArgsConstructor
public class KakaoRouteSearchService {

    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService kakaoUriBuilderService;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final UserRepository userRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;


    public KakaoRouteAllResponseDto requestRouteSearch(String originAddress, String destinationAddress, Long userId) {

        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("출발지 혹은 목적지 주소가 비어있습니다.");
        }

        // 사용자 인증 정보 확인
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 없거나 일치하지 않습니다.");
        }


        // 기존 kakaoAddressSearchService.requestAddressSearch(destinationAddress).getDocumentDtoList().get(0); -> index 오류
        // 출발지와 도착지 주소 검색
        List<DocumentDto> originList = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList();
        if (originList.isEmpty()) {
            throw new IllegalArgumentException("출발지 주소를 찾을 수 없습니다.");
        }
        DocumentDto origin = originList.get(0);

        List<DocumentDto> destinationList = kakaoAddressSearchService.requestAddressSearch(destinationAddress).getDocumentDtoList();
        if (destinationList.isEmpty()) {
            throw new IllegalArgumentException("도착지 주소를 찾을 수 없습니다.");
        }
        DocumentDto destination = destinationList.get(0);



        // "위도,경도" 형식의 문자열 생성
        String originCoord = origin.getLongitude() + "," + origin.getLatitude();
        String destinationCoord = destination.getLongitude() + "," + destination.getLatitude();

        URI uri = kakaoUriBuilderService.buildUriByRouteSearch(originCoord, destinationCoord);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(headers);

        try {
            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoRouteAllResponseDto.class).getBody();
        } catch (RestClientException e) {
            log.error("API 호출 중 오류 발생", e);
            throw new RuntimeException("API 호출 실패", e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생", e);
            throw new RuntimeException("시스템 오류", e);
        }
    }


//    //경로 재생성
    public KakaoRouteAllResponseDto requestRouteReSearch(double lat, double lng) {

        if (ObjectUtils.isEmpty(lat) || ObjectUtils.isEmpty(lng)){
            throw new IllegalArgumentException("좌표가 올바르지 않습니다.");
        }
        log.info("기본 경로 이탈시 준비");

        double startCoord = Double.parseDouble(lat + "," + lng);

        URI uri = kakaoUriBuilderService.buildUriByReRouteSearch(startCoord);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(headers);

        try {
            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoRouteAllResponseDto.class).getBody();
        } catch (RestClientException e) {
            log.error("API 호출 중 오류 발생", e);
            throw new RuntimeException("API 호출 실패", e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생", e);
            throw new RuntimeException("시스템 오류", e);
        }
    }
}