package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.CredenciaisInvalidasException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
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
        Usuario usuario = usuarioGateway.buscarPorEmail(request.email())
                .orElseThrow(() -> new CredenciaisInvalidasException());
        
        if (!usuario.isAtivo()) {
            throw new CredenciaisInvalidasException("Usuário inativo");
        }
        
        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        
        return usuario;
    }
    
    public record AutenticarUsuarioRequest(
        String email,
        String senha
    ) {}
}
