package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoGraphQLClient;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CriarConsultaUseCase {
    
    private final HistoricoGraphQLClient historicoGraphQLClient;
    
    public CriarConsultaUseCase(HistoricoGraphQLClient historicoGraphQLClient) {
        this.historicoGraphQLClient = historicoGraphQLClient;
    }
    
    public void executar(CriarConsultaRequest request, UUID usuarioLogadoId) {
        // Gerar ID único para a consulta
        UUID consultaId = UUID.randomUUID();

        // Enviar consulta para o serviço de histórico via GraphQL
        historicoGraphQLClient.salvarConsulta(
                consultaId,
                request.pacienteId(),
                request.medicoId(),
                request.especialidadeId(),
                usuarioLogadoId,
                request.dataHora(),
                request.observacoes(),
                "AGENDADA"
        );
    }
}
