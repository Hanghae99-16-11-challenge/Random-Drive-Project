package com.example.randomdriveproject.user.dto.kakao;

import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoAccount {
    private boolean profile_nickname_needs_agreement;
    private KakaoProfile profile;
    private boolean has_email;
    private boolean email_needs_agreement;
    private boolean is_email_valid;
    private boolean is_email_verified;
    private String email;

    public KakaoAccount(Map<String, Object> data)
    {
        profile_nickname_needs_agreement = (boolean) data.get("profile_nickname_needs_agreement");

        profile = new KakaoProfile((Map<String, Object>) data.get("profile"));

        has_email = (boolean) data.get("has_email");
        email_needs_agreement = (boolean) data.get("email_needs_agreement");
        is_email_valid = (boolean) data.get("is_email_valid");
        is_email_verified = (boolean) data.get("is_email_verified");
        email = (String) data.get("email");
    }
}
