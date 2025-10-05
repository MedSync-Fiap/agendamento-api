package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.TipoRole;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BuscarUsuariosPorRoleUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public BuscarUsuariosPorRoleUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public List<Usuario> executar(String role) {
        TipoRole tipoRole = TipoRole.valueOf(role.toUpperCase());
        return usuarioGateway.buscarUsuariosPorRole(tipoRole);
    }
}
