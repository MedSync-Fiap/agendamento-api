package com.medsync.cadastroagendamento.application.exceptions;

public class CpfJaExisteException extends RuntimeException {
    public CpfJaExisteException(String cpf) {
        super("CPF já existe: " + cpf);
    }
}