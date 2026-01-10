package org.aibe4.dodeul.global.security;

import java.util.List;
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
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        CustomOAuth2UserService customOAuth2UserService,
        OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler
    ) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf ->
                csrf.ignoringRequestMatchers("/api/**", "/h2-console/**")
                    .ignoringRequestMatchers("/consultations/**")
                    .ignoringRequestMatchers("/ws/**")
            )
            .authorizeHttpRequests(auth ->
                auth
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
                        "/oauth2/**",
                        "/login/oauth2/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form ->
                form.loginPage("/auth/login")
                    .defaultSuccessUrl("/post-login", true)
                    .permitAll()
            )
            .oauth2Login(oauth2 ->
                oauth2
                    .loginPage("/auth/login")
                    .userInfoEndpoint(userInfo ->
                        userInfo.userService(customOAuth2UserService)
                    )
                    .successHandler(oAuth2LoginSuccessHandler)
            )
            .logout(logout ->
                logout.logoutUrl("/logout")
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
