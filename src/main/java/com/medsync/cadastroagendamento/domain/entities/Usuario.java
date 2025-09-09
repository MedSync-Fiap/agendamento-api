package com.medsync.cadastroagendamento.domain.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Usuario {
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String senhaHash;
    private UUID roleId;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<Telefone> telefones;

    public Usuario() {}

    public Usuario(UUID id, String nome, String cpf, String email, String senhaHash, 
                   UUID roleId, boolean ativo, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senhaHash = senhaHash;
        this.roleId = roleId;
        this.ativo = ativo;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    // Business methods
    public void ativar() {
        this.ativo = true;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void desativar() {
        this.ativo = false;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void atualizarDados(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean isMedico() {
        return "MEDICO".equals(getRoleNome());
    }

    public boolean isEnfermeiro() {
        return "ENFERMEIRO".equals(getRoleNome());
    }

    public boolean isPaciente() {
        return "PACIENTE".equals(getRoleNome());
    }

    public boolean isAdmin() {
        return "ADMIN".equals(getRoleNome());
    }

    private String getRoleNome() {
        // This would typically be resolved through a service or repository
        // For now, we'll assume it's stored as a string in the roleId field
        // In a real implementation, you'd have a Role entity
        return "UNKNOWN"; // This should be resolved properly
    }
}
