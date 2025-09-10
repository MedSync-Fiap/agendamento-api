package com.medsync.cadastroagendamento.presentation.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados para atualização de consulta")
public record AtualizarConsultaRequest(
        @Schema(description = "ID do médico", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID medicoId,
        
        @Schema(description = "Data e hora da consulta", example = "2024-12-25T14:30:00")
        LocalDateTime dataHora,
        
        @Schema(description = "Observações da consulta", example = "Consulta de rotina")
        String observacoes,
        
        @NotNull(message = "ID do usuário que está atualizando é obrigatório")
        @Schema(description = "ID do usuário que está atualizando", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID editadoPorId
) {}
