package com.example.randomdriveproject.navigation.random.service;

import com.example.randomdriveproject.navigation.random.entity.RandomDestination;
import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.request.dto.RandomDocumentDto;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Slf4j(topic = "RealRandomRouteSearchService")
@Service
@RequiredArgsConstructor
public class RealRandomRouteSearchService {

    private final RestTemplate restTemplate;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final RandomKakaoCategorySearchService kakaoCategorySearchService;
    private final RandomDestinationRepository randomDestinationRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    public KakaoRouteAllResponseDto requestAllRandomWay(String username, String originAddress, Integer distance, Integer count) {

        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(distance)) return null;

        // 출발지 주소를 좌표로 변환
        DocumentDto origin = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList().get(0);
        double originY = origin.getLatitude();
        double originX = origin.getLongitude();

        double convertedDistance = distance / 100.0;

        // 목적지를 거리 기반으로 무작위적으로 가져오는 메소드
        DocumentDto destination = getDestination(originY, originX, convertedDistance);

        // 목적지 DB에 남김, 만일 동일 사용자가 이미 목적지를 저장해 놓았다면, 삭제
        RandomDestination randomDestination = new RandomDestination(username, destination.getAddressName());
        RandomDestination olderRandomDestination = randomDestinationRepository.findByUsername(username);
        if (olderRandomDestination != null)
            randomDestinationRepository.delete(olderRandomDestination);
        randomDestinationRepository.save(randomDestination);

        List<RandomDocumentDto> waypoints = getWayPoints(originY, originX, destination.getLatitude(), destination.getLongitude(), count);

        return makeRequestForm(origin,destination,waypoints);
    }

    private KakaoRouteAllResponseDto makeRequestForm(DocumentDto origin, DocumentDto destination, List<RandomDocumentDto> waypoints){
        Map<String,Object> uriData = new TreeMap<>();
        uriData.put("origin",new RandomDocumentDto(origin.getLongitude(),origin.getLatitude()));
        uriData.put("destination",new RandomDocumentDto(destination.getLongitude(), destination.getLatitude()));

        uriData.put("waypoints", waypoints);
        uriData.put("priority","RECOMMEND");

        URI uri = URI.create("https://apis-navi.kakaomobility.com/v1/waypoints/directions");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity <Map<String,Object>> httpEntity = new HttpEntity<>(uriData,headers);

        return restTemplate.postForEntity(uri,httpEntity,KakaoRouteAllResponseDto.class).getBody();
    }

    // 무작위 목적지 가져오기
    private DocumentDto getDestination(double originY, double originX, double distance) {
        double radians = Math.toRadians(getRandomAngle());

        double randomY = originY + distance * Math.sin(radians);
        double randomX = originX + distance * Math.cos(radians);
        System.out.println("새로운 좌표: (" + randomY + ", " + randomX + ")");
        double tempY = randomY; // 임시 Y 좌표
        double tempX = randomX; // 임시 X 좌표

        // 목적지 값을 반경으로 계산해서 가져오는 메소드
        KakaoApiResponseDto responses = kakaoCategorySearchService.requestPharmacyCategorySearch(randomY, randomX, 2);

        // 랜덤으로 다중 목적지와 경유지 만들기 알고리즘
        int randomLength = responses.getDocumentDtoList().size();

        // 만일 찍은 좌표 주변에 아무것도 없다면..?
        if (randomLength == 0) { // 출발지 좌표를 기준으로 대칭되는 곳의 좌표를 목적지 좌표로 수정
            randomX -= 2 * (randomX - originX);
            randomY -= 2 * (randomY - originY);
            responses = kakaoCategorySearchService.requestPharmacyCategorySearch(randomY, randomX, 2);
            randomLength = responses.getDocumentDtoList().size();
            if (randomLength == 0) { // 출발지 좌표를 기준으로 Y좌표가 대칭되는 곳의 좌표를 목적지 좌표로 수정
                randomX = tempX;
                responses = kakaoCategorySearchService.requestPharmacyCategorySearch(randomY, randomX, 2);
                randomLength = responses.getDocumentDtoList().size();
                if (randomLength == 0) { // 출발지 좌표를 기준으로 X좌표가 대칭되는 곳의 좌표를 목적지 좌표로 수정
                    randomX -= 2 * (randomX - originX);
                    randomY = tempY;
                    responses = kakaoCategorySearchService.requestPharmacyCategorySearch(randomY, randomX, 2);
                }
            }
        }
        Random rd = new Random();
        int destinationCnt = rd.nextInt(randomLength);
        DocumentDto destination = responses.getDocumentDtoList().get(destinationCnt);
        return destination;
    }

    // 경유지 구하기 - 직선거리 주변 경유지
    private List<RandomDocumentDto> getWayPoints(double originY, double originX, double destinationY, double destinationX, Integer count) {
        double distance = calculateDistance(originY, originX, destinationY, destinationX);
        double realDistance = distance * 100;
        List<RandomDocumentDto> wayPoints = new ArrayList<>();
        if(count * 0.2 < distance) {
            for (int i = 0; i < count; i++) {
                double tempY = originY + (destinationY - originY) * i / count;
                double tempX = originX + (destinationX - originX) * i / count;
                KakaoApiResponseDto responses = kakaoCategorySearchService.requestPharmacyCategorySearch(tempY, tempX, 20);
                int randomLength = responses.getDocumentDtoList().size();
                Random rd = new Random();
                int wayPointCnt = rd.nextInt(randomLength);
                RandomDocumentDto wayPoint = new RandomDocumentDto(responses.getDocumentDtoList().get(wayPointCnt));
                wayPoints.add(wayPoint);
            }
        }
        else {
            for (int i = 1; i <= count; i++) {
                double tempY = originY + (destinationY - originY) * i / count;
                double tempX = originX + (destinationX - originX) * i / count;
                KakaoApiResponseDto responses = kakaoCategorySearchService.requestPharmacyCategorySearch(tempY, tempX, realDistance/count);
                int randomLength = responses.getDocumentDtoList().size();
                Random rd = new Random();
                int wayPointCnt = rd.nextInt(randomLength);
                RandomDocumentDto wayPoint = new RandomDocumentDto(responses.getDocumentDtoList().get(wayPointCnt));
                wayPoints.add(wayPoint);
            }
        }
        return wayPoints;
    }

    private double getRandomAngle() {
        Random random = new Random();
        // 0부터 3600 사이의 무작위 정수를 얻음
        int randomInt = random.nextInt(3601); // 0부터 3600까지의 정수
        // 정수를 10으로 나누어 각도를 얻음 (0.1 단위로)
        double angle = randomInt / 10.0;
        // 점검용
        System.out.println("무작위 각도: " + angle);
        return angle;
    }

    // 두 점 사이의 거리를 계산하는 메서드
    public static double calculateDistance(double originY, double originX, double destinationY, double destinationX) {
        double deltaY = destinationY - originY;
        double deltaX = destinationX - originX;
        // 유클리드 거리 계산
        return Math.sqrt(deltaY * deltaY + deltaX * deltaX);
    }

}
