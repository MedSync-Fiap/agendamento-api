package com.medsync.cadastroagendamento.presentation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarConsultaRequest(
    @NotNull(message = "ID do paciente é obrigatório")
    UUID pacienteId,
    
    @NotNull(message = "ID do médico é obrigatório")
    UUID medicoId,
    
    @NotNull(message = "Data e hora são obrigatórias")
    @Future(message = "Data e hora devem ser no futuro")
    LocalDateTime dataHora,
    
    String observacoes
) {}
