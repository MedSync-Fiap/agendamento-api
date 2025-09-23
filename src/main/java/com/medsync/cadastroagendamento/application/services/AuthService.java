package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.application.usecases.AutenticarUsuarioUseCase;
import com.medsync.cadastroagendamento.presentation.dto.AutenticarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.AuthResponse;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.config.JwtConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final JwtConfig jwtConfig;
    
    public AuthService(AutenticarUsuarioUseCase autenticarUsuarioUseCase, JwtConfig jwtConfig) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.jwtConfig = jwtConfig;
    }
    
    public AuthResponse autenticarUsuario(AutenticarUsuarioRequest request) {
        Usuario usuario = autenticarUsuarioUseCase.executar(request);
        
        String token = jwtConfig.generateToken(
            usuario.getId(), 
            usuario.getEmail(), 
            usuario.getRoleNome(),
            usuario.getPermissoes()
        );
        
        Long expiresIn = jwtConfig.getExpirationTime();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expiresIn / 1000);
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            usuario.getId().toString(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRoleNome(),
            usuario.getPermissoes()
        );
        
        return new AuthResponse(
            token,
            "Bearer",
            expiresIn,
            expiresAt,
            userInfo
        );
    }
}
