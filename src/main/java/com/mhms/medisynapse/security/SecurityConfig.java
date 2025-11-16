package com.mhms.medisynapse.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/lab-reports/**"  // Allow access to uploaded lab reports
                        ).permitAll()

                        // Super Admin only endpoints
                        .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")

                        // Hospital Admin endpoints
                        .requestMatchers("/api/hospital-admin/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN")

                        // Doctor endpoints
                        .requestMatchers("/api/doctors/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "DOCTOR")

                        // Download Report endpoints
                        .requestMatchers("/api/v1/reports/download", "/api/v1/reports/*/download").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "RECEPTIONIST")

                        // Billing endpoints
                        .requestMatchers("/api/v1/billing/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST")

                        // Receptionist management endpoints
                        .requestMatchers("/api/v1/receptionists/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN")

                        // Receptionist profile endpoint
                        .requestMatchers("/api/v1/receptionist/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "RECEPTIONIST")

                        // Patient endpoints
                        .requestMatchers("/api/patients/**").hasAnyRole("SUPER_ADMIN", "HOSPITAL_ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PATIENT")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
