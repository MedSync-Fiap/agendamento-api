package com.medsync.cadastroagendamento.infrastructure.security;

import com.medsync.cadastroagendamento.infrastructure.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtConfig jwtConfig;
    
    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                String email = jwtConfig.getUsernameFromToken(token);
                UUID userId = jwtConfig.getUserIdFromToken(token);
                String role = jwtConfig.getRoleFromToken(token);
                List<String> permissions = jwtConfig.getPermissionsFromToken(token);
                
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Criar authorities baseadas no role e nas permissões
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    
                    // Adicionar permissões se existirem
                    if (permissions != null) {
                        authorities.addAll(permissions.stream()
                            .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission))
                            .collect(Collectors.toList()));
                    }
                    
                    // Adicionar role como authority
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            email, 
                            null, 
                            authorities
                        );
                    
                    authToken.setDetails(userId);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                logger.error("Erro ao processar token JWT", e);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
