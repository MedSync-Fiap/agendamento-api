package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarUsuariosInativosUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(ListarUsuariosInativosUseCase.class);
    
    private final UsuarioGateway usuarioGateway;
    
    public ListarUsuariosInativosUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public List<Usuario> executar() {
        try {
            return usuarioGateway.buscarUsuariosInativos();
        } catch (Exception e) {
            log.error("Erro ao listar usuários inativos: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar usuários inativos no banco de dados", e);
        }
    }
}
