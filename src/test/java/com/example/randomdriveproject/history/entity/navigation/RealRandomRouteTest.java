package com.example.randomdriveproject.history.entity.navigation;

import com.example.randomdriveproject.navigation.random.entity.RandomDestination;
import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
import com.example.randomdriveproject.navigation.random.service.RealRandomRouteSearchService;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.KakaoUriBuilderService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@EnableConfigurationProperties//테스트에서 property를 사용
@SpringBootTest//모든 빈 사용

@DisplayName("'Real' RandomRouteSearchService 테스트")
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
public class RealRandomRouteTest {

    //SpringBootTest vs DataJpaTest : https://cobbybb.tistory.com/23

    RestTemplate restTemplate;
    KakaoUriBuilderService kakaoUriBuilderService;

    KakaoAddressSearchService kakaoAddressSearchService;
    RandomKakaoCategorySearchService kakaoCategorySearchService;

//    @Autowired
    @MockBean//가상 객체
    RandomDestinationRepository randomDestinationRepository;

    RealRandomRouteSearchService realRandomRouteService;


    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @BeforeEach
    void setup()
    {
        restTemplate = new RestTemplate();
        kakaoUriBuilderService = new KakaoUriBuilderService();
        kakaoAddressSearchService = new KakaoAddressSearchService(restTemplate, kakaoUriBuilderService);
        kakaoCategorySearchService = new RandomKakaoCategorySearchService(kakaoUriBuilderService, restTemplate);

        realRandomRouteService = new RealRandomRouteSearchService(restTemplate, kakaoAddressSearchService, kakaoCategorySearchService, randomDestinationRepository);

        ReflectionTestUtils.setField(realRandomRouteService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌

        ReflectionTestUtils.setField(kakaoAddressSearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (realRandomRouteService안)

        ReflectionTestUtils.setField(kakaoCategorySearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌 + (realRandomRouteService안)
        
        System.out.println("프라이빗이라 테스트 안한 함수 존재");
        // 하는 방법 + 지양 이유 -> https://mangkyu.tistory.com/235
    }

    @DisplayName("DB 확인")
    @Test
    void RepositoryTest()
    {
        System.out.println("Size : "+ randomDestinationRepository.count());
    }

    @DisplayName(" '리얼'랜덤길서비스 - 반경 기반 랜덤 길찾기")
    @Test
    void requestAllRandomWay()
    {
        String address = "제주특별자치도 제주시 남성로 2";
        int pathType = 1;
        pathType = Math.max(Math.min(pathType, 3), 1);

        var result = realRandomRouteService.requestAllRandomWay(1L, address, 5, 1, pathType);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }

    @DisplayName(" '리얼'랜덤길서비스 - 목적지 기반 랜덤 길찾기")
    @Test
    void requestRandomWay()
    {
        String address = "제주특별자치도 제주시 남성로 2";
        String address2 = "제주특별자치도 제주시 남성로 22길2";
        int pathType = 1;
        pathType = Math.max(Math.min(pathType, 3), 1);

        var result = realRandomRouteService.requestRandomWay( address, address2, 1, pathType);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRoutes().length > 0);
    }
    
    //프라이빗 이여서 미작성
    //makeRequestForm
    //getDestination
    //getWayPointsAroundLine
    //getWayPointsInBox
    //getWayPointsCircular
    //getRandomAngle
    //calculateDistance
    //getRandomWayPoint
}
