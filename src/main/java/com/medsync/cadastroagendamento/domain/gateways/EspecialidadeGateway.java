package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EspecialidadeGateway {
    Especialidade salvar(Especialidade especialidade);
    Optional<Especialidade> buscarPorId(UUID id);
    Optional<Especialidade> buscarPorNome(String nome);
    List<Especialidade> buscarTodas();
    void deletar(UUID id);
    boolean existePorNome(String nome);
}
