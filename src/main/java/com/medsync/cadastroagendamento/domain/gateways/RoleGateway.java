package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleGateway {
    Optional<Role> buscarPorId(UUID id);
    List<Role> buscarTodas();
    Role salvar(Role role);
}
