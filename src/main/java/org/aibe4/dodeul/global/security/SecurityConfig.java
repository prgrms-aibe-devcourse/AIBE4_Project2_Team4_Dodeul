package org.aibe4.dodeul.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS
            .cors(Customizer.withDefaults())

            // CSRF: UI는 보호, API는 제외(개발 편의)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

            // URL 권한
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/auth/**",
                    "/api/auth/**",

                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",

                    "/oauth2/**",
                    "/login/oauth2/**",
                    "/login",
                    "/h2-console/**",

                    // board 공개 조회 permitAll (dev 최신 반영)
                    "/api/board/posts",
                    "/api/board/posts/**"
                ).permitAll()

                // 역할 기반
                .requestMatchers("/mypage/mentor/**").hasRole("MENTOR")
                .requestMatchers("/mypage/mentee/**").hasRole("MENTEE")

                // API 역할 분리 시
                .requestMatchers("/api/mentor/**").hasRole("MENTOR")
                .requestMatchers("/api/mentee/**").hasRole("MENTEE")

                .requestMatchers("/mypage/**", "/api/**").authenticated()
                .anyRequest().authenticated()
            )

            // 세션 관리
            .sessionManagement(session -> session
                .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                .invalidSessionUrl("/auth/login?expired")
            )

            // 로그인/로그아웃
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/mypage/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/auth/login")
            );

        // H2 console iframe 차단 방지
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    // 개발용 CORS (배포 전 도메인 제한)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*")); // TODO: 배포 시 도메인 제한
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 멘토/멘티 테스트 계정 (임시)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername("mentor@test.com")
                .password(passwordEncoder.encode("1234"))
                .roles("MENTOR")
                .build(),
            User.withUsername("mentee@test.com")
                .password(passwordEncoder.encode("1234"))
                .roles("MENTEE")
                .build()
        );
    }
}
