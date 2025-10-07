package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoGraphQLClient;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CriarConsultaUseCase {
    
    private final HistoricoGraphQLClient historicoGraphQLClient;
    private final PublicarNotificacaoUseCase publicarNotificacaoUseCase;

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CriarConsultaUseCase.class);
    
    public CriarConsultaUseCase(HistoricoGraphQLClient historicoGraphQLClient,
                               PublicarNotificacaoUseCase publicarNotificacaoUseCase) {
        this.historicoGraphQLClient = historicoGraphQLClient;
        this.publicarNotificacaoUseCase = publicarNotificacaoUseCase;
    }
    
    private UUID consultaIdCriada;
    
    public UUID executar(CriarConsultaRequest request, UUID usuarioLogadoId) {

        consultaIdCriada = UUID.randomUUID();

        historicoGraphQLClient.salvarConsulta(
                consultaIdCriada,
                request.pacienteId(),
                request.medicoId(),
                request.especialidadeId(),
                usuarioLogadoId,
                request.dataHora(),
                request.observacoes(),
                "AGENDADA"
        );
        
        Consulta consulta = new Consulta();
        consulta.setId(consultaIdCriada);
        consulta.setPacienteId(request.pacienteId());
        consulta.setMedicoId(request.medicoId());
        consulta.setEspecialidadeId(request.especialidadeId());
        consulta.setCriadoPorId(usuarioLogadoId);
        consulta.setDataHora(request.dataHora());
        consulta.setObservacoes(request.observacoes());
        consulta.setStatus(StatusConsulta.AGENDADA);
        
        try {
            publicarNotificacaoUseCase.publicarConsultaCriada(consulta);
        } catch (Exception e) {
            logger.error("Erro ao publicar notificação de consulta criada: {}", consultaIdCriada, e);
        }
        
        return consultaIdCriada;
    }
    
    public UUID getConsultaId() {
        return consultaIdCriada;
    }
}
