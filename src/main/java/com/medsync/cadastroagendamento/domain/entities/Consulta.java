package com.medsync.cadastroagendamento.domain.entities;

import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;

import java.time.LocalDateTime;
import java.util.UUID;

public class Consulta {
    
    private UUID id;
    private UUID pacienteId;
    private UUID medicoId;
    private UUID especialidadeId;
    private UUID criadoPorId;
    private LocalDateTime dataHora;
    private String observacoes;
    private StatusConsulta status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    // Construtores
    public Consulta() {}
    
    public Consulta(UUID id, UUID pacienteId, UUID medicoId, UUID especialidadeId, UUID criadoPorId,
                    LocalDateTime dataHora, String observacoes, StatusConsulta status,
                    LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.especialidadeId = especialidadeId;
        this.criadoPorId = criadoPorId;
        this.dataHora = dataHora;
        this.observacoes = observacoes;
        this.status = status;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }
    
    // Getters e Setters
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
    
    public UUID getEspecialidadeId() {
        return especialidadeId;
    }
    
    public void setEspecialidadeId(UUID especialidadeId) {
        this.especialidadeId = especialidadeId;
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
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public StatusConsulta getStatus() {
        return status;
    }
    
    public void setStatus(StatusConsulta status) {
        this.status = status;
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
    
    // Métodos de negócio
    public boolean podeCancelar() {
        return status != null && status.podeCancelar();
    }
    
    public boolean podeReagendar() {
        return status != null && status.podeReagendar();
    }
    
    public boolean isFinal() {
        return status != null && status.isFinal();
    }
    
    public boolean isHoje() {
        if (dataHora == null) return false;
        return dataHora.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    public boolean isFutura() {
        if (dataHora == null) return false;
        return dataHora.isAfter(LocalDateTime.now());
    }
    
    public boolean isPassada() {
        if (dataHora == null) return false;
        return dataHora.isBefore(LocalDateTime.now());
    }
    
    @Override
    public String toString() {
        return "Consulta{" +
                "id=" + id +
                ", pacienteId=" + pacienteId +
                ", medicoId=" + medicoId +
                ", especialidadeId=" + especialidadeId +
                ", criadoPorId=" + criadoPorId +
                ", dataHora=" + dataHora +
                ", observacoes='" + observacoes + '\'' +
                ", status=" + status +
                ", criadoEm=" + criadoEm +
                ", atualizadoEm=" + atualizadoEm +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Consulta consulta = (Consulta) o;
        return id != null ? id.equals(consulta.id) : consulta.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
