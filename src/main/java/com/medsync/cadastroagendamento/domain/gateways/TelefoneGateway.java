package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Telefone;

import java.util.List;
import java.util.UUID;

public interface TelefoneGateway {
    List<Telefone> buscarPorUsuarioId(UUID usuarioId);
    Telefone salvar(Telefone telefone);
    void deletar(UUID telefoneId);
    void deletarPorUsuarioId(UUID usuarioId);
}

