package com.example.randomdriveproject.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi myApi() {
        return GroupedOpenApi.builder()
                .group("경로 추천 api") // 컨트롤러 그룹의 이름
                .pathsToMatch("/api/**") // 해당 패턴을 사용하여 컨트롤러를 그룹화
                .build();
    }




}
