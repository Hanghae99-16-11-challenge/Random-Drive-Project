package com.example.randomdriveproject.navigation.random.service;

import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Slf4j(topic = "OffCourseService")
@Service
@RequiredArgsConstructor
public class RandomOffCourseService {
    private final RestTemplate restTemplate;
    private final KakaoAddressSearchService kakaoAddressSearchService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    public KakaoRouteAllResponseDto requestOffCourseSearch(String currentAddress, double destinationY, double destinationX, String waypointsY, String waypointsX) {

        if (ObjectUtils.isEmpty(currentAddress) || ObjectUtils.isEmpty(destinationY) || ObjectUtils.isEmpty(destinationX)) return null;
        log.info("랜덤 네비게이션 경로 이탈 서비스 실행");
        DocumentDto origin = kakaoAddressSearchService.requestAddressSearch(currentAddress).getDocumentDtoList().get(0);

        double originY = origin.getLatitude();
        double originX = origin.getLongitude();

        // "위도,경도" 형식의 문자열 생성
        Map<String,Object> uriData = new TreeMap<>();
        uriData.put("origin",new DocumentDto( "현 출발지" , originY, originX));
        uriData.put("destination",new DocumentDto("이전 목적지", destinationY, destinationX));

        if (!waypointsX.equals("0")) {
            List<DocumentDto> waypoints = new ArrayList<>();

            Queue<Double> waypointsYQueue = new LinkedList<>();
            String[] waypointsYArray = waypointsY.split(" ");
            for (String coordinate : waypointsYArray) {
                double waypointY = Double.parseDouble(coordinate);
                waypointsYQueue.add(waypointY);
                // 예외처리
            }

            Queue<Double> waypointsXQueue = new LinkedList<>();
            String[] waypointsXArray = waypointsX.split(" ");
            for (String coordinate : waypointsXArray) {
                double waypointX = Double.parseDouble(coordinate);
                waypointsXQueue.add(waypointX);
                // 예외처리
            }

            while (!waypointsYQueue.isEmpty()) {
                DocumentDto waypoint = new DocumentDto("이전 경유지", waypointsYQueue.poll(), waypointsXQueue.poll());
                waypoints.add(waypoint);
            }

            uriData.put("waypoints", waypoints);
        }
        uriData.put("priority","RECOMMEND");

        URI uri = URI.create("https://apis-navi.kakaomobility.com/v1/waypoints/directions");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity <Map<String,Object>> httpEntity = new HttpEntity<>(uriData,headers);

        return restTemplate.postForEntity(uri,httpEntity,KakaoRouteAllResponseDto.class).getBody();
    }

    public KakaoRouteAllResponseDto requestOffCourseCoordinateSearch(double originY, double originX, double destinationY, double destinationX, String waypointsY, String waypointsX) {

        if (ObjectUtils.isEmpty(originY) || ObjectUtils.isEmpty(originX) || ObjectUtils.isEmpty(destinationY) || ObjectUtils.isEmpty(destinationX)) return null;
        log.info("랜덤 네비게이션 경로 이탈 서비스 실행");

        // "위도,경도" 형식의 문자열 생성
        Map<String,Object> uriData = new TreeMap<>();
        uriData.put("origin",new DocumentDto( "현 출발지" , originY, originX));
        uriData.put("destination",new DocumentDto("이전 목적지", destinationY, destinationX));

        if (!waypointsX.isEmpty()) {
            List<DocumentDto> waypoints = new ArrayList<>();

            Queue<Double> waypointsYQueue = new LinkedList<>();
            String[] waypointsYArray = waypointsY.split(" ");
            for (String coordinate : waypointsYArray) {
                double waypointY = Double.parseDouble(coordinate);
                waypointsYQueue.add(waypointY);
                // 예외처리
            }

            Queue<Double> waypointsXQueue = new LinkedList<>();
            String[] waypointsXArray = waypointsX.split(" ");
            for (String coordinate : waypointsXArray) {
                double waypointX = Double.parseDouble(coordinate);
                waypointsXQueue.add(waypointX);
                // 예외처리
            }

            while (!waypointsYQueue.isEmpty()) {
                DocumentDto waypoint = new DocumentDto("이전 경유지", waypointsYQueue.poll(), waypointsXQueue.poll());
                waypoints.add(waypoint);
            }

            uriData.put("waypoints", waypoints);
        }
        uriData.put("priority","RECOMMEND");

        URI uri = URI.create("https://apis-navi.kakaomobility.com/v1/waypoints/directions");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity <Map<String,Object>> httpEntity = new HttpEntity<>(uriData,headers);

        return restTemplate.postForEntity(uri,httpEntity,KakaoRouteAllResponseDto.class).getBody();
    }
}
