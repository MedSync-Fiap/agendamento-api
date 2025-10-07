package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.exception.UsuarioNotFoundException;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReativarUsuarioUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(ReativarUsuarioUseCase.class);
    
    private final UsuarioGateway usuarioGateway;
    
    public ReativarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public void executar(UUID id) {
        try {
            // Verificar se o usuário existe (incluindo inativos)
            if (!usuarioGateway.buscarPorIdIncluindoInativos(id).isPresent()) {
                throw UsuarioNotFoundException.byId(id);
            }
            
            // Reativar usuário
            usuarioGateway.reativarUsuario(id);
            log.info("Usuário {} reativado com sucesso", id);
        } catch (UsuarioNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            log.error("Erro ao reativar usuário {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Falha ao reativar usuário no banco de dados", e);
        }
    }
}
