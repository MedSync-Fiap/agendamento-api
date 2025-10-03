package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeletarUsuarioUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public DeletarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public void executar(UUID id) {
        if (!usuarioGateway.buscarPorId(id).isPresent()) {
            throw new UsuarioNaoEncontradoException(id);
        }
        
        usuarioGateway.deletar(id);
    }
}
