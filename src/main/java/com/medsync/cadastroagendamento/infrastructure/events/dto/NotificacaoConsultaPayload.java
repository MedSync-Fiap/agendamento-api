package com.medsync.cadastroagendamento.infrastructure.events.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacaoConsultaPayload(
    UUID consultaId,
    UUID pacienteId,
    UUID medicoId,
    UUID criadoPorId,
    LocalDateTime dataHora,
    String status,
    String observacoes,
    String tipoEvento,
    LocalDateTime timestamp
) {}
