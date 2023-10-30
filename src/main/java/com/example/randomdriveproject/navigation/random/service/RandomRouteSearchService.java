package com.example.randomdriveproject.navigation.random.service;

import com.example.randomdriveproject.navigation.random.entity.RandomDestination;
import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
import com.example.randomdriveproject.request.service.KakaoKeywordSearchService;
import com.example.randomdriveproject.user.entity.User;
import com.example.randomdriveproject.user.repository.UserRepository;
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

@Slf4j(topic = "RandomRouteSearchService")
@Service
@RequiredArgsConstructor
public class RandomRouteSearchService {

    private final RestTemplate restTemplate;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final RandomKakaoCategorySearchService kakaoCategorySearchService;
    private final RandomDestinationRepository randomDestinationRepository;
    private final UserRepository userRepository;
    private final KakaoKeywordSearchService randomKakaoKeywordSearchService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    public KakaoRouteAllResponseDto requestAllRandomWay(User user, String originAddress, Integer redius) {

        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(redius)) return null;

        // 유저 인증
        String username = user.getUsername();
        Optional<User> userCheck = userRepository.findByUsername(username);
        if (!userCheck.isPresent() || !username.equals(userCheck.get().getUsername())) {
            throw new IllegalArgumentException("잘못된 정보입니다.");
        }

        // 출발지와 도착지 주소를 각각 좌표로 변환
        DocumentDto origin = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList().get(0);

        /***
         목적지와 경유지 값을 반경으로 계산해서 가져오는 메소드 -> 관광명소
         ***/
        KakaoApiResponseDto tourResponses = kakaoCategorySearchService.requestAttractionCategorySearch(origin.getLatitude(), origin.getLongitude(), redius);

        /***
         목적지와 경유지 값을 반경으로 계산해서 가져오는 메소드 -> 문화시설
         ***/
        KakaoApiResponseDto cultureResponses = kakaoCategorySearchService.requestCultureCategorySearch(origin.getLatitude(), origin.getLongitude(), redius);


        /***
         랜덤으로 다중 목적지와 경유지 만들기 알고리즘
         ***/
        int RandomLength = tourResponses.getDocumentDtoList().size();


        Random rd = new Random();

        int destinationCnt = rd.nextInt(RandomLength);
        int waypointsCnt = rd.nextInt(RandomLength);

        if(destinationCnt == waypointsCnt){
            if(destinationCnt == 0){
                destinationCnt += 1;
            }
            waypointsCnt = destinationCnt - 1;
            // 인덱스 범위 오류 수정
        }


        DocumentDto destination = tourResponses.getDocumentDtoList().get(destinationCnt);
        log.info("destination : {}", destination );

        DocumentDto waypoints = tourResponses.getDocumentDtoList().get(waypointsCnt);
        log.info("waypoints : {}", waypoints );

        /**
         추가 : place_name 과 address_name의 주소
         **/

        // 주소에서 원하는 부분만 추출
        String[] addressPartsDes = destination.getAddressName().split(" ");
        String[] addressPartsWay = waypoints.getAddressName().split(" ");

        String refinedAddressDes = (addressPartsDes.length >= 2) ? addressPartsDes[0] + " " + addressPartsDes[1] : destination.getAddressName();
        String refinedAddressWay = (addressPartsDes.length >= 2) ? addressPartsWay[0] + " " + addressPartsWay[1] : waypoints.getAddressName();

        // 주소와 장소명 결합
        String combinedKeywordDes = refinedAddressDes + " " + destination.getPlaceName();
        String combinedKeywordWay = refinedAddressWay + " " + waypoints.getPlaceName();
        log.info("combinedKeywordDes : {}", combinedKeywordDes );
        log.info("combinedKeywordWay : {}", combinedKeywordWay );


        //----------------------------------------------------------------//

        // 목적지 DB에 남김, 만일 동일 사용자가 이미 목적지를 저장해 놓았다면, 삭제
        RandomDestination randomDestination = new RandomDestination(username, combinedKeywordDes);
        RandomDestination olderRandomDestination = randomDestinationRepository.findByUsername(username);
        if (olderRandomDestination != null)
            randomDestinationRepository.delete(olderRandomDestination);
        randomDestinationRepository.save(randomDestination);




        /***
         요청 헤더 만드는 공식
         ***/

        return makeRequestForm(origin,destination,waypoints);
    }

    public KakaoRouteAllResponseDto requestRamdomWay(String originAddress, String destinationAddress,Integer redius) {


        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(redius) || ObjectUtils.isEmpty(destinationAddress)) return null;

        // 출발지와 도착지 주소를 각각 좌표로 변환
        DocumentDto origin = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList().get(0);
        DocumentDto destination = kakaoAddressSearchService.requestAddressSearch(destinationAddress).getDocumentDtoList().get(0);

        /***
         * 목적지와 경유지 값을 반경으로 계산해서 가져오는 메소드
         ***/
        KakaoApiResponseDto responses = kakaoCategorySearchService.requestAttractionCategorySearch(origin.getLatitude(), origin.getLongitude(), redius);

        /***
         랜덤으로 경유지 만들기 알고리즘
         ***/
        int RandomLength = responses.getDocumentDtoList().size();

        Random rd = new Random();

        int waypointsCnt = rd.nextInt(RandomLength);

        DocumentDto waypoints = responses.getDocumentDtoList().get(waypointsCnt);

        /***
         * 요청 헤더 만드는 공식
         ***/

        return makeRequestForm(origin,destination,waypoints);

    }

    private KakaoRouteAllResponseDto makeRequestForm(DocumentDto origin, DocumentDto destination, DocumentDto waypoints){
        Map<String,Object> uriData = new TreeMap<>();
        uriData.put("origin",new DocumentDto(origin.getAddressName(),origin.getLatitude(), origin.getLongitude()));
        uriData.put("destination",new DocumentDto(destination.getAddressName(),destination.getLatitude(),destination.getLongitude()));

        uriData.put("waypoints",new ArrayList<>(Arrays.asList(
                new DocumentDto(waypoints.getAddressName(), waypoints.getLatitude(), waypoints.getLongitude())
        )));
        uriData.put("priority","RECOMMEND");

        URI uri = URI.create("https://apis-navi.kakaomobility.com/v1/waypoints/directions");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(uriData,headers);

        return restTemplate.postForEntity(uri,httpEntity,KakaoRouteAllResponseDto.class).getBody();
    }
}


