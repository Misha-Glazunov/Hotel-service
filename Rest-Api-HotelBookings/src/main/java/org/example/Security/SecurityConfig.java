package org.example.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // USER может просматривать отели и комнаты
                        .requestMatchers("/api/hotels", "/api/rooms", "/api/rooms/recommend").hasAnyRole("USER", "ADMIN")
                        // ADMIN может создавать/удалять отели и комнаты
                        .requestMatchers("/api/hotels/**", "/api/rooms/**").hasRole("ADMIN")
                        // Внутренние эндпоинты для интеграции с Booking Service
                        .requestMatchers("/api/rooms/*/confirm-availability", "/api/rooms/*/release").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}