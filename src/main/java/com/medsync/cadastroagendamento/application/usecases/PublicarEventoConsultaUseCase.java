package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.events.ConsultaCriadaEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaEditadaEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class PublicarEventoConsultaUseCase {
    
    private final PublicarEventoHistoricoUseCase publicarEventoHistoricoUseCase;
    private final PublicarEventoNotificacaoUseCase publicarEventoNotificacaoUseCase;
    
    public PublicarEventoConsultaUseCase(PublicarEventoHistoricoUseCase publicarEventoHistoricoUseCase,
                                        PublicarEventoNotificacaoUseCase publicarEventoNotificacaoUseCase) {
        this.publicarEventoHistoricoUseCase = publicarEventoHistoricoUseCase;
        this.publicarEventoNotificacaoUseCase = publicarEventoNotificacaoUseCase;
    }
    
    public void publicarConsultaCriada(Consulta consulta) {
        ConsultaCriadaEvent evento = criarEventoConsultaCriada(consulta);
        publicarEventoHistoricoUseCase.executar(evento);
        publicarEventoNotificacaoUseCase.executar(evento);
    }
    
    public void publicarConsultaEditada(Consulta consulta, UUID editadoPorId, Map<String, Object> alteracoes) {
        ConsultaEditadaEvent evento = criarEventoConsultaEditada(consulta, editadoPorId, alteracoes);
        publicarEventoHistoricoUseCase.executar(evento);
        publicarEventoNotificacaoUseCase.executar(evento);
    }
    
    private ConsultaCriadaEvent criarEventoConsultaCriada(Consulta consulta) {
        return new ConsultaCriadaEvent(
            consulta.getId(),
            consulta.getPacienteId(),
            consulta.getMedicoId(),
            consulta.getCriadoPorId(),
            consulta.getDataHora(),
            LocalDateTime.now()
        );
    }
    
    private ConsultaEditadaEvent criarEventoConsultaEditada(Consulta consulta, UUID editadoPorId, Map<String, Object> alteracoes) {
        return new ConsultaEditadaEvent(
            consulta.getId(),
            consulta.getPacienteId(),
            consulta.getMedicoId(),
            editadoPorId,
            alteracoes,
            LocalDateTime.now()
        );
    }
}
