package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.EmailJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.CpfJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.RoleNaoEncontradaException;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.RoleGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CriarUsuarioUseCase {
    
    private final UsuarioGateway usuarioGateway;
    private final RoleGateway roleGateway;
    private final PasswordEncoder passwordEncoder;
    
    public CriarUsuarioUseCase(UsuarioGateway usuarioGateway, RoleGateway roleGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.roleGateway = roleGateway;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario executar(CriarUsuarioRequest request) {
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
        
        return usuarioGateway.salvar(usuario);
    }
    
    private void validarDadosUnicos(String email, String cpf) {
        if (usuarioGateway.existePorEmail(email)) {
            throw new EmailJaExisteException(email);
        }
        
        if (usuarioGateway.existePorCpf(cpf)) {
            throw new CpfJaExisteException(cpf);
        }
    }
    
    private Role buscarRole(UUID roleId) {
        return roleGateway.buscarPorId(roleId)
                .orElseThrow(() -> new RoleNaoEncontradaException(roleId));
    }
}
