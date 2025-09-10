package com.medsync.cadastroagendamento.infrastructure.persistence.entities;

import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_consulta")
public class ConsultaJpaEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;
    
    @Column(name = "medico_id", nullable = false)
    private UUID medicoId;
    
    @Column(name = "criado_por_id", nullable = false)
    private UUID criadoPorId;
    
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusConsulta status;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
    
    public ConsultaJpaEntity() {}
    
    public ConsultaJpaEntity(UUID id, UUID pacienteId, UUID medicoId, UUID criadoPorId, 
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
}
