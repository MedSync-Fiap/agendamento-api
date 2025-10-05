package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import java.util.List;
import java.util.UUID;

public interface MedicoEspecialidadeGateway {
    List<Especialidade> buscarEspecialidadesPorMedico(UUID medicoId);
    String buscarPrimeiraEspecialidade(UUID medicoId);
}
