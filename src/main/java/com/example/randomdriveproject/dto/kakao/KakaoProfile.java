package com.example.randomdriveproject.dto.kakao;

import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoProfile {
    private String nickname;

    public KakaoProfile(Map<String, Object> data)
    {
        nickname = (String) data.get("nickname");
    }
}
