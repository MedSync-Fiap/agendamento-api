package com.medsync.cadastroagendamento.application.exceptions;

import java.util.UUID;

public class ConsultaNaoPodeSerCanceladaException extends RuntimeException {
    
    public ConsultaNaoPodeSerCanceladaException(UUID consultaId) {
        super(String.format("A consulta com ID %s não pode ser cancelada no status atual", consultaId));
    }
}
