package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.exception.InvalidCredentialsException;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.AutenticarUsuarioRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticarUsuarioUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(AutenticarUsuarioUseCase.class);
    
    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;
    
    public AutenticarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario executar(AutenticarUsuarioRequest request) {
        try {
            Usuario usuario = buscarUsuarioPorEmail(request.email());
            validarUsuarioAtivo(usuario);
            validarSenha(usuario, request.senha());
            return usuario;
        } catch (InvalidCredentialsException e) {
            throw e; 
        } catch (Exception e) {
            log.error("Erro ao autenticar usuário com email {}: {}", request.email(), e.getMessage(), e);
            throw new DatabaseException("Falha ao autenticar usuário no banco de dados", e);
        }
    }
    
    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioGateway.buscarPorEmail(email)
                .orElseThrow(() -> InvalidCredentialsException.invalidEmail());
    }
    
    private void validarUsuarioAtivo(Usuario usuario) {
        if (!usuario.isAtivo()) {
            throw new InvalidCredentialsException("Usuário inativo");
        }
    }
    
    private void validarSenha(Usuario usuario, String senha) {
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw InvalidCredentialsException.invalidPassword();
        }
    }
}
