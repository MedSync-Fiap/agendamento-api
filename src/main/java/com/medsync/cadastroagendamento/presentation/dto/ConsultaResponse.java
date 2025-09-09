package com.medsync.cadastroagendamento.presentation.dto;

import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaResponse(
    UUID id,
    UUID pacienteId,
    UUID medicoId,
    UUID criadoPorId,
    LocalDateTime dataHora,
    StatusConsulta status,
    String observacoes,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
