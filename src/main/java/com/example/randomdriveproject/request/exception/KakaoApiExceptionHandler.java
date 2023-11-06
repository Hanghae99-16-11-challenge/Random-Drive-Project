package com.example.randomdriveproject.request.exception;

import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.function.Supplier;

@Slf4j
public class KakaoApiExceptionHandler { // REST API 호출을 위해 restTemplate.exchange 부분 예외처리

    public static KakaoApiResponseDto handleApiCall(Supplier<KakaoApiResponseDto> apiCall, URI requestUri) {

        // 요청 시작 시 로그 남기기
        log.info("카카오 API 호출 시작: {}", requestUri);
        try {
            KakaoApiResponseDto response = apiCall.get();

            if (response == null) {
                log.error("카카오 API로부터 null 응답 받음");
                throw new RuntimeException("카카오 API 응답 문제");
            }

            // 요청 성공 시 로그 남기기
            log.info("카카오 API 호출 성공: {}", requestUri);
            return response;

        } catch (HttpStatusCodeException e) {
            log.error("카카오 API 호출 중 HTTP 오류 발생, 상태 코드: {}, 오류 본문: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("카카오 API HTTP 오류", e);

        } catch (RestClientException e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new RuntimeException("카카오 API 호출 실패", e);
        }
    }
}
