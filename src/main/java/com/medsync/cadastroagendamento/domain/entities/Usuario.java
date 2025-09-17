package com.medsync.cadastroagendamento.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Usuario {
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String senhaHash;
    private LocalDate dataNascimento;
    private UUID roleId;
    private Role role;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<Telefone> telefones;

    public Usuario() {}

    public Usuario(UUID id, String nome, String cpf, String email, String senhaHash, 
                   LocalDate dataNascimento, UUID roleId, boolean ativo, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senhaHash = senhaHash;
        this.dataNascimento = dataNascimento;
        this.roleId = roleId;
        this.ativo = ativo;
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        return role != null && "MEDICO".equals(role.getNome());
    }

    public boolean isEnfermeiro() {
        return role != null && "ENFERMEIRO".equals(role.getNome());
    }

    public boolean isPaciente() {
        return role != null && "PACIENTE".equals(role.getNome());
    }

    public boolean isAdmin() {
        return role != null && "ADMIN".equals(role.getNome());
    }

    public String getRoleNome() {
        return role != null ? role.getNome() : "UNKNOWN";
    }

    public List<String> getPermissoes() {
        return role != null && role.getPermissoes() != null 
            ? role.getPermissoes().stream().map(Permissao::getNome).toList()
            : List.of();
    }

    public boolean hasPermission(String permissionName) {
        return role != null && role.hasPermission(permissionName);
    }
}
