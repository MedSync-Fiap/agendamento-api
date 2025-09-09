package com.medsync.cadastroagendamento.application.exceptions;

import java.util.UUID;

public class ConsultaNaoPodeSerEditadaException extends RuntimeException {
    
    public ConsultaNaoPodeSerEditadaException(UUID id) {
        super("Consulta não pode ser editada com ID: " + id);
    }
}
