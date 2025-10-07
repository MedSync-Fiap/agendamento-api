package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.exception.UsuarioAlreadyExistsException;
import com.medsync.cadastroagendamento.domain.exception.UsuarioNotFoundException;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.RoleGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarUsuarioRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AtualizarUsuarioUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(AtualizarUsuarioUseCase.class);
    
    private final UsuarioGateway usuarioGateway;
    private final RoleGateway roleGateway;
    private final PasswordEncoder passwordEncoder;
    
    public AtualizarUsuarioUseCase(UsuarioGateway usuarioGateway, RoleGateway roleGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.roleGateway = roleGateway;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario executar(UUID id, AtualizarUsuarioRequest request) {
        try {
            Usuario usuario = buscarUsuario(id);
            atualizarCamposUsuario(usuario, request);
            usuario.setAtualizadoEm(LocalDateTime.now());
            return usuarioGateway.salvar(usuario);
        } catch (UsuarioNotFoundException | UsuarioAlreadyExistsException e) {
            throw e; 
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Falha ao atualizar usuário no banco de dados", e);
        }
    }
    
    private Usuario buscarUsuario(UUID id) {
        return usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> UsuarioNotFoundException.byId(id));
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
            try {
                Role novaRole = roleGateway.buscarPorId(request.roleId())
                        .orElseThrow(() -> new DatabaseException("Role não encontrada com ID: " + request.roleId(), request.roleId()));
                usuario.setRole(novaRole);
            } catch (Exception e) {
                log.error("Erro ao buscar role com ID {}: {}", request.roleId(), e.getMessage(), e);
                throw new DatabaseException("Falha ao buscar role no banco de dados", e);
            }
        }
    }
    
    private void validarEmailNaoExiste(String email) {
        if (usuarioGateway.existePorEmail(email)) {
            throw UsuarioAlreadyExistsException.byEmail(email);
        }
    }
}
