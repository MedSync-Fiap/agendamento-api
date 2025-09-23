package com.medsync.cadastroagendamento.domain.events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaHistoricoEvent(
    UUID consultaId,
    LocalDateTime dataHora,
    String status,
    String observacoes,
    String tipoEvento, // "CRIADA" ou "EDITADA"
    LocalDateTime timestamp,
    
    // Dados detalhados do paciente
    UUID pacienteId,
    String pacienteNome,
    String pacienteCpf,
    String pacienteEmail,
    LocalDate pacienteDataNascimento,
    
    // Dados detalhados do médico
    UUID medicoId,
    String medicoNome,
    String medicoCpf,
    String medicoEmail,
    String medicoEspecialidade,
    
    // Dados do usuário que criou/editou
    UUID usuarioId,
    String usuarioNome,
    String usuarioEmail,
    String usuarioRole
) {}
