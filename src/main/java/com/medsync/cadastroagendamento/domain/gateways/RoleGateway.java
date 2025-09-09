package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleGateway {
    Role salvar(Role role);
    Optional<Role> buscarPorId(UUID id);
    Optional<Role> buscarPorNome(String nome);
    List<Role> buscarTodas();
    void deletar(UUID id);
    boolean existePorNome(String nome);
}
