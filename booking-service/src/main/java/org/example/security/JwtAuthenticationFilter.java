package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null && jwtUtil.validateToken(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                System.out.println("=== JWT AUTHENTICATION ===");
                System.out.println("Username: " + username);
                System.out.println("Role from token: " + role);

                // Проверяем, что роль не null
                if (role != null) {
                    // ВАЖНО: Приводим роль к верхнему регистру
                    String normalizedRole = role.toUpperCase();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + normalizedRole))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication SUCCESS for user: " + username);
                    System.out.println("Granted authority: ROLE_" + normalizedRole);
                } else {
                    System.out.println("Role is NULL in token!");
                }

            } catch (Exception e) {
                System.err.println("Error during JWT authentication: " + e.getMessage());
                // Не устанавливаем аутентификацию в случае ошибки
            }
        } else {
            System.out.println("No valid JWT token found");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Убираем "Bearer "
        }
        return null;
    }
}