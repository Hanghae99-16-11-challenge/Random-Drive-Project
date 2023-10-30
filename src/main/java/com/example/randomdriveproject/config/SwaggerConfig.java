package com.example.randomdriveproject.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi naviApi() {
        return GroupedOpenApi.builder()
                .group("navi api") // 컨트롤러 그룹의 이름
                .pathsToMatch("/api/**")
                .build();
    }
    @Bean
    public GroupedOpenApi homeApi() {
        return GroupedOpenApi.builder()
                .group("home api") // 컨트롤러 그룹의 이름
                .pathsToMatch("/view/**")
                .build();
    }




}
