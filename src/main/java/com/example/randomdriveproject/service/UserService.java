package com.example.randomdriveproject.service;

import com.example.randomdriveproject.dto.kakao.KakaoAccount;
import com.example.randomdriveproject.dto.kakao.KakaoDto;
import com.example.randomdriveproject.dto.KakaoTokenDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.JsonParser;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Slf4j(topic = "UserService")
@Service
public class UserService {

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private final static String KAKAO_API_URI = "https://kapi.kakao.com";

    public  String getKaKaoLogin()
    {
        return "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=0c3c82e2bab1baa630c741b2c9f72e3c" +
                "&redirect_uri=http://localhost:8080/api/auth/login";
    }

    public KakaoDto getKakaoInfo(String code) throws Exception
    {
        if (code == null) throw new Exception("Failed get authorization code");

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type"   , "authorization_code");
            params.add("client_id"    , "0c3c82e2bab1baa630c741b2c9f72e3c");
            params.add("code"         , code);
            params.add("redirect_uri" , "http://localhost:8080/api/auth/login");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );


            {

                var temp = new KakaoTokenDto(response.getBody());
                log.info(temp.getAccess_token() + " / " + temp.getRefresh_token() + " / " + temp.getExpires_in());
                //키값은 KakaoTokenDto 참고

                accessToken = temp.getAccess_token();
                refreshToken = temp.getRefresh_token();


            }


        }catch (Exception e)
        {
            log.error(e.getMessage() + e.getStackTrace());
            throw  new Exception("API call failed" + e);
        }

        return getUserInfoWithToken(accessToken);
    }

    private void getVaildAccessToken(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        
        try {
            RestTemplate rt = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = rt.exchange(
                    KAKAO_API_URI + "/v1/user/access_token_info",
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> data = objectMapper.readValue(response.getBody(), Map.class);
            
        }catch (HttpClientErrorException e)
        {
            log.error("Not Vaild Access : " + e.getMessage() + "\n" + e.getResponseBodyAsString() + "\n" + e);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> data = objectMapper.readValue(e.getResponseBodyAsString() , Map.class);
            int stateCode = (int) data.get("code");


            switch (stateCode)
            {
                case -401:
                {
                    throw new Exception("유효하지 않거나 , 만료된 액세스 토큰");//======= Refresh Token 으로 재발급 시도
                }//유효하지 않거나 , 만료된 액세스 토큰
                case -1:
                {
                    throw new Exception("카카오 플랫폼 서비스의 일시적 내부 장애 상태");
                }//카카오 서비스 일시적 장애
                case -2:
                {
                    throw new Exception("올바른 형식으로 요청했는지 확인 필요");
                }//호출값이 잘못됨
                default:
                {
                    throw new Exception(e.getMessage());
                }//로그아웃 처리 권장
            }
        }

    }
    //refreshToekn을 추가하여 , accessToken 를 재발급 받음

    private KakaoDto getUserInfoWithToken(String accessToken) throws Exception
    {
        getVaildAccessToken(accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        //HttpHeader 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        return new KakaoDto(response);
    }

    private String getAccessFormRefresh(String refreshToekn) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //Http body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type"   , "refresh_token");
        params.add("client_id"    , "0c3c82e2bab1baa630c741b2c9f72e3c");
        params.add("refresh_token", refreshToekn);

        //HttpHeader 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        httpEntity.getBody().add("grant_type", "refresh_token");


        ResponseEntity<String> response = rt.exchange(
                KAKAO_API_URI + "/oauth/token",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(response.getBody(), Map.class);

        log.info("--> refreshed :" + data.get("access_token") + " / " + data.get("expires_in"));

        return (String) data.get("access_token");
    }//------ 확인 필요

    private void doLogout(String accessToken, boolean unlink)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        //ResponseEntity<String> response =
        restTemplate.exchange(
                KAKAO_AUTH_URI + "/v1/user/logout",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        if (unlink)
        {
            restTemplate.exchange(
                    KAKAO_AUTH_URI + "/v1/user/unlink",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
        }
    }//------ 확인 필요
}
