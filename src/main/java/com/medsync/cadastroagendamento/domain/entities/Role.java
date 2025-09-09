package com.medsync.cadastroagendamento.domain.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Role {
    private UUID id;
    private String nome;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<Permissao> permissoes;

    public Role() {}

    public Role(UUID id, String nome, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.nome = nome;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    // Business methods
    public boolean temPermissao(String nomePermissao) {
        if (permissoes == null) {
            return false;
        }
        return permissoes.stream()
                .anyMatch(permissao -> permissao.getNome().equals(nomePermissao));
    }

    public void adicionarPermissao(Permissao permissao) {
        if (permissoes != null && !permissoes.contains(permissao)) {
            permissoes.add(permissao);
        }
    }

    public void removerPermissao(Permissao permissao) {
        if (permissoes != null) {
            permissoes.remove(permissao);
        }
    }
}
