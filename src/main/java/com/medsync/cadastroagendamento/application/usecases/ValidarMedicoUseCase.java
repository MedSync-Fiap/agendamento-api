package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ValidarMedicoUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public ValidarMedicoUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public void executar(UUID medicoId) {
        Usuario medico = usuarioGateway.buscarPorId(medicoId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(medicoId));
    }
}
