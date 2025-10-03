package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.domain.gateways.EspecialidadeGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarEspecialidadesUseCase {
    
    private final EspecialidadeGateway especialidadeGateway;
    
    public ListarEspecialidadesUseCase(EspecialidadeGateway especialidadeGateway) {
        this.especialidadeGateway = especialidadeGateway;
    }
    
    public List<Especialidade> executar() {
        return especialidadeGateway.buscarTodas();
    }
}
