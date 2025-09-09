package com.medsync.cadastroagendamento.domain.entities;

import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import java.time.LocalDateTime;
import java.util.UUID;

public class Consulta {
    private UUID id;
    private UUID pacienteId;
    private UUID medicoId;
    private UUID criadoPorId;
    private LocalDateTime dataHora;
    private StatusConsulta status;
    private String observacoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Consulta() {}

    public Consulta(UUID id, UUID pacienteId, UUID medicoId, UUID criadoPorId, 
                    LocalDateTime dataHora, StatusConsulta status, String observacoes,
                    LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.criadoPorId = criadoPorId;
        this.dataHora = dataHora;
        this.status = status;
        this.observacoes = observacoes;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public StatusConsulta getStatus() {
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    // Business methods
    public void agendar() {
        this.status = StatusConsulta.AGENDADA;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void confirmar() {
        this.status = StatusConsulta.CONFIRMADA;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = StatusConsulta.CANCELADA;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void marcarComoRealizada() {
        this.status = StatusConsulta.REALIZADA;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void marcarFalta() {
        this.status = StatusConsulta.FALTA;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void atualizarDataHora(LocalDateTime novaDataHora) {
        this.dataHora = novaDataHora;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void atualizarObservacoes(String observacoes) {
        this.observacoes = observacoes;
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean podeSerEditada() {
        return status == StatusConsulta.AGENDADA || status == StatusConsulta.CONFIRMADA;
    }

    public boolean podeSerCancelada() {
        return status == StatusConsulta.AGENDADA || status == StatusConsulta.CONFIRMADA;
    }
}
