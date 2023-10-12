package com.example.randomdriveproject.dto.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class KakaoDto {

    private long id;
    private boolean has_signed_up;
    private String connected_at;// 2023-10-11T07:45:32Z , UTC기준으로 한국시간(KST)와 9시간 차이
    private String synched_at;
    private LinkedHashMap<String, String> properties;
    private KakaoAccount kakao_account;
    //for_partner

    public KakaoDto(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(response.getBody(), Map.class);

        id = (Long) data.get("id");
        //has_signed_up = (boolean) data.get("has_signed_up");
        connected_at = (String) data.get("connected_at");
        synched_at = (String) data.get("synched_at");
        properties = (LinkedHashMap) data.get("properties");

        kakao_account = new KakaoAccount((Map<String, Object>) data.get("kakao_account"));

    }
}