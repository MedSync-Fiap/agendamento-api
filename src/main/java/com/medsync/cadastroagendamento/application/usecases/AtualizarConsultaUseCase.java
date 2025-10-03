package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoGraphQLClient;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AtualizarConsultaUseCase {
    
    private final HistoricoGraphQLClient historicoGraphQLClient;
    
    public AtualizarConsultaUseCase(HistoricoGraphQLClient historicoGraphQLClient) {
        this.historicoGraphQLClient = historicoGraphQLClient;
    }
    
    public void executar(UUID consultaId, UUID pacienteId, UUID medicoId, 
                        AtualizarConsultaRequest request, UUID usuarioLogadoId) {

        historicoGraphQLClient.atualizarConsulta(
            consultaId,
            pacienteId,
            medicoId,
            request.especialidadeId(),
            usuarioLogadoId,
            request.dataHora(),
            request.observacoes(),
            request.status()
        );
    }
}
