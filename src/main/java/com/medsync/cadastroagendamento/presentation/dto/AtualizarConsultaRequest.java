package com.medsync.cadastroagendamento.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record AtualizarConsultaRequest(
        UUID pacienteId,
        
        UUID medicoId,
        
        @NotNull(message = "ID da especialidade é obrigatório")
        UUID especialidadeId,

        @NotNull(message = "Data e hora são obrigatórias")
        @Future(message = "Data da consulta deve ser futura")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalDateTime dataHora,

        @Size(max = 500, message = "Observações não podem exceder 500 caracteres")
        String observacoes,

        @NotNull(message = "Status da consulta é obrigatório")
        String status
) {}