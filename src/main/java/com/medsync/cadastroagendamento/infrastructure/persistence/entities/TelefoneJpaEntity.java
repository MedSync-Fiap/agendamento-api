package com.medsync.cadastroagendamento.infrastructure.persistence.entities;

import com.medsync.cadastroagendamento.domain.enums.TipoTelefone;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_usuario_telefone")
public class TelefoneJpaEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    
    @Column(name = "numero", nullable = false)
    private String numero;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTelefone tipo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioJpaEntity usuario;
    
    public TelefoneJpaEntity() {}
    
    public TelefoneJpaEntity(UUID id, String numero, TipoTelefone tipo) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public TipoTelefone getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoTelefone tipo) {
        this.tipo = tipo;
    }
    
    public UsuarioJpaEntity getUsuario() {
        return usuario;
    }
    
    public void setUsuario(UsuarioJpaEntity usuario) {
        this.usuario = usuario;
    }
}
