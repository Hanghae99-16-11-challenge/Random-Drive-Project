package com.example.randomdriveproject.navigation.random.service;

import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.request.dto.RandomDocumentDto;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
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
    private final RandomKakaoCategorySearchService kakaoCategorySearchService;
    private final RandomDestinationRepository randomDestinationRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    public KakaoRouteAllResponseDto requestOffCourseSearch(double originY, double originX, double destinationY, double destinationX, String waypointsY, String waypointsX) {

        if (ObjectUtils.isEmpty(originY) || ObjectUtils.isEmpty(originX) || ObjectUtils.isEmpty(destinationY) || ObjectUtils.isEmpty(destinationX)) return null;
        System.out.println("호출준비");

        // "위도,경도" 형식의 문자열 생성
        Map<String,Object> uriData = new TreeMap<>();
        uriData.put("origin",new RandomDocumentDto(originX, originY));
        uriData.put("destination",new RandomDocumentDto(destinationX, destinationY));

        List<RandomDocumentDto> waypoints = new ArrayList<>();

        Queue<Double> waypointsYQueue = new LinkedList<>();
        String[] waypointsYArray = waypointsY.split(", ");
        for (String coordinate : waypointsYArray) {
            double waypointX = Double.parseDouble(coordinate);
            waypointsYQueue.add(waypointX);
            // 예외처리
        }

        Queue<Double> waypointsXQueue = new LinkedList<>();
        String[] waypointsXArray = waypointsX.split(", ");
        for (String coordinate : waypointsXArray) {
            double waypointY = Double.parseDouble(coordinate);
            waypointsXQueue.add(waypointY);
            // 예외처리
        }

        while (!waypointsYQueue.isEmpty()) {
            RandomDocumentDto waypoint = new RandomDocumentDto(waypointsXQueue.poll(), waypointsYQueue.poll());
            waypoints.add(waypoint);
        }

        uriData.put("waypoints", waypoints);
        uriData.put("priority","RECOMMEND");

        URI uri = URI.create("https://apis-navi.kakaomobility.com/v1/waypoints/directions");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity <Map<String,Object>> httpEntity = new HttpEntity<>(uriData,headers);

        return restTemplate.postForEntity(uri,httpEntity,KakaoRouteAllResponseDto.class).getBody();
    }
}
