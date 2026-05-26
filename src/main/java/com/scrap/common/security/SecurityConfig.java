package com.scrap.common.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtResponseFilter jwtResponseFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ ALLOW OPTIONS (IMPORTANT)
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // ✅ AUTH APIs
                        .requestMatchers(
                                "/api/user/login",
                                "/api/user/signup",
                                "/api/user/forgotpassword",
                                "/api/user/logout"
                        ).permitAll()

                        // ✅ SWAGGER
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ✅ PUBLIC APIs
                        .requestMatchers(

                                "/ws/**",

                                "/api/user/profile/**",
                                "/api/user/updateprofile/**",
                                "/api/user/bank/**",
                                "/api/user/verify/**",
                                "/api/user/user/profile",

                                "/api/scraporders/**",
                                "/api/customersell/**",

                                "/api/prices/**",
                                "/api/transactions/**",
                                "/api/inventory/**",

                                "/api/scrapyard/**",
                                "/api/scrapyard/invoice/**",

                                "/api/payment/**",

                                "/api/delivery/**",
                                "/api/reviews/**",

                                "/api/orders/**",
                                "/api/scrapyard/getallproducts",

                                "/uploads/**"

                        ).permitAll()

                        // ✅ EVERYTHING ELSE SECURED
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.addFilterAfter(
                jwtResponseFilter,
                JwtAuthenticationFilter.class
        );

        return http.build();
    }

    // ✅ CORS CONFIG
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                List.of("http://localhost:3000")
        );

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {

        return authenticationConfiguration
                .getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}