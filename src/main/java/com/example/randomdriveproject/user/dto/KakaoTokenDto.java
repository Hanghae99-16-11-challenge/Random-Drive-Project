package com.example.randomdriveproject.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.Map;

//변수명 바꾸지 마세요.
@Getter
public class KakaoTokenDto {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Integer expires_in;
    private String scope;
    private Integer refresh_token_expires_in;

    public static Map<String, Object> toMap(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, Map.class);
    }
    public KakaoTokenDto(String code) throws Exception {
        var value = toMap(code);

        access_token = (String) value.get("access_token");
        token_type = (String) value.get("token_type");
        refresh_token = (String) value.get("refresh_token");
        expires_in = (Integer) value.get("expires_in");
        scope = (String) value.get("scope");
        refresh_token_expires_in = (Integer) value.get("refresh_token_expires_in");
    }
}
