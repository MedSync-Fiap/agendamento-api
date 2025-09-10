package com.medsync.cadastroagendamento.domain.events;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ConsultaEditadaEvent {
    private UUID consultaId;
    private UUID pacienteId;
    private UUID medicoId;
    private UUID editadoPorId;
    private Map<String, Object> alteracoes;
    private LocalDateTime timestamp;

    public ConsultaEditadaEvent() {}

    public ConsultaEditadaEvent(UUID consultaId, UUID pacienteId, UUID medicoId, UUID editadoPorId, 
                                 Map<String, Object> alteracoes, LocalDateTime timestamp) {
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.editadoPorId = editadoPorId;
        this.alteracoes = alteracoes;
        this.timestamp = timestamp;
    }

    public UUID getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(UUID consultaId) {
        this.consultaId = consultaId;
    }

    public UUID getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(UUID pacienteId) {
        this.pacienteId = pacienteId;
    }

    public UUID getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(UUID medicoId) {
        this.medicoId = medicoId;
    }

    public UUID getEditadoPorId() {
        return editadoPorId;
    }

    public void setEditadoPorId(UUID editadoPorId) {
        this.editadoPorId = editadoPorId;
    }

    public Map<String, Object> getAlteracoes() {
        return alteracoes;
    }

    public void setAlteracoes(Map<String, Object> alteracoes) {
        this.alteracoes = alteracoes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
