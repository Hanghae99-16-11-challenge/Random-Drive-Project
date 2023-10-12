package com.example.randomdriveproject.service;

import com.example.randomdriveproject.dto.kakao.KakaoAccount;
import com.example.randomdriveproject.dto.kakao.KakaoDto;
import com.example.randomdriveproject.dto.KakaoTokenDto;
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
    public static Map<String, Object> toMap(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, Map.class);
    }
    public static String getProperty(String json)
    {
        //{nickname=이름} 이렇게 들어옴
        //kakao_account 도

        String content = json.substring(1, json.length() - 1);

        return  content.split("=")[1];
    }
    public static Map<String, String> toMap_Property(String json)
    {
        Map<String, String> map = new HashMap<>();
        for (String pair : json.split(",")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                map.put(key, value);
            }
        }

        return map;
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

    private KakaoDto getUserInfoWithToken(String accessToken) throws Exception
    {
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
}
