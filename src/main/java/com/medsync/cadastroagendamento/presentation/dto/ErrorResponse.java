package com.medsync.cadastroagendamento.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String code,
    String message,
    String timestamp,
    String path,
    Integer status,
    String error,
    List<ValidationError> validationErrors,
    String stackTrace,
    String details
) {
    
    public record ValidationError(
        String field,
        String message,
        Object rejectedValue
    ) {}
}
