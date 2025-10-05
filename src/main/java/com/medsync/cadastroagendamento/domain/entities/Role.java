package com.medsync.cadastroagendamento.domain.entities;

import com.medsync.cadastroagendamento.domain.enums.TipoRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Role {
    private UUID id;
    private TipoRole tipo;
    private String descricao;
    private List<Permissao> permissoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Role() {}

    public Role(UUID id, TipoRole tipo, String descricao, List<Permissao> permissoes, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.tipo = tipo;
        this.descricao = descricao;
        this.permissoes = permissoes;
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

    public TipoRole getTipo() {
        return tipo;
    }

    public void setTipo(TipoRole tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
    
    public List<Permissao> getPermissoes() {
        return permissoes;
    }
    
    public void setPermissoes(List<Permissao> permissoes) {
        this.permissoes = permissoes;
    }
}