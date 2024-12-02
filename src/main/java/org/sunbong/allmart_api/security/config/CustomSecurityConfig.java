package org.sunbong.allmart_api.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.sunbong.allmart_api.security.filter.JWTCheckFilter;
import org.sunbong.allmart_api.security.util.JWTUtil;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class CustomSecurityConfig implements WebMvcConfigurer {

    private final JWTUtil jwtUtil;



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin(config -> config.disable());

        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.NEVER));

        http.csrf(config -> config.disable());

//        http.addFilterBefore(new JWTCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

//        http.authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/api/v1/customer/makeToken","/api/v1/customer/signUp/phoneNumber/**",
//                        "/api/v1/qrcode/signUp", "/api/v1/customer/signIn", "/api/v1/customer/**").permitAll()
//                .requestMatchers("/api/v1/**").hasRole("USER") // /api/v1/** 경로는 관리자 권한만 접근 가능
//
//
//                .anyRequest().authenticated()
//        );
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

//        http.authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/uploads/**").permitAll() // /uploads/** 경로 허용
//                .requestMatchers("/api/v1/member/signUp", "/api/v1/member/makeToken",
//                        "api/v1/mart/**").permitAll()
//                .requestMatchers("/api/v1/**").hasRole("MARTADMIN") // /api/v1/** 경로는 관리자 권한만 접근 가능
//                .anyRequest().authenticated()
//        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*")); // 어디서든 허락
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 이미지 요청을 처리할 경로
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/upload/"); // 실제 업로드 경로
    }
}
