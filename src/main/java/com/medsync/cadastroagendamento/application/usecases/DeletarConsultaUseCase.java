package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoGraphQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeletarConsultaUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(DeletarConsultaUseCase.class);
    
    private final HistoricoGraphQLClient historicoGraphQLClient;
    
    public DeletarConsultaUseCase(HistoricoGraphQLClient historicoGraphQLClient) {
        this.historicoGraphQLClient = historicoGraphQLClient;
    }
    
    public void executar(UUID consultaId, UUID pacienteId, UUID usuarioLogadoId) {
        try {
            historicoGraphQLClient.atualizarStatusConsulta(consultaId, "INATIVA", "Consulta removida do sistema", usuarioLogadoId);
            log.info("Consulta {} marcada como inativa (soft delete)", consultaId);
        } catch (Exception e) {
            log.error("Erro ao deletar consulta {}: {}", consultaId, e.getMessage(), e);
            throw new DatabaseException("Falha ao deletar consulta no banco de dados", e);
        }
    }
}
