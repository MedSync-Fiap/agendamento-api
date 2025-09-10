package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.application.usecases.AutenticarUsuarioUseCase;
import com.medsync.cadastroagendamento.presentation.dto.AutenticarUsuarioRequest;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.config.JwtConfig;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final JwtConfig jwtConfig;
    
    public AuthService(AutenticarUsuarioUseCase autenticarUsuarioUseCase, JwtConfig jwtConfig) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.jwtConfig = jwtConfig;
    }
    
    public String autenticarUsuario(AutenticarUsuarioRequest request) {
        Usuario usuario = autenticarUsuarioUseCase.executar(request);
        return jwtConfig.generateToken(usuario.getId(), usuario.getEmail(), "USER");
    }
    
    public Usuario obterUsuarioAutenticado(AutenticarUsuarioRequest request) {
        return autenticarUsuarioUseCase.executar(request);
    }
}
