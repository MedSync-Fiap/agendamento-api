package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.exception.UsuarioAlreadyExistsException;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.RoleGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CriarUsuarioUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(CriarUsuarioUseCase.class);
    
    private final UsuarioGateway usuarioGateway;
    private final RoleGateway roleGateway;
    private final PasswordEncoder passwordEncoder;
    
    public CriarUsuarioUseCase(UsuarioGateway usuarioGateway, RoleGateway roleGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.roleGateway = roleGateway;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario executar(CriarUsuarioRequest request) {
        try {
            validarDadosUnicos(request.email(), request.cpf());
            
            Role role = buscarRole(request.roleId());
            
            Usuario usuario = new Usuario();
            usuario.setId(UUID.randomUUID());
            usuario.setNome(request.nome());
            usuario.setEmail(request.email());
            usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
            usuario.setCpf(request.cpf());
            usuario.setDataNascimento(request.dataNascimento());
            usuario.setRole(role);
            usuario.setCriadoEm(LocalDateTime.now());
            usuario.setAtualizadoEm(LocalDateTime.now());
            usuario.setAtivo(true);
            
            return usuarioGateway.salvar(usuario);
        } catch (UsuarioAlreadyExistsException e) {
            throw e; 
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao criar usuário no banco de dados", e);
        }
    }
    
    private void validarDadosUnicos(String email, String cpf) {
        if (usuarioGateway.existePorEmail(email)) {
            throw UsuarioAlreadyExistsException.byEmail(email);
        }
        
        if (usuarioGateway.existePorCpf(cpf)) {
            throw UsuarioAlreadyExistsException.byCpf(cpf);
        }
    }
    
    private Role buscarRole(UUID roleId) {
        try {
            return roleGateway.buscarPorId(roleId)
                    .orElseThrow(() -> new DatabaseException("Role não encontrada com ID: " + roleId, roleId));
        } catch (Exception e) {
            log.error("Erro ao buscar role com ID: {}", roleId, e);
            throw new DatabaseException("Falha ao buscar role no banco de dados", e);
        }
    }
}
