package com.example.randomdriveproject.request;

import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.KakaoKeywordSearchService;
import com.example.randomdriveproject.request.service.KakaoUriBuilderService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
import com.example.randomdriveproject.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

@EnableConfigurationProperties//테스트에서 property를 사용
@SpringBootTest()//모든 빈 사용
public class requestServiceTest {

    KakaoAddressSearchService kakaoAddressSearchService;
    KakaoKeywordSearchService kakaoKeywordSearchService;
    KakaoUriBuilderService kakaoUriBuilderService;
    RandomKakaoCategorySearchService categorySearchService;

    RestTemplate restTemplate;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @BeforeEach
    void setup()
    {
        kakaoUriBuilderService = new KakaoUriBuilderService();//URL 조합 서비스
        restTemplate = new RestTemplate();

        kakaoAddressSearchService = new KakaoAddressSearchService(restTemplate, kakaoUriBuilderService);
        kakaoKeywordSearchService = new KakaoKeywordSearchService(kakaoUriBuilderService, restTemplate);
        categorySearchService = new RandomKakaoCategorySearchService(kakaoUriBuilderService, restTemplate);

        ReflectionTestUtils.setField(kakaoAddressSearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌
        ReflectionTestUtils.setField(kakaoKeywordSearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌
        ReflectionTestUtils.setField(categorySearchService,
                "kakaoRestApiKey",
                kakaoRestApiKey);//@Vaild 가 작동이 안되 , 리플렉션으로 넣어줌
    }

    @DisplayName("주소 to 위경도 변환")
    @Test
    void addressSearch()
    {
        String address = "제주특별자치도 제주시 남성로 2";
        
        var result = kakaoAddressSearchService.requestAddressSearch(address);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getDocumentDtoList().get(0).getLongitude(), 126.513124645234);
        Assertions.assertEquals(result.getDocumentDtoList().get(0).getLatitude(), 33.5004439520997);
    }
    
    @DisplayName("키워드 검색")
    @Test
    void keywordSearch()
    {
        String keyword = "카카오";

        var result = kakaoKeywordSearchService.requestAttractionKeywordSearch(keyword);

        if (result.getDocumentDtoList().size() > 0)
        {
            System.out.println("Size : " + result.getDocumentDtoList().size() + " / 0 : "
                    + result.getDocumentDtoList().get(0).getAddressName());
        }

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getDocumentDtoList().isEmpty());
    }

    @DisplayName("URL 인코드")
    @Test
    void urlEncode()
    {
        String address = "제주특별자치도 제주시 남성로 2";

        var result = kakaoUriBuilderService.buildUriByAddressSearch(address);

        System.out.println(result.toString());

        Assertions.assertNotNull(result);
    }

    @DisplayName("길찾기 주소 생성")
    @Test
    void buildRouteSearchUrl()
    {
        String address = "제주특별자치도 제주시 남성로 2";
        String address2 = "제주특별자치도 제주시 남성로 4";

        var result = kakaoUriBuilderService.buildUriByRouteSearch(address, address2);

        Assertions.assertFalse(result.toString().isEmpty());
    }

    @DisplayName("카테고리 검색 주소 생성")
    @Test
    void buildCategorySearchUrl()
    {
        double lng = 126.513124645234;
        double lat = 33.5004439520997;
        double radius = 5;
        String CULTURE_CATEGORY = "CT1";

        var result = kakaoUriBuilderService.buildUriByCategorySearch(lng, lat, radius, CULTURE_CATEGORY);

        Assertions.assertFalse(result.toString().isEmpty());
    }

    @DisplayName("경로 재생성 주소 생성")
    @Test
    void buildRereouteSearchUrl()
    {
        double lng = 126.513124645234;
        double lat = 33.5004439520997;

        try {
            double startCoord = Double.parseDouble(lat + "," + lng);//실제로 이렇게 씀  (navigation>basic>service>KakaoRouteSearchService 89줄)

            var result = kakaoUriBuilderService.buildUriByReRouteSearch(startCoord);

            Assertions.assertFalse(result.toString().isEmpty());
        }catch (Exception e)
        {
            // 실제로 startCoord = Double.parseDouble(lat + "," + lng); 이렇게 씀 안될껀데....
            throw new NumberFormatException("double형에   , 가 있어요.");
        }

    }

    @DisplayName("키워드 주소 생성")
    @Test
    void buildKeywordSearchUrl()
    {
        String keyword = "카카오";

        var result = kakaoUriBuilderService.buildUriByKeywordSearch(keyword);

        Assertions.assertFalse(result.toString().isEmpty());
    }
    //kakaoUriBuilderService.buildUriByKeywordSearch//단순 URL 조합 함수

    @DisplayName("주변 카테고리 위치")
    @Test
    void requestCategorySearch()
    {
        double lng = 126.513124645234;
        double lat = 33.5004439520997;
        double radius = 5;

        var result = categorySearchService.requestAttractionCategorySearch(lng, lat, radius);

        if (!result.getDocumentDtoList().isEmpty())
        {
            System.out.println("Size : " + result.getDocumentDtoList().size() + " / [0] : " + result.getDocumentDtoList().get(0).getAddressName());
        }

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getDocumentDtoList().isEmpty());
    }
    @DisplayName("주변 카테고리 위치 - 미사용")
    @Test
    void requestCategorySearch_NoUsages()
    {
        double lng = 126.513124645234;
        double lat = 33.5004439520997;
        double radius = 5;

        var result = categorySearchService.requestCultureCategorySearch(lng, lat, radius);

        if (!result.getDocumentDtoList().isEmpty())
        {
            System.out.println("Size : " + result.getDocumentDtoList().size() + " / [0] : " + result.getDocumentDtoList().get(0).getAddressName());
        }

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getDocumentDtoList().isEmpty());
    }
}
