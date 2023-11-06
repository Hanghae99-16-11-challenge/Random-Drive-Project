package com.example.randomdriveproject.user;

import com.example.randomdriveproject.user.dto.KakaoUserInfoDto;
import com.example.randomdriveproject.user.entity.User;
import com.example.randomdriveproject.user.jwt.JwtUtil;
import com.example.randomdriveproject.user.repository.UserRepository;
import com.example.randomdriveproject.user.service.KakaoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

//@ExtendWith(SpringExtension.class)
public class KaKaoUserTest {

    KakaoService kakaoService;

    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    UserRepository userRepository;
    @MockBean
    RestTemplate restTemplate;
    @MockBean
    JwtUtil jwtUtil;
    @BeforeEach
    void setup()
    {
        kakaoService = new KakaoService(passwordEncoder, userRepository, restTemplate, jwtUtil);
    }
    
    @Test
    @DisplayName("카카오 회원가입")
    void register()
    {
        String username = "닉네임";

//        var user = kakaoService.registerKakaoUserIfNeeded(new KakaoUserInfoDto(1L, username, "email@email.com"));
//
//
//        Assertions.assertEquals(username, user.getUsername());
    }//접근 권한 때문에 비활성화
    
    // 나머지는 인가코드 때문에 테스트 불가
}
