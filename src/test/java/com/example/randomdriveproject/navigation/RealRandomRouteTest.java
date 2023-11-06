//package com.example.randomdriveproject.navigation;
//
//import com.example.randomdriveproject.navigation.random.entity.RandomDestination;
//import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
//import com.example.randomdriveproject.navigation.random.service.RealRandomRouteSearchService;
//import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
//import com.example.randomdriveproject.request.service.KakaoUriBuilderService;
//import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.repository.query.FluentQuery;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@EnableConfigurationProperties//테스트에서 property를 사용
//@SpringBootTest//모든 빈 사용
//
//@DisplayName("'Real' RandomRouteSearchService 테스트")
////@ExtendWith(SpringExtension.class)
////@DataJpaTest
//public class RealRandomRouteTest {
//
//    //SpringBootTest vs DataJpaTest : https://cobbybb.tistory.com/23
//
//    RestTemplate restTemplate;
//    KakaoUriBuilderService kakaoUriBuilderService;
//
//    KakaoAddressSearchService kakaoAddressSearchService;
//    RandomKakaoCategorySearchService kakaoCategorySearchService;
//
////    @Autowired
//    @MockBean//가상 객체
//    RandomDestinationRepository randomDestinationRepository;
//
//    RealRandomRouteSearchService realRandomRouteService;
//
//
//    @Value("${kakao.rest.api.key}")
//    private String kakaoRestApiKey;
//
//    @BeforeEach
//    void setup()
//    {
//        restTemplate = new RestTemplate();
//        kakaoUriBuilderService = new KakaoUriBuilderService();
//        kakaoAddressSearchService = new KakaoAddressSearchService(restTemplate, kakaoUriBuilderService);
//        kakaoCategorySearchService = new RandomKakaoCategorySearchService(kakaoUriBuilderService, restTemplate);
//
//        realRandomRouteService = new RealRandomRouteSearchService(restTemplate, kakaoAddressSearchService, kakaoCategorySearchService, randomDestinationRepository);
//
//        ReflectionTestUtils.setField(realRandomRouteService,
//                "kakaoRestApiKey",
//                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌
//
//        ReflectionTestUtils.setField(kakaoAddressSearchService,
//                "kakaoRestApiKey",
//                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (realRandomRouteService안)
//
//        ReflectionTestUtils.setField(kakaoCategorySearchService,
//                "kakaoRestApiKey",
//                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (realRandomRouteService안)
//
//        System.out.println("프라이빗이라 테스트 안한 함수 존재");
//        // 하는 방법 + 지양 이유 -> https://mangkyu.tistory.com/235
//    }
//
//    @DisplayName("DB 확인")
//    @Test
//    void RepositoryTest()
//    {
//        System.out.println("Size : "+ randomDestinationRepository.count());
//    }
//
//    @DisplayName("랜덤길찾기서비스 - 반경 기반 랜덤 길찾기")
//    @Test
//    void requestAllRandomWay()
//    {
//        String address1 = "서울 용산구 동자동 43-205";
//        int pathType1 = 1;
//        pathType1 = Math.max(Math.min(pathType1, 3), 1);
//        var result1 = realRandomRouteService.requestAllRandomWay(1L, address1, 5, 1, pathType1);
//
//        String address2 = "제주특별자치도 제주시 남성로 2";
//        int pathType2 = 2;
//        pathType2 = Math.max(Math.min(pathType2, 3), 1);
//        var result2 = realRandomRouteService.requestAllRandomWay(1L, address2, 5, 1, pathType2);
//
//        String address3 = "경기 군포시 수리산로 244";
//        int pathType3 = 3;
//        pathType3 = Math.max(Math.min(pathType2, 3), 1);
//        var result3 = realRandomRouteService.requestAllRandomWay(1L, address3, 5, 1, pathType3);
//
//        System.out.println("결과1");
//        Assertions.assertNotNull(result1);
//        Assertions.assertTrue(result1.getRoutes().length > 0);
//        System.out.println("결과2");
//        Assertions.assertNotNull(result2);
//        Assertions.assertTrue(result2.getRoutes().length > 0);
//        System.out.println("결과3");
//        Assertions.assertNotNull(result3);
//        Assertions.assertTrue(result3.getRoutes().length > 0);
//
//        var limitWaypointResult3 = realRandomRouteService.requestAllRandomWay(1L, address1, 5, 8, pathType1);
//        System.out.println("경유지 수 제한");
//        Assertions.assertTrue(limitWaypointResult3.getWaypointsLength() <= 3);
//
//        var limitWaypointResult5 = realRandomRouteService.requestAllRandomWay(1L, address1, 10, 8, pathType1);
//        System.out.println("경유지 수 제한");
//        Assertions.assertTrue(limitWaypointResult5.getWaypointsLength() <= 5);
//    }
//
//    @Test
//    void testAllRandomNullableRequests() {
//        Exception originAddressException = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestAllRandomWay(1L, null, 5, 3, 1);
//        });
//        Exception distanceException = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestAllRandomWay(1L, "출발 주소", null, 3, 1);
//        });
//        Exception countException = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestAllRandomWay(1L, "출발 주소", 5, null, 1);
//        });
//
//        Assertions.assertEquals("출발지 또는 경유지 수 또는 거리가 비어있습니다.", originAddressException.getMessage());
//        Assertions.assertEquals("출발지 또는 경유지 수 또는 거리가 비어있습니다.", distanceException.getMessage());
//        Assertions.assertEquals("출발지 또는 경유지 수 또는 거리가 비어있습니다.", countException.getMessage());
//    }
//
//    @Test
//    void testAllRandomInvalidOriginAddress() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestAllRandomWay(1L, "부정확한 주소", 5, 3, 1);
//        });
//
//        System.out.println("실패 메세지: " + exception.getMessage());
//        Assertions.assertEquals("출발지 주소를 찾을 수 없습니다.", exception.getMessage());
//    }
//
//    @DisplayName("랜덤길찾기서비스 - 목적지 기반 랜덤 길찾기")
//    @Test
//    void requestRandomWay()
//    {
//        String originAddress1 = "서울 용산구 동자동 43-205";
//        String destinationAddress1 = "서울 강남구 역삼동 822-4";
//        int pathType1 = 1;
//        pathType1 = Math.max(Math.min(pathType1, 3), 1);
//        var result1 = realRandomRouteService.requestRandomWay(originAddress1, destinationAddress1, 1, pathType1);
//
//        String originAddress2 = "제주특별자치도 제주시 남성로 2";
//        String destinationAddress2 = "제주특별자치도 제주시 남성로 22길2";
//        int pathType2 = 2;
//        pathType2 = Math.max(Math.min(pathType2, 3), 1);
//        var result2 = realRandomRouteService.requestRandomWay(originAddress2, destinationAddress2, 1, pathType2);
//
//        String originAddress3 = "경기 군포시 수리산로 244";
//        String destinationAddress3 = "경기 수원시 팔달구 팔달로2가 138";
//
//        System.out.println("결과1");
//        Assertions.assertNotNull(result1);
//        Assertions.assertTrue(result1.getRoutes().length > 0);
//        System.out.println("결과2");
//        Assertions.assertNotNull(result2);
//        Assertions.assertTrue(result2.getRoutes().length > 0);
//
//
//        var limitWaypointResult3 = realRandomRouteService.requestRandomWay( originAddress1, destinationAddress1, 8, pathType1);
//        System.out.println("경유지 수 : " + limitWaypointResult3.getWaypointsLength());
//        Assertions.assertTrue(limitWaypointResult3.getWaypointsLength() <= 3);
//
//        var limitWaypointResult5 = realRandomRouteService.requestRandomWay( originAddress3, destinationAddress3, 8, pathType1);
//        System.out.println("경유지 수 : " + limitWaypointResult5.getWaypointsLength());
//        Assertions.assertTrue(limitWaypointResult5.getWaypointsLength() <= 5);
//    }
//
//    @Test
//    void testRandomNullableRequests() {
//        Exception originAddressException = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestRandomWay(null, "목적 주소", 1, 1);
//        });
//        Exception distanceException = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestRandomWay("출발 주소", null, 1, 1);
//        });
//        Exception countException = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestRandomWay("출발 주소", "목적 주소", null, 1);
//        });
//
//        Assertions.assertEquals("출발지 또는 목적지 주소 혹은 경유지 수가 비어있습니다.", originAddressException.getMessage());
//        Assertions.assertEquals("출발지 또는 목적지 주소 혹은 경유지 수가 비어있습니다.", distanceException.getMessage());
//        Assertions.assertEquals("출발지 또는 목적지 주소 혹은 경유지 수가 비어있습니다.", countException.getMessage());
//    }
//
//    @Test
//    void testRandomInvalidOriginAddress() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestRandomWay("부정확한 출발 주소", "경기 군포시 수리산로 244", 1, 1);
//        });
//
//        System.out.println("실패 메세지: " + exception.getMessage());
//        Assertions.assertEquals("출발지 주소를 찾을 수 없습니다.", exception.getMessage());
//    }
//
//    @Test
//    void testRandomInvalidDestinationAddress() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            realRandomRouteService.requestRandomWay("경기 군포시 수리산로 244", "부정확한 목적 주소", 1, 1);
//        });
//
//        System.out.println("실패 메세지: " + exception.getMessage());
//        Assertions.assertEquals("도착지 주소를 찾을 수 없습니다.", exception.getMessage());
//    }
//
//    //프라이빗 이여서 미작성
//    //makeRequestForm
//    //getDestination
//    //getWayPointsAroundLine
//    //getWayPointsInBox
//    //getWayPointsCircular
//    //getRandomAngle
//    //calculateDistance
//    //getRandomWayPoint
//}
