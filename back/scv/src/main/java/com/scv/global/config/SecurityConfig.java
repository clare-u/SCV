package com.scv.global.config;

import com.scv.domain.oauth2.handler.CustomLoginFailureHandler;
import com.scv.domain.oauth2.handler.CustomLoginSuccessHandler;
import com.scv.domain.oauth2.service.CustomOAuth2UserService;
import com.scv.global.filter.CustomJwtLogoutFilter;
import com.scv.global.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomJwtLogoutFilter customJwtLogoutFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // cors 설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setMaxAge(3600L);

            configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
            configuration.setExposedHeaders(Collections.singletonList("Authorization"));

            return configuration;
        }));

        // csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        // From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        // JwtFilter 추가
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 로그아웃 필터 추가
        http.addFilterBefore(customJwtLogoutFilter, LogoutFilter.class);

        // oauth2
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customLoginFailureHandler));

        // 경로별 인가 작업
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated());

        // 세션 설정 : STATELESS
        http.sessionManagement(session -> session.
                sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
