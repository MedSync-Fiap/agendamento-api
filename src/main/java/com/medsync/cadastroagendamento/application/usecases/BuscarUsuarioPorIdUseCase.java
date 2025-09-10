package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuscarUsuarioPorIdUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public BuscarUsuarioPorIdUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public Usuario executar(UUID id) {
        return usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }
}
