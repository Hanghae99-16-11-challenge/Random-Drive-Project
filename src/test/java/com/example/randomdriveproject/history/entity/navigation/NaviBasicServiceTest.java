package com.example.randomdriveproject.history.entity.navigation;

import com.example.randomdriveproject.navigation.basic.service.KakaoRouteSearchService;
import com.example.randomdriveproject.navigation.basic.service.KeywordSearchService;
import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
import com.example.randomdriveproject.navigation.random.service.RealRandomRouteSearchService;
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

@DisplayName("Navigation Basic Service 테스트")
public class NaviBasicServiceTest {
    RestTemplate restTemplate;
    KakaoUriBuilderService kakaoUriBuilderService;

    KakaoAddressSearchService kakaoAddressSearchService;
    RandomKakaoCategorySearchService kakaoCategorySearchService;
    KakaoKeywordSearchService kakaoKeywordSearchService;


    @MockBean
    UserRepository userRepository;
    //    @Autowired

    String[] TestAddress = {"제주특별자치도 제주시 남성로 2",
            "제주특별자치도 제주시 남성로 22길2",
            "제주특별자치도 제주시 조천읍 와산서1길 32",
            "제주특별자치도 제주시 애월읍 고내로9길 25"};

    // [[x,y]...[x,y]]
    double[][] waypoint = {{126.53001226699159, 33.50674031248494},
            {126.51580214859632, 33.504461199016994},
            {126.49423334018748, 33.48096748115865}};

    KeywordSearchService keywordSearchService;
    KakaoRouteSearchService kakaoRouteSearchService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @BeforeEach
    void setup()
    {
        restTemplate = new RestTemplate();
        kakaoUriBuilderService = new KakaoUriBuilderService();
        kakaoAddressSearchService = new KakaoAddressSearchService(restTemplate, kakaoUriBuilderService);
        kakaoCategorySearchService = new RandomKakaoCategorySearchService(kakaoUriBuilderService, restTemplate);
        kakaoKeywordSearchService = new KakaoKeywordSearchService(kakaoUriBuilderService, restTemplate);

        keywordSearchService = new KeywordSearchService(kakaoKeywordSearchService);
        kakaoRouteSearchService = new KakaoRouteSearchService
                (restTemplate, kakaoUriBuilderService, kakaoAddressSearchService, userRepository);

        ReflectionTestUtils.setField(kakaoAddressSearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (kakaoRouteSearchService안)

        ReflectionTestUtils.setField(kakaoCategorySearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (kakaoRouteSearchService안)

        ReflectionTestUtils.setField(kakaoKeywordSearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (keywordSearchService 안)
        
        ReflectionTestUtils.setField(kakaoRouteSearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌

    }
    
    @DisplayName("키워드 To 주소")
    @Test
    void requestKeywordRandomWay()
    {
        var result = keywordSearchService.requestKeywordRandomWay("카카오");

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }


    //KakaoRouteSearchService

    @DisplayName("길 생성")
    @Test
    void requestRoute()
    {
        var result = kakaoRouteSearchService.requestRouteSearch(TestAddress[0], TestAddress[1]);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }

    @DisplayName("길 재생성")
    @Test
    void requestRoute_ReSearch()
    {
        var result = kakaoRouteSearchService.requestRouteReSearch(waypoint[0][1], waypoint[0][0]);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }
}
