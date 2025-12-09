package com.example.tak.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        log.info("SecurityConfig 초기화 시작");
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 기능 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 페이지 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ta-kiosk/ai-agent/**", "/ws/kiosk/**").permitAll()
                        .anyRequest().permitAll()
                )
                .build();
    }
}
