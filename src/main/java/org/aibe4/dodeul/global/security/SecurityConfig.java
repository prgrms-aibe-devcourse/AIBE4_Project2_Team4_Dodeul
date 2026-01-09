package org.aibe4.dodeul.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
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
            .csrf(
                csrf ->
                    csrf.ignoringRequestMatchers("/api/**", "/h2-console/**")
                        .ignoringRequestMatchers("/consultations/**")
                        .ignoringRequestMatchers("/ws/**")) // 웹소켓 엔드포인트 CSRF 제외

            // URL 권한
            .authorizeHttpRequests(
                auth ->
                    auth
                        // demo role 테스트 보호 (ApiController 기준)
                        .requestMatchers("/api/demo/role/mentor")
                        .hasRole("MENTOR")
                        .requestMatchers("/api/demo/role/mentee")
                        .hasRole("MENTEE")

                        // 공개 허용
                        // 공개 허용
                        .requestMatchers(
                            "/",
                            "/error",
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/icons/**", // 추가
                            "/favicon.ico",
                            "/auth/**",
                            "/onboarding/**",
                            "/api/auth/**",
                            "/api/onboarding/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/oauth2/**",
                            "/login/oauth2/**",
                            "/h2-console/**",
                            "/demo/**",
                            "/api/board/posts",
                            "/demo/**",
                            "/api/board/posts",
                            "/api/board/posts/**",
                            "/consultations/**",
                            "/ws/**") // 웹소켓 엔드포인트 허용
                        .permitAll()

                        // 역할 기반 (mypage)
                        .requestMatchers("/mypage/mentor/**")
                        .hasRole("MENTOR")
                        .requestMatchers("/mypage/mentee/**")
                        .hasRole("MENTEE")

                        // API 역할 분리
                        .requestMatchers("/api/mentor/**")
                        .hasRole("MENTOR")
                        .requestMatchers("/api/mentee/**")
                        .hasRole("MENTEE")
                        .requestMatchers("/mypage/**", "/api/**")
                        .authenticated()
                        .anyRequest()
                        .authenticated())
            .sessionManagement(
                session ->
                    session.sessionFixation(
                            sessionFixation -> sessionFixation.migrateSession())
                        .invalidSessionUrl("/auth/login?expired"))
            .formLogin(
                form ->
                    form.loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll())
            .logout(
                logout ->
                    logout.logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/auth/login"));

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
