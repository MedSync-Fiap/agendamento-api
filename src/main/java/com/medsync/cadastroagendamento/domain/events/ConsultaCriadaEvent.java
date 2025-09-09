package com.medsync.cadastroagendamento.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConsultaCriadaEvent {
    private UUID consultaId;
    private UUID pacienteId;
    private UUID medicoId;
    private UUID criadoPorId;
    private LocalDateTime dataHora;
    private LocalDateTime timestamp;

    public ConsultaCriadaEvent() {}

    public ConsultaCriadaEvent(UUID consultaId, UUID pacienteId, UUID medicoId, UUID criadoPorId, 
                               LocalDateTime dataHora, LocalDateTime timestamp) {
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.criadoPorId = criadoPorId;
        this.dataHora = dataHora;
        this.timestamp = timestamp;
    }

    // Getters and Setters
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

    public UUID getCriadoPorId() {
        return criadoPorId;
    }

    public void setCriadoPorId(UUID criadoPorId) {
        this.criadoPorId = criadoPorId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
