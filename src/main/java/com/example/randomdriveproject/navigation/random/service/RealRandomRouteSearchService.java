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

    // 반경 기반 랜덤 길찾기
    public KakaoRouteAllResponseDto requestAllRandomWay(Long userId, String originAddress, Integer distance, Integer count, int type) {

        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(distance) || ObjectUtils.isEmpty(count)) {
            throw new IllegalArgumentException("출발지 또는 경유지 수 또는 거리가 비어있습니다.");
        }

        if (distance < 10) {
            count = Math.min(count, 3);
        }
        else if (distance < 20) {
            count = Math.min(count, 5);
        }

        // 출발지 주소를 좌표로 변환
        List<DocumentDto> originList = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList();
        if (originList.isEmpty()) {
            throw new IllegalArgumentException("출발지 주소를 찾을 수 없습니다.");
        }
        DocumentDto origin = originList.get(0);
        double originY = origin.getLatitude();
        double originX = origin.getLongitude();

        double convertedDistance = distance / 100.0;

        // 목적지를 거리 기반으로 무작위적으로 가져오는 메소드
        DocumentDto destination = getDestination(originY, originX, convertedDistance);

        double destinationY = destination.getLatitude();
        double destinationX = destination.getLongitude();

        // 목적지 DB에 남김, 만일 동일 사용자가 이미 목적지를 저장해 놓았다면, 삭제
        RandomDestination randomDestination = new RandomDestination(userId, destination.getAddressName());
        RandomDestination olderRandomDestination = randomDestinationRepository.findByUserId(userId);
        if (olderRandomDestination != null)
            randomDestinationRepository.delete(olderRandomDestination);
        randomDestinationRepository.save(randomDestination);

        if (type == 1) {
            // 선형 랜덤 경유지 추천
            List<RandomDocumentDto> waypoints = getWayPointsAroundLine(originY, originX, destinationY, destinationX, count);
            return makeRequestForm(origin,destination,waypoints); // uri 데이터 생성시 null은 나오지 않음
        }
        else if (type == 2) {
            // 박스형 랜덤 경유지
            List<RandomDocumentDto> waypoints = getWayPointsInBox(originY, originX, destinationY, destinationX, count);
            return makeRequestForm(origin,destination,waypoints);
        }
        else { // type == 3
            // 순환형 랜덤 경유지
            List<RandomDocumentDto> waypoints = getWayPointsCircular(originY, originX, convertedDistance/2, count);
            return makeRequestForm(origin, origin, waypoints);
        }
    }

    // 목적지 기반 랜덤 길찾기
    public KakaoRouteAllResponseDto requestRandomWay(String originAddress, String destinationAddress, Integer count, int type) {

        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(count) || ObjectUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("출발지 또는 목적지 주소 혹은 경유지 수가 비어있습니다.");
        }

        // 출발지 주소를 좌표로 변환
        List<DocumentDto> originList = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList();
        if (originList.isEmpty()) {
            throw new IllegalArgumentException("출발지 주소를 찾을 수 없습니다.");
        }
        DocumentDto origin = originList.get(0);
        double originY = origin.getLatitude();
        double originX = origin.getLongitude();

        //목적지 주소를 좌표로 전환
        List<DocumentDto> destinationList = kakaoAddressSearchService.requestAddressSearch(destinationAddress).getDocumentDtoList();
        if (destinationList.isEmpty()) {
            throw new IllegalArgumentException("도착지 주소를 찾을 수 없습니다.");
        }
        DocumentDto destination = destinationList.get(0);
        double destinationY = destination.getLatitude();
        double destinationX = destination.getLongitude();

        double distance = calculateDistance(originY, originX, destinationY, destinationX);
        double realDistance = distance * 100;

        if (realDistance < 10) {
            count = Math.min(count, 3);
        }
        else if (realDistance < 20) {
            count = Math.min(count, 5);
        }

        if (type == 1) {
            List<RandomDocumentDto> waypoints = getWayPointsAroundLine(originY, originX, destinationY, destinationX, count);
            return makeRequestForm(origin,destination,waypoints);
        }
        else { // type == 2
            List<RandomDocumentDto> waypoints = getWayPointsInBox(originY, originX, destinationY, destinationX, count);
            return makeRequestForm(origin,destination,waypoints);
        }

    }

    // 반환형 만들기
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
        double radians = Math.toRadians(getRandomAngle(360));

        double randomY = originY + distance * Math.sin(radians);
        double randomX = originX + distance * Math.cos(radians);

        double tempY = randomY; // 임시 Y 좌표
        double tempX = randomX; // 임시 X 좌표

        // 목적지 값을 반경으로 계산해서 가져오는 메소드
        KakaoApiResponseDto responses = kakaoCategorySearchService.requestAttractionCategorySearch(randomY, randomX, 2);

        int randomLength = responses.getDocumentDtoList().size();

        // 만일 찍은 좌표 주변에 아무것도 없다면..?
        if (randomLength == 0) { // 출발지 좌표를 기준으로 대칭되는 곳의 좌표를 목적지 좌표로 수정
            randomX -= 2 * (randomX - originX);
            randomY -= 2 * (randomY - originY);
            responses = kakaoCategorySearchService.requestAttractionCategorySearch(randomY, randomX, 2);
            randomLength = responses.getDocumentDtoList().size();
            if (randomLength == 0) { // 출발지 좌표를 기준으로 Y좌표가 대칭되는 곳의 좌표를 목적지 좌표로 수정
                randomX = tempX;
                responses = kakaoCategorySearchService.requestAttractionCategorySearch(randomY, randomX, 2);
                randomLength = responses.getDocumentDtoList().size();
                if (randomLength == 0) { // 출발지 좌표를 기준으로 X좌표가 대칭되는 곳의 좌표를 목적지 좌표로 수정
                    randomX -= 2 * (randomX - originX);
                    randomY = tempY;
                    responses = kakaoCategorySearchService.requestAttractionCategorySearch(randomY, randomX, 2);
                }
            }
        }
        if (randomLength == 0) {
            throw new NullPointerException("목적지를 찾을 수 없습니다. 범위를 수정해보거나 다시 시도해주세요.");
        }
        Random rd = new Random();
        int destinationCnt = rd.nextInt(randomLength);
        DocumentDto destination = responses.getDocumentDtoList().get(destinationCnt);
        System.out.println("목적지 x 좌표: " + destination.getLongitude());
        return destination;
    }

    // 경유지 구하기 - 직선거리 주변 경유지
    private List<RandomDocumentDto> getWayPointsAroundLine(double originY, double originX, double destinationY, double destinationX, Integer count) {
        double distance = calculateDistance(originY, originX, destinationY, destinationX);
        double realDistance = distance * 100;
        List<RandomDocumentDto> wayPoints = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            double tempY = originY + (destinationY - originY) * i / count;
            double tempX = originX + (destinationX - originX) * i / count;
            double redius = Math.min(realDistance/count, 20);
            RandomDocumentDto wayPoint = getRandomWayPoint(tempY, tempX, redius);
            if (wayPoint != null)
                wayPoints.add(wayPoint);
        }
        return wayPoints;
    }

    // 경유지 구하기 - 정사각형 박스 내부 경유지
    private List<RandomDocumentDto> getWayPointsInBox(double originY, double originX, double destinationY, double destinationX, Integer count) {
        List<RandomDocumentDto> wayPoints = new ArrayList<>();
        double middleY = (originY + destinationY) / 2;
        double middleX = (originX + destinationX) / 2;
        double distanceY = Math.abs(originY - destinationY);
        double distanceX = Math.abs(originX - destinationX);
        double sideLength = Math.max(distanceY, distanceX);

        if (sideLength == distanceY) {
            if (originY > middleY) {
                for (int i = 0; i < count; i++) {
                    Random rd = new Random();
                    int randomNum = rd.nextInt(count);
                    double randomY = originY - sideLength / count / 2 - sideLength / count * i;
                    for (int j = 0; j < count; j++) { // 경유지 찾을 때까지 해당 범위의 모든 구역 탐색
                        double randomX = middleX - sideLength / 2 + sideLength / count * ((randomNum+j)%count);
                        double redius = Math.min(sideLength/count * 100, 20);
                        RandomDocumentDto wayPoint = getRandomWayPoint(randomY, randomX, redius);
                        if (wayPoint != null) {
                            wayPoints.add(wayPoint);
                            break;
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < count; i++) {
                    Random rd = new Random();
                    int randomNum = rd.nextInt(count);
                    double randomY = originY + sideLength / count / 2 + sideLength / count * i;
                    for (int j = 0; j < count; j++) { // 경유지 찾을 때까지 해당 범위의 모든 구역 탐색
                        double randomX = middleX - sideLength / 2 + sideLength / count * ((randomNum+j)%count);
                        double redius = Math.min(sideLength/count * 100, 20);
                        RandomDocumentDto wayPoint = getRandomWayPoint(randomY, randomX, redius);
                        if (wayPoint != null) {
                            wayPoints.add(wayPoint);
                            break;
                        }
                    }
                }
            }
        }
        else {
            if(originX > middleX) {
                for (int i = 0; i < count; i++) {
                    Random rd = new Random();
                    int randomNum = rd.nextInt(count);
                    double randomX = originX - sideLength / count / 2 - sideLength / count * i;
                    for (int j = 0; j < count; j++) { // 경유지 찾을 때까지 해당 범위의 모든 구역 탐색
                        double randomY = middleY - sideLength / 2 + sideLength / count * ((randomNum+j)%count);
                        double redius = Math.min(sideLength/count * 100, 20);
                        RandomDocumentDto wayPoint = getRandomWayPoint(randomY, randomX, redius);
                        if (wayPoint != null) {
                            wayPoints.add(wayPoint);
                            break;
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < count; i++) {
                    Random rd = new Random();
                    int randomNum = rd.nextInt(count);
                    double randomX = originX + sideLength / count / 2 + sideLength / count * i;
                    for (int j = 0; j < count; j++) { // 경유지 찾을 때까지 해당 범위의 모든 구역 탐색
                        double randomY = middleY - sideLength / 2 + sideLength / count * ((randomNum+j)%count);
                        double redius = Math.min(sideLength/count * 100, 20);
                        RandomDocumentDto wayPoint = getRandomWayPoint(randomY, randomX, redius);
                        if (wayPoint != null) {
                            wayPoints.add(wayPoint);
                            break;
                        }
                    }
                }
            }
        }
        return  wayPoints;
    }

    private List<RandomDocumentDto> getWayPointsCircular(double originY, double originX, double redius, Integer count) {
        List<RandomDocumentDto> wayPoints = new ArrayList<>();

        double degree = getRandomAngle(360);
        double radian = Math.toRadians(degree);
        double rediusY = originY + redius * Math.sin(radian);
        double rediusX = originX + redius * Math.cos(radian);
        System.out.println("중점 : " + rediusY + ", " + rediusX);
        int rangeDegree = 360 / (count + 1);

        for (int i = 0; i < count; i++) {
            double wayPointRadian = Math.toRadians(degree + 180 + getRandomAngle(rangeDegree)+ rangeDegree * i);
            System.out.println("각도 : " + wayPointRadian);
            for (double temp = redius; temp > 0.02; temp -= 0.02) { // 경유지 찾을 때까지 반지름 내부 모든 구역 탐색
                double wayPointY = rediusY + redius * Math.sin(wayPointRadian);
                double wayPointX = rediusX + redius * Math.cos(wayPointRadian);
                System.out.println("정점" + (i+1) + " : " + wayPointY + ", " + wayPointX);
                RandomDocumentDto wayPoint = getRandomWayPoint(wayPointY, wayPointX, 2);
                if (wayPoint != null) {
                    wayPoints.add(wayPoint);
                    break;
                }
            }
        }
        return wayPoints;
    }

    // 무작위 각도를 반환하는 메서드
    private double getRandomAngle(int angleRange) {
        Random random = new Random();
        // 0부터 3600 사이의 무작위 정수를 얻음
        int randomInt = random.nextInt(angleRange * 10 + 1); // 랜덤 각도 구해줌
        // 정수를 10으로 나누어 각도를 얻음 (0.1 단위로)
        double angle = randomInt / 10.0;
        // 점검용
        System.out.println("무작위 각도: " + angle);
        return angle;
    }

    // 두 점 사이의 거리를 계산하는 메서드
    private double calculateDistance(double originY, double originX, double destinationY, double destinationX) {
        double deltaY = destinationY - originY;
        double deltaX = destinationX - originX;
        // 유클리드 거리 계산
        return Math.sqrt(deltaY * deltaY + deltaX * deltaX);
    }

    // 특정 좌표 주변 경유지를 골라주는 메서드
    private RandomDocumentDto getRandomWayPoint(double y, double x, double redius) {
        Random rd = new Random();
        KakaoApiResponseDto responses = kakaoCategorySearchService.requestAttractionCategorySearch(y, x, redius);
        int randomLength = responses.getDocumentDtoList().size();
        if (randomLength == 0)
            return null;
        int wayPointCnt = rd.nextInt(randomLength);
        return new RandomDocumentDto(responses.getDocumentDtoList().get(wayPointCnt));
    }

}
