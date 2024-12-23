package com.example.meet.common.config;

import com.example.meet.common.auth.JwtTokenProvider;
import com.example.meet.common.filter.JwtAuthenticationFilter;
import com.example.meet.common.filter.RequestBodyCachingFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityFilterChain result = http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L); //1시간
                        return config;
                    }
                }))
                // CSRF 보호 해제
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> {
                    headers.addHeaderWriter(new ContentSecurityPolicyHeaderWriter(
                            "default-src 'self'; script-src 'self' 'unsafe-inline'; object-src 'none'; frame-ancestors 'self' http://localhost:5173;"));
                })
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/auth/login","/auth/token/refresh").permitAll()
                        .requestMatchers("/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
                         "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**",
                         "/webjars/**", "/swagger-ui.html", "/api-reference", "/favicon.ico").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers("/batch/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();

        return result;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<RequestBodyCachingFilter> requestBodyCachingFilter() {
        FilterRegistrationBean<RequestBodyCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestBodyCachingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

}
