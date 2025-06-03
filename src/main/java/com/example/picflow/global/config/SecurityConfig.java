package com.example.picflow.global.config;

import com.example.picflow.global.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/search/**").permitAll() // 검색은 누구나 가능
                        .requestMatchers("/api/posts").permitAll() // 전체 게시물 조회는 누구나 가능
                        .requestMatchers("/api/posts/{postId}").permitAll() // 특정 게시물 조회는 누구나 가능
                        .requestMatchers("/api/posts/search/**").permitAll() // 게시물 검색은 누구나 가능

                        // 정적 리소스 (혹시 모를 경우를 대비)
                        .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // OPTIONS 요청 허용 (CORS preflight)
                        .requestMatchers("OPTIONS", "/**").permitAll()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}