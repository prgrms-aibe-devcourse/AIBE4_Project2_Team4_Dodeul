package org.aibe4.dodeul.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/h2-console/**", "/consulting-applications/**")
            )
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스 및 인프라
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/favicon.ico",
                    "/error",
                    "/h2-console/**",
                    "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                    "/demo/**"
                ).permitAll()

                // 권한 우선 확인 구간
                // 아래의 permitAll보다 먼저 선언되어야 가로채기가 가능함
                .requestMatchers("/onboarding/nickname/**").authenticated()
                .requestMatchers("/post-login").authenticated()

                // 공개 비즈니스 로직
                // 로그인 없이 접근 가능한 페이지 및 API
                .requestMatchers(
                    "/",
                    "/auth/**", "/oauth2/**", "/login/oauth2/**",
                    "/api/auth/**",
                    "/onboarding/**", "/api/onboarding/**"
                ).permitAll()

                // 게시판 조회(GET)만 공개
                .requestMatchers(HttpMethod.GET, "/api/board/posts", "/board/posts").permitAll()

                // 멘토 전용 구간
                .requestMatchers(
                    "/mypage/mentor/**",
                    "/api/demo/role/mentor",
                    "/api/mentor/**"
                ).hasRole("MENTOR")

                // 상담 신청 관련 (상세조회, 수정, 삭제 포함)
                .requestMatchers("/consulting-applications/**").authenticated()

                // AI 초안 생성 API (이게 추가되어야 403 에러가 해결됩니다!)
                .requestMatchers("/api/ai/**").authenticated()

                // 멘티 전용 구간
                .requestMatchers(
                    "/mypage/mentee/**",
                    "/matchings/**",
                    "/api/demo/role/mentee",
                    "/api/mentee/**"
                ).hasRole("MENTEE")

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
                .defaultSuccessUrl("/post-login")
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/auth/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/auth/login")
            )
            .exceptionHandling(handler -> handler
                // 인증되지 않은 사용자
                .authenticationEntryPoint((request, response, authException) -> {
                    // API 요청이면 401 JSON 응답
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding("UTF-8");

                        CommonResponse<Void> errorResponse = CommonResponse.fail(ErrorCode.UNAUTHORIZED_ACCESS);
                        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                    }
                    // 화면 요청이면 로그인 페이지로 리다이렉트
                    else {
                        response.sendRedirect("/auth/login");
                    }
                })

                // 권한이 없는 사용자
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // API 요청이면 403 JSON 응답
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding("UTF-8");

                        CommonResponse<Void> errorResponse = CommonResponse.fail(ErrorCode.ACCESS_DENIED);
                        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                    }
                    // 화면 요청이면 403 에러 페이지로 리다이렉트
                    else {
                        response.sendRedirect("/error/403");
                    }
                })
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
