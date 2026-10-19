
package com.campusconnect.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .authorizeHttpRequests(auth -> auth

                                                // Public APIs for local development
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/hello",
                                                                "/api/students",
                                                                "/api/requests",
                                                                "/api/auth/csrf")
                                                .permitAll()

                                                // Allow login requests
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/auth/login")
                                                .permitAll()

                                                // Other endpoints require authentication
                                                .anyRequest().authenticated())

                                // We will implement our own JSON login endpoint
                                .formLogin(form -> form.disable())

                                // Disable HTTP Basic login
                                .httpBasic(basic -> basic.disable());

                return http.build();
        }
}
