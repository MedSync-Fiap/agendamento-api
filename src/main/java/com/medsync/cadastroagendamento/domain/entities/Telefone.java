package com.medsync.cadastroagendamento.domain.entities;

import com.medsync.cadastroagendamento.domain.enums.TipoTelefone;

import java.time.LocalDateTime;
import java.util.UUID;

public class Telefone {
    
    private UUID id;
    private String numero;
    private TipoTelefone tipo;
    private Boolean isPrincipal;
    private Usuario usuario;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    public Telefone() {}
    
    public Telefone(UUID id, String numero, TipoTelefone tipo, Boolean isPrincipal, Usuario usuario, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.isPrincipal = isPrincipal;
        this.usuario = usuario;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }
    
    // Getters
    public UUID getId() { return id; }
    public String getNumero() { return numero; }
    public TipoTelefone getTipo() { return tipo; }
    public Boolean getIsPrincipal() { return isPrincipal; }
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    
    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setTipo(TipoTelefone tipo) { this.tipo = tipo; }
    public void setIsPrincipal(Boolean isPrincipal) { this.isPrincipal = isPrincipal; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    
    // Builder pattern
    public static TelefoneBuilder builder() {
        return new TelefoneBuilder();
    }
    
    public static class TelefoneBuilder {
        private UUID id;
        private String numero;
        private TipoTelefone tipo;
        private Boolean isPrincipal;
        private Usuario usuario;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
        
        public TelefoneBuilder id(UUID id) { this.id = id; return this; }
        public TelefoneBuilder numero(String numero) { this.numero = numero; return this; }
        public TelefoneBuilder tipo(TipoTelefone tipo) { this.tipo = tipo; return this; }
        public TelefoneBuilder isPrincipal(Boolean isPrincipal) { this.isPrincipal = isPrincipal; return this; }
        public TelefoneBuilder usuario(Usuario usuario) { this.usuario = usuario; return this; }
        public TelefoneBuilder criadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; return this; }
        public TelefoneBuilder atualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; return this; }
        
        public Telefone build() {
            return new Telefone(id, numero, tipo, isPrincipal, usuario, criadoEm, atualizadoEm);
        }
    }
}
