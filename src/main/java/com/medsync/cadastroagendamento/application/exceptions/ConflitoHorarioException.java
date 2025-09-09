package com.medsync.cadastroagendamento.application.exceptions;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConflitoHorarioException extends RuntimeException {
    
    public ConflitoHorarioException(UUID medicoId, LocalDateTime dataHora) {
        super("Já existe uma consulta agendada para o médico " + medicoId + " no horário " + dataHora);
    }
}
