package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.EmailJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarUsuarioRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AtualizarUsuarioUseCase {
    
    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;
    
    public AtualizarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario executar(UUID id, AtualizarUsuarioRequest request) {
        Usuario usuario = buscarUsuario(id);
        atualizarCamposUsuario(usuario, request);
        usuario.setAtualizadoEm(LocalDateTime.now());
        return usuarioGateway.salvar(usuario);
    }
    
    private Usuario buscarUsuario(UUID id) {
        return usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }
    
    private void atualizarCamposUsuario(Usuario usuario, AtualizarUsuarioRequest request) {
        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            validarEmailNaoExiste(request.email());
            usuario.setEmail(request.email());
        }
        
        if (request.nome() != null) {
            usuario.setNome(request.nome());
        }
        
        if (request.senha() != null) {
            usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        }
        
        if (request.roleId() != null) {
            usuario.setRoleId(request.roleId());
        }
    }
    
    private void validarEmailNaoExiste(String email) {
        if (usuarioGateway.existePorEmail(email)) {
            throw new EmailJaExisteException(email);
        }
    }
}
