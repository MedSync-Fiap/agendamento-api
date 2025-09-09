package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.CpfJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.EmailJaExisteException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
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
        // Validar se email já existe
        if (usuarioGateway.existePorEmail(request.email())) {
            throw new EmailJaExisteException(request.email());
        }
        
        // Validar se CPF já existe
        if (usuarioGateway.existePorCpf(request.cpf())) {
            throw new CpfJaExisteException(request.cpf());
        }
        
        // Criar usuário
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
        
        return usuarioGateway.salvar(usuario);
    }
    
    public record CriarUsuarioRequest(
        String nome,
        String cpf,
        String email,
        String senha,
        UUID roleId
    ) {}
}
