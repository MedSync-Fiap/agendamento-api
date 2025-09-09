package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BuscarUsuarioUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public BuscarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public Usuario buscarPorId(UUID id) {
        return usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }
    
    public Usuario buscarPorEmail(String email) {
        return usuarioGateway.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("email", email));
    }
    
    public Usuario buscarPorCpf(String cpf) {
        return usuarioGateway.buscarPorCpf(cpf)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("CPF", cpf));
    }
    
    public List<Usuario> buscarPorRole(UUID roleId) {
        return usuarioGateway.buscarPorRole(roleId);
    }
    
    public List<Usuario> buscarMedicos() {
        return usuarioGateway.buscarMedicos();
    }
    
    public List<Usuario> buscarPacientes() {
        return usuarioGateway.buscarPacientes();
    }
    
    public List<Usuario> buscarTodos() {
        return usuarioGateway.buscarTodos();
    }
}
