// src/main/java/org/aibe4/dodeul/global/security/SecurityConfig.java
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
        http.cors(Customizer.withDefaults())
            .csrf(
                csrf ->
                    csrf.ignoringRequestMatchers(
                        "/api/**", "/h2-console/**", "/consulting-applications/**"))
            .authorizeHttpRequests(
                auth ->
                    auth
                        // 정적 리소스 및 인프라
                        .requestMatchers(
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/favicon.ico",
                            "/error",
                            "/h2-console/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/demo/**")
                        .permitAll()

                        // 권한 우선 확인 구간 (permitAll보다 먼저 선언)
                        .requestMatchers("/onboarding/nickname/**")
                        .authenticated()
                        .requestMatchers("/post-login")
                        .authenticated()

                        // 공개 비즈니스 로직
                        .requestMatchers(
                            "/",
                            "/auth/**",
                            "/oauth2/**",
                            "/login/oauth2/**",
                            "/api/auth/**",
                            "/onboarding/**",
                            "/api/onboarding/**",
                            "/search/mentors",
                            "/api/search/mentors")
                        .permitAll()

                        // 게시판 조회(GET)만 공개
                        .requestMatchers(HttpMethod.GET, "/api/board/posts", "/board/posts")
                        .permitAll()

                        // 파일 업로드 API (인증 필요)
                        .requestMatchers(HttpMethod.POST, "/api/files")
                        .authenticated()

                        // 멘토 전용 구간
                        .requestMatchers(
                            "/mentor/**",
                            "/mypage/mentor/**",
                            "/api/demo/role/mentor",
                            "/api/mentor/**")
                        .hasRole("MENTOR")

                        // 상담 신청 관련 (상세조회, 수정, 삭제 포함)
                        .requestMatchers("/consulting-applications/**")
                        .authenticated()

                        // AI 초안 생성 API
                        .requestMatchers("/api/ai/**")
                        .authenticated()

                        // 멘티 전용 구간
                        .requestMatchers(
                            "/mentee/**",
                            "/mypage/mentee/**",
                            "/matchings/**",
                            "/api/demo/role/mentee",
                            "/api/mentee/**")
                        .hasRole("MENTEE")

                        .anyRequest()
                        .authenticated())
            .sessionManagement(
                session ->
                    session
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                        .invalidSessionUrl("/auth/login?expired"))
            .formLogin(
                form ->
                    form.loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/post-login")
                        .failureUrl("/auth/login?error")
                        .permitAll())
            .oauth2Login(
                oauth ->
                    oauth.loginPage("/auth/login")
                        .userInfoEndpoint(
                            userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler))
            .logout(
                logout ->
                    logout.logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/auth/login"))
            .exceptionHandling(
                handler ->
                    handler
                        .authenticationEntryPoint(
                            (request, response, authException) -> {
                                if (request.getRequestURI().startsWith("/api/")) {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.setCharacterEncoding("UTF-8");

                                    CommonResponse<Void> errorResponse =
                                        CommonResponse.fail(ErrorCode.UNAUTHORIZED_ACCESS);
                                    response
                                        .getWriter()
                                        .write(objectMapper.writeValueAsString(errorResponse));
                                } else {
                                    response.sendRedirect("/auth/login");
                                }
                            })
                        .accessDeniedHandler(
                            (request, response, accessDeniedException) -> {
                                if (request.getRequestURI().startsWith("/api/")) {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.setCharacterEncoding("UTF-8");

                                    CommonResponse<Void> errorResponse =
                                        CommonResponse.fail(ErrorCode.ACCESS_DENIED);
                                    response
                                        .getWriter()
                                        .write(objectMapper.writeValueAsString(errorResponse));
                                } else {
                                    response.sendRedirect("/error/403");
                                }
                            }));

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
