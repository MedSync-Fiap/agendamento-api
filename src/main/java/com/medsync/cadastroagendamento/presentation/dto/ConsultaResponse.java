package com.medsync.cadastroagendamento.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaResponse(
    UUID id,
    UUID pacienteId,
    UUID medicoId,
    LocalDateTime dataHora,
    String status,
    String observacoes,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
