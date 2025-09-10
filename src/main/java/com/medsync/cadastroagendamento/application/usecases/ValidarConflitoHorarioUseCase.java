package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.ConflitoHorarioException;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ValidarConflitoHorarioUseCase {
    
    private final ConsultaGateway consultaGateway;
    
    public ValidarConflitoHorarioUseCase(ConsultaGateway consultaGateway) {
        this.consultaGateway = consultaGateway;
    }
    
    public void executar(UUID medicoId, LocalDateTime dataHora) {
        if (consultaGateway.existeConsultaNoHorario(medicoId, dataHora)) {
            throw new ConflitoHorarioException(medicoId, dataHora);
        }
    }
    
    public void executarExcluindo(UUID medicoId, LocalDateTime dataHora, UUID consultaId) {
        if (consultaGateway.existeConsultaNoHorarioExcluindo(medicoId, dataHora, consultaId)) {
            throw new ConflitoHorarioException(medicoId, dataHora);
        }
    }
}
