package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.services.AuthService;
import com.medsync.cadastroagendamento.presentation.dto.AutenticarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.LoginRequest;
import com.medsync.cadastroagendamento.presentation.dto.LoginResponse;
import com.medsync.cadastroagendamento.presentation.mappers.UsuarioDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "API para autenticação de usuários")
public class AuthController {
    
    private final AuthService authService;
    private final UsuarioDtoMapper mapper;
    
    public AuthController(AuthService authService, UsuarioDtoMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }
    
    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var useCaseRequest = new AutenticarUsuarioRequest(
            request.email(),
            request.senha()
        );
        
        var usuario = authService.obterUsuarioAutenticado(useCaseRequest);
        var token = authService.autenticarUsuario(useCaseRequest);
        
        var response = new LoginResponse(
            token,
            "Bearer",
            mapper.toResponse(usuario)
        );
        
        return ResponseEntity.ok(response);
    }
}
