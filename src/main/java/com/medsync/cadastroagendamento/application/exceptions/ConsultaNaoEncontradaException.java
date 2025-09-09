package com.medsync.cadastroagendamento.application.exceptions;

import java.util.UUID;

public class ConsultaNaoEncontradaException extends RuntimeException {
    
    public ConsultaNaoEncontradaException(UUID id) {
        super("Consulta não encontrada com ID: " + id);
    }
}
