package com.medsync.cadastroagendamento.domain.entities;

import com.medsync.cadastroagendamento.domain.enums.TipoTelefone;
import java.util.UUID;

public class Telefone {
    private UUID id;
    private UUID usuarioId;
    private String numero;
    private TipoTelefone tipo;

    public Telefone() {}

    public Telefone(UUID id, UUID usuarioId, String numero, TipoTelefone tipo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.numero = numero;
        this.tipo = tipo;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
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
}
