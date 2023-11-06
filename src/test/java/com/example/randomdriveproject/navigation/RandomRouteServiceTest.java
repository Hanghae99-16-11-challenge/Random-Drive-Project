package com.example.randomdriveproject.navigation;

import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
import com.example.randomdriveproject.navigation.random.service.RandomKakaoRouteSearchService;
import com.example.randomdriveproject.navigation.random.service.RandomOffCourseService;
import com.example.randomdriveproject.navigation.random.service.RandomRouteSearchService;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.KakaoKeywordSearchService;
import com.example.randomdriveproject.request.service.KakaoUriBuilderService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
import com.example.randomdriveproject.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@EnableConfigurationProperties//테스트에서 property를 사용
@SpringBootTest//모든 빈 사용

@DisplayName("RandomRouteSearchService 테스트")
public class RandomRouteServiceTest {
    //SpringBootTest vs DataJpaTest : https://cobbybb.tistory.com/23

    RestTemplate restTemplate;
    KakaoUriBuilderService kakaoUriBuilderService;


    KakaoAddressSearchService kakaoAddressSearchService;
    RandomKakaoCategorySearchService kakaoCategorySearchService;
    KakaoKeywordSearchService randomKakaoKeywordSearchService;
    RandomKakaoRouteSearchService randomKakaoRouteSearchService;

    //    @Autowired
    @MockBean//가상 객체
    RandomDestinationRepository randomDestinationRepository;
    @MockBean
    UserRepository userRepository;

    String[] TestAddress = {"제주특별자치도 제주시 남성로 2",
            "제주특별자치도 제주시 남성로 22길2",
            "제주특별자치도 제주시 조천읍 와산서1길 32",
            "제주특별자치도 제주시 애월읍 고내로9길 25"};

    // [[x,y]...[x,y]]
    double[][] waypoint = {{126.53001226699159, 33.50674031248494},
            {126.51580214859632, 33.504461199016994},
            {126.49423334018748, 33.48096748115865}};

    //===========

    RandomRouteSearchService randomRouteSearchService;
    RandomOffCourseService randomOffCourseService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @BeforeEach
    void setup()
    {
        restTemplate = new RestTemplate();
        kakaoUriBuilderService = new KakaoUriBuilderService();
        kakaoAddressSearchService = new KakaoAddressSearchService(restTemplate, kakaoUriBuilderService);
        kakaoCategorySearchService = new RandomKakaoCategorySearchService(kakaoUriBuilderService, restTemplate);
        randomKakaoKeywordSearchService = new KakaoKeywordSearchService(kakaoUriBuilderService,restTemplate);


        randomRouteSearchService = new RandomRouteSearchService
                (
                        restTemplate,
                        kakaoAddressSearchService,
                        kakaoCategorySearchService,
                        randomDestinationRepository,
                        userRepository,
                        randomKakaoKeywordSearchService
                );
        randomOffCourseService = new RandomOffCourseService(restTemplate, kakaoAddressSearchService);
        randomKakaoRouteSearchService = new RandomKakaoRouteSearchService
                (restTemplate, kakaoAddressSearchService, kakaoCategorySearchService, randomDestinationRepository);

        {
            ReflectionTestUtils.setField(randomRouteSearchService,
                    "kakaoRestApiKey",
                    kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌

            ReflectionTestUtils.setField(kakaoAddressSearchService,
                    "kakaoRestApiKey",
                    kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (realRandomRouteService안)

            ReflectionTestUtils.setField(kakaoCategorySearchService,
                    "kakaoRestApiKey",
                    kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (realRandomRouteService안)

            ReflectionTestUtils.setField(randomOffCourseService,
                    "kakaoRestApiKey",
                    kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌

            ReflectionTestUtils.setField(randomKakaoRouteSearchService,
                    "kakaoRestApiKey",
                    kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌
        }//Set Private variable - kakaoRestApiKey

        System.out.println("프라이빗이라 테스트 안한 함수 존재");
        // 하는 방법 + 지양 이유 -> https://mangkyu.tistory.com/235
    }

    //==== RandomRouteSearchService
    // RandomRouteController 63에서만 쓰고있음
    @DisplayName("request AllRandom Way")
    @Test
    void requestAllRandom()
    {
        String address = "제주특별자치도 제주시 남성로 2";
        int pathType = 1;
        pathType = Math.max(Math.min(pathType, 3), 1);

        var result = randomRouteSearchService.requestAllRandomWay(1L, address, 5);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }
    @DisplayName("request Random Way")
    @Test
    void requestRandom()
    {
        String address = "제주특별자치도 제주시 남성로 2";
        String address2 = "제주특별자치도 제주시 남성로 22길2";
        int pathType = 1;
        pathType = Math.max(Math.min(pathType, 3), 1);

        var result = randomRouteSearchService.requestRamdomWay( address, address2, 1);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }
    //makeRequestForm

    //==== RandomOffCourseService
    @DisplayName("랜덤 네비게이션 경로 이탈 서비스")
    @Test
    void requestOutOfPath()
    {
        String address = TestAddress[0];
        double desY = waypoint[0][1];
        double desX = waypoint[0][0];
        String waypointsY = "" + waypoint[1][1];
        String waypointsX = "" + waypoint[1][0];

        var result = randomOffCourseService.requestOffCourseSearch
                (address, desY, desX, waypointsY, waypointsX);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }

    //====RandomKakaoRouteSearchService
    // RandomRouteController 74줄 에서만 쓰고 있음
    @DisplayName("request AllRandom Way")
    @Test
    void requestAllRandomKakao()
    {
        String address = TestAddress[0];
        int pathType = 1;
        pathType = Math.max(Math.min(pathType, 3), 1);

        var result = randomKakaoRouteSearchService.requestAllRandomWay(1L, address, 5);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }
    @DisplayName("request Random Way")
    @Test
    void requestRandomKakao()
    {
        String address = TestAddress[0];
        String address2 = TestAddress[1];

        var result = randomKakaoRouteSearchService.requestRamdomWay( address, address2, 1);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }

}