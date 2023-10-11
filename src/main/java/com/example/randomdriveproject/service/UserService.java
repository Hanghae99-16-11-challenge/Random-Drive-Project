package com.example.randomdriveproject.service;

import com.example.randomdriveproject.dto.KakaoDto;
import com.example.randomdriveproject.dto.KakaoTokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Slf4j(topic = "UserService")
@Service
public class UserService {

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";

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
//                var map = toMap(response.getBody());
//                log.info(map.get("access_token").toString() + "/" + map.get("refresh_token").toString());

                var temp = new KakaoTokenDto(response.getBody());
                log.info(temp.getAccess_token() + " / " + temp.getRefresh_token() + " / " + temp.getExpires_in());
                //키값은 KakaoTokenDto 참고
            }


        }catch (Exception e)
        {
            log.error(e.getMessage() + e.getStackTrace());
            throw  new Exception("API call failed" + e);
        }

        return null;
    }

}
