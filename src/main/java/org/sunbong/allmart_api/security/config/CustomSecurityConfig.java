package org.sunbong.allmart_api.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.sunbong.allmart_api.security.filter.JWTCheckFilter;
import org.sunbong.allmart_api.security.util.JWTUtil;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.sunbong.allmart_api.social.service.CustomOAuth2UserService;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class CustomSecurityConfig implements WebMvcConfigurer {

    private final JWTUtil jwtUtil;

    private final AuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {

        http.formLogin(config -> config.disable());

        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.NEVER));

        http.csrf(config -> config.disable());

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

//        http.addFilterBefore(new JWTCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

//        http.authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/uploads/**").permitAll() // /uploads/** 경로 허용
//                .requestMatchers("/api/v1/member/signUp", "/api/v1/member/makeToken",
//                        "api/v1/mart/**").permitAll()
//                .requestMatchers("/api/v1/**").hasRole("MARTADMIN") // /api/v1/** 경로는 관리자 권한만 접근 가능
//                .anyRequest().authenticated()
//        );

        // 카카오톡 로그인 엔드포인트 설정
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("http://localhost:5173/customer/signin") // 사용자 정의 로그인 페이지 설정 (필요하면 변경)"
                .successHandler((request, response, authentication) -> {
                    // 인증 성공 시 React로 리디렉션
                    String redirectUrl = "http://localhost:5173/oauth/kakao";
                    response.sendRedirect(redirectUrl);
                })
                .failureHandler(customAuthenticationFailureHandler)
                .authorizationEndpoint(auth -> auth
                        .baseUri("/oauth2/authorization")) // 기본 OAuth2 인증 URL
                .tokenEndpoint(token -> token
                        .accessTokenResponseClient(customAccessTokenResponseClient  ())) // Access Token 처리
                .userInfoEndpoint(userInfo  -> userInfo
                        .userService(customOAuth2UserService) // 사용자 정보 처리 서비스

                ));

        return http.build();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> customAccessTokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of("https://allmartservice.shop","http://localhost:5173","https://allmartsystem.shop")); // 어디서든 허락
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD","PATCH", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setExposedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;

    }


}
