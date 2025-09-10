package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.CredenciaisInvalidasException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.AutenticarUsuarioRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticarUsuarioUseCase {
    
    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;
    
    public AutenticarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario executar(AutenticarUsuarioRequest request) {
        Usuario usuario = buscarUsuarioPorEmail(request.email());
        validarUsuarioAtivo(usuario);
        validarSenha(usuario, request.senha());
        return usuario;
    }
    
    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioGateway.buscarPorEmail(email)
                .orElseThrow(() -> new CredenciaisInvalidasException());
    }
    
    private void validarUsuarioAtivo(Usuario usuario) {
        if (!usuario.isAtivo()) {
            throw new CredenciaisInvalidasException("Usuário inativo");
        }
    }
    
    private void validarSenha(Usuario usuario, String senha) {
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
    }
}
