package com.medsync.cadastroagendamento.domain.exception;

import java.util.UUID;

public class PacienteAlreadyExistsException extends BusinessRuleException {

    public static final String ERROR_CODE = "PACIENTE_ALREADY_EXISTS";

    public PacienteAlreadyExistsException(String message) {
        super(message, ERROR_CODE);
    }

    public PacienteAlreadyExistsException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public static PacienteAlreadyExistsException byId(UUID id) {
        return new PacienteAlreadyExistsException("Paciente já existe encontrado com ID: " + id, id);
    }
    
    public static PacienteAlreadyExistsException byCpf(String cpf) {
        return new PacienteAlreadyExistsException("Paciente já existe com CPF: " + cpf, cpf);
    }
}
