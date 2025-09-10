package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ValidarPacienteUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public ValidarPacienteUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public void executar(UUID pacienteId) {
        Usuario paciente = usuarioGateway.buscarPorId(pacienteId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(pacienteId));
    }
}
