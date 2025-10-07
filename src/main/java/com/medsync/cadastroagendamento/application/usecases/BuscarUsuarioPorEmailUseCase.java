package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.exception.UsuarioNotFoundException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

@Component
public class BuscarUsuarioPorEmailUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public BuscarUsuarioPorEmailUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public Usuario executar(String email) {
        return usuarioGateway.buscarPorEmail(email)
                .orElseThrow(() -> UsuarioNotFoundException.byEmail(email));
    }
}
