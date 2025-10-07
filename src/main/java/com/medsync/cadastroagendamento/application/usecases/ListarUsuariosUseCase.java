package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.exception.UsuarioNotFoundException;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ListarUsuariosUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(ListarUsuariosUseCase.class);
    
    private final UsuarioGateway usuarioGateway;
    
    public ListarUsuariosUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public List<Usuario> executar() {
        try {
            return usuarioGateway.buscarTodos();
        } catch (Exception e) {
            log.error("Erro ao listar usuários: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar usuários no banco de dados", e);
        }
    }
    
    public Usuario buscarPorId(UUID id) {
        try {
            return usuarioGateway.buscarPorId(id)
                    .orElseThrow(() -> UsuarioNotFoundException.byId(id));
        } catch (UsuarioNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar usuário no banco de dados", e);
        }
    }
}
