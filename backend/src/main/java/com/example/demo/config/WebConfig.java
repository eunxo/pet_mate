package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /uploads/** 로 시작하는 주소로 요청하면
        // 실제 서버 하드디스크의 C:/Web/uploads/ 경로에서 파일을 찾아 보여줌
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/Web/uploads/");
    }
}