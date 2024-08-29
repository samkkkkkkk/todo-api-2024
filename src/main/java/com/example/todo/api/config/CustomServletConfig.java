package com.example.todo.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // CORS 설정을 적용할 url
                .allowedOrigins("*") // 자원 공유를 허락할 origin을 설정 (origin: 프로토콜, ip주소, 포트번호)
                .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS") // 요청 방식
                .maxAge(300) // 원하는 시간만큼 기존에 허락했던 요청 정보를 기억할 시간
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type"); // 외부에서 들어오는 해더 정보들
    }
}
