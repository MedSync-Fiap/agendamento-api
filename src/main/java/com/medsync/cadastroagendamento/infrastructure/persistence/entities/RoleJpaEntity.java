package com.medsync.cadastroagendamento.infrastructure.persistence.entities;

import com.medsync.cadastroagendamento.domain.enums.TipoRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_role")
public class RoleJpaEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "nome", nullable = false, unique = true)
    private String nome;
    
    @Column(name = "descricao")
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoRole tipo;
    
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tb_role_permissao",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private List<PermissaoJpaEntity> permissoes;
    
    public RoleJpaEntity() {}
    
    public RoleJpaEntity(UUID id, String nome, String descricao, TipoRole tipo, 
                        LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public TipoRole getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoRole tipo) {
        this.tipo = tipo;
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
    
    public List<PermissaoJpaEntity> getPermissoes() {
        return permissoes;
    }
    
    public void setPermissoes(List<PermissaoJpaEntity> permissoes) {
        this.permissoes = permissoes;
    }
}
