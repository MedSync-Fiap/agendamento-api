package com.medsync.cadastroagendamento.domain.exception;

import java.util.UUID;

public class PacienteNotFoundException extends BusinessRuleException {
    
    public static final String ERROR_CODE = "PACIENTE_NOT_FOUND";
    
    public PacienteNotFoundException(String message) {
        super(message, ERROR_CODE);
    }
    
    public PacienteNotFoundException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public static PacienteNotFoundException byId(UUID id) {
        return new PacienteNotFoundException("Paciente não encontrado com ID: " + id, id);
    }
    
    public static PacienteNotFoundException byCpf(String cpf) {
        return new PacienteNotFoundException("Paciente não encontrado com CPF: " + cpf, cpf);
    }
}
