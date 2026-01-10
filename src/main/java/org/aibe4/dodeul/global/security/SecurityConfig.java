package org.aibe4.dodeul.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/h2-console/**")
                .ignoringRequestMatchers("/consultations/**")
                .ignoringRequestMatchers("/ws/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/demo/role/mentor").hasRole("MENTOR")
                .requestMatchers("/api/demo/role/mentee").hasRole("MENTEE")

                // 닉네임 온보딩 플로우
                .requestMatchers("/onboarding/nickname/**").authenticated()
                .requestMatchers("/post-login").authenticated()

                .requestMatchers(
                    "/",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/icons/**",
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
                    "/consultations/**",
                    "/ws/**"
                ).permitAll()

                // 게시판(API): 비로그인 목록만 허용(상세 포함 나머지는 로그인 필요)
                .requestMatchers(HttpMethod.GET, "/api/board/posts").permitAll()
                .requestMatchers("/api/board/posts/**").authenticated()

                // 게시판(View): 비로그인 목록만 허용(상세/작성/등록은 로그인 필요)
                .requestMatchers(HttpMethod.GET, "/board/posts").permitAll()
                .requestMatchers(HttpMethod.POST, "/board/posts").authenticated()
                .requestMatchers("/board/posts/**").authenticated()

                .requestMatchers("/mypage/mentor/**").hasRole("MENTOR")
                .requestMatchers("/mypage/mentee/**", "/matchings/**").hasRole("MENTEE")

                // API 역할 분리
                .requestMatchers("/api/mentor/**").hasRole("MENTOR")
                .requestMatchers("/api/mentee/**").hasRole("MENTEE")

                // 나머지 마이페이지/API는 로그인 필요
                .requestMatchers("/mypage/**", "/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                .invalidSessionUrl("/auth/login?expired")
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/post-login", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/auth/login")
            );

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
