package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.CpfJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.EmailJaExisteException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CriarUsuarioUseCase {
    
    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;
    
    public CriarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario executar(CriarUsuarioRequest request) {
        validarEmailNaoExiste(request.email());
        validarCpfNaoExiste(request.cpf());
        
        Usuario usuario = criarUsuario(request);
        return usuarioGateway.salvar(usuario);
    }
    
    private void validarEmailNaoExiste(String email) {
        if (usuarioGateway.existePorEmail(email)) {
            throw new EmailJaExisteException(email);
        }
    }
    
    private void validarCpfNaoExiste(String cpf) {
        if (usuarioGateway.existePorCpf(cpf)) {
            throw new CpfJaExisteException(cpf);
        }
    }
    
    private Usuario criarUsuario(CriarUsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome(request.nome());
        usuario.setCpf(request.cpf());
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setRoleId(request.roleId());
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
        return usuario;
    }
}
