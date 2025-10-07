package com.medsync.cadastroagendamento.domain.exception;

import java.util.UUID;

public class EspecialidadeNotFoundException extends BusinessRuleException {
    
    public static final String ERROR_CODE = "ESPECIALIDADE_NOT_FOUND";
    
    public EspecialidadeNotFoundException(String message) {
        super(message, ERROR_CODE);
    }
    
    public EspecialidadeNotFoundException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public static EspecialidadeNotFoundException byId(UUID id) {
        return new EspecialidadeNotFoundException("Especialidade não encontrada com ID: " + id, id);
    }
    
    public static EspecialidadeNotFoundException byNome(String nome) {
        return new EspecialidadeNotFoundException("Especialidade não encontrada com nome: " + nome, nome);
    }
}
