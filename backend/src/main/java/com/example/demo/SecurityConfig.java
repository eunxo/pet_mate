package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 설정을 활성화합니다.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 외부(Vue)에서 오는 POST 요청(회원가입)을 허용하기 위해 비활성화합니다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 아래 작성한 CORS 설정을 적용합니다.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 개발 단계이므로 모든 페이지 접속을 일단 허용합니다.
                );
        return http.build();
    }

    // Vue(5173)와 스프링(8080) 사이의 포트 차이를 극복하는 설정입니다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Vue 주소를 허용합니다.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 주소에 대해 위 설정을 적용합니다.
        return source;
    }
}