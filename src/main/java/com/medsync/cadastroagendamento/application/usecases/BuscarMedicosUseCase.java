package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuscarMedicosUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public BuscarMedicosUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public List<Usuario> executar() {
        return usuarioGateway.buscarMedicos();
    }
}
