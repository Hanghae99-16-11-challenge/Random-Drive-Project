//package com.example.randomdriveproject.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//
//@Slf4j(topic = "Web Config")
//@Configuration
//public class CORSConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry)
//    {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
//                .allowedHeaders("*")
//                .allowCredentials(true)//프론트에서도 axios > allowCredentials 허용해야함
//                .maxAge(3600);
//        //로그인후 request응답 헤더에 Origin, Access-Control-Request-Method,Access-Control-Request-Headers이 필요
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource()
//    {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.addAllowedOriginPattern("*");
//
//        configuration.addAllowedMethod("*");
//        configuration.addAllowedMethod("GET");
//        configuration.addAllowedMethod("POST");// * 이 있지만 혹시나 해서 넣어봄
//
//        configuration.addAllowedHeader("*");
//        configuration.setAllowCredentials(true);
//
//
//        return new CorsConfigurationSource() {
//            @Override
//            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//
//                log.info("--- " + request.getRequestURI()  + " / " + request.getMethod() + " / " + request.getProtocol() + " / " + request.getHeader("Origin")
//                        + "\n" + request.getContextPath() + " / " + request.getRemoteUser() + " | "  + request.getServletPath() + " | " + request.getRemoteHost());
//                return configuration;
//            }
//        };
//    }//프론트엔드에서 CORS 가 유효하면 , 여기로 옴
//}
