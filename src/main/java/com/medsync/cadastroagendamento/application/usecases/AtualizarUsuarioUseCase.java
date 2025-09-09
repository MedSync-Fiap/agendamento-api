package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.EmailJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
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
        Usuario usuario = usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        
        // Verificar se email já existe em outro usuário
        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            if (usuarioGateway.existePorEmail(request.email())) {
                throw new EmailJaExisteException(request.email());
            }
            usuario.setEmail(request.email());
        }
        
        // Atualizar campos
        if (request.nome() != null) {
            usuario.setNome(request.nome());
        }
        
        if (request.senha() != null) {
            usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        }
        
        if (request.roleId() != null) {
            usuario.setRoleId(request.roleId());
        }
        
        usuario.setAtualizadoEm(LocalDateTime.now());
        
        return usuarioGateway.salvar(usuario);
    }
    
    public record AtualizarUsuarioRequest(
        String nome,
        String email,
        String senha,
        UUID roleId
    ) {}
}
