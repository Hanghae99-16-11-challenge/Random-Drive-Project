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
//    @MockBean//가상 객체
//            RandomDestinationRepository randomDestinationRepository;

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
        ReflectionTestUtils.setField(kakaoRouteSearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌

    }
    
    //@DisplayName("")//설명 없음
    @Test
    void requestKeywordRandomWay()
    {
        keywordSearchService.requestKeywordRandomWay("카카오");
    }
}
