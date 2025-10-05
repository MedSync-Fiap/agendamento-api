package com.medsync.cadastroagendamento.infrastructure.security;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.config.JwtConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    
    private final JwtConfig jwtConfig;
    
    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }
    
    public String generateToken(Usuario usuario) {
        List<String> permissions = usuario.getRole().getPermissoes().stream()
                .map(permissao -> permissao.getNome())
                .toList();
        
        return jwtConfig.generateToken(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getRole().getTipo().toString(),
            permissions
        );
    }
}
