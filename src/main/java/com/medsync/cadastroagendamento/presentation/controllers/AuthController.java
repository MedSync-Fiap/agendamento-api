package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AutenticarUsuarioUseCase;
import com.medsync.cadastroagendamento.infrastructure.config.JwtConfig;
import com.medsync.cadastroagendamento.presentation.dto.LoginRequest;
import com.medsync.cadastroagendamento.presentation.dto.LoginResponse;
import com.medsync.cadastroagendamento.presentation.mappers.UsuarioDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final JwtConfig jwtConfig;
    private final UsuarioDtoMapper mapper;
    
    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase,
                         JwtConfig jwtConfig,
                         UsuarioDtoMapper mapper) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.jwtConfig = jwtConfig;
        this.mapper = mapper;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var useCaseRequest = new AutenticarUsuarioUseCase.AutenticarUsuarioRequest(
            request.email(),
            request.senha()
        );
        
        var usuario = autenticarUsuarioUseCase.executar(useCaseRequest);
        var token = jwtConfig.generateToken(usuario.getId(), usuario.getEmail(), "USER");
        
        var response = new LoginResponse(
            token,
            "Bearer",
            mapper.toResponse(usuario)
        );
        
        return ResponseEntity.ok(response);
    }
}
