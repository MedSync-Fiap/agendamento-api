package com.medsync.cadastroagendamento.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaNotificacaoEvent(
    UUID consultaId,
    UUID pacienteId,
    UUID medicoId,
    UUID criadoPorId,
    LocalDateTime dataHora,
    String status,
    String observacoes,
    String tipoEvento, // "CRIADA" ou "EDITADA"
    LocalDateTime timestamp
) {}
