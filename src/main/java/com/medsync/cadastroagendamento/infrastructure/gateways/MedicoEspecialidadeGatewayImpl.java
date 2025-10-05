package com.medsync.cadastroagendamento.infrastructure.gateways;

import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.domain.gateways.MedicoEspecialidadeGateway;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.EspecialidadeJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.mappers.EspecialidadeMapper;
import com.medsync.cadastroagendamento.infrastructure.persistence.repositories.EspecialidadeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MedicoEspecialidadeGatewayImpl implements MedicoEspecialidadeGateway {
    
    private final EspecialidadeJpaRepository especialidadeJpaRepository;
    private final EspecialidadeMapper especialidadeMapper;
    
    public MedicoEspecialidadeGatewayImpl(EspecialidadeJpaRepository especialidadeJpaRepository, 
                                        EspecialidadeMapper especialidadeMapper) {
        this.especialidadeJpaRepository = especialidadeJpaRepository;
        this.especialidadeMapper = especialidadeMapper;
    }
    
    @Override
    public List<Especialidade> buscarEspecialidadesPorMedico(UUID medicoId) {
        return especialidadeJpaRepository.findByMedicoId(medicoId)
                .stream()
                .map(especialidadeMapper::toDomain)
                .toList();
    }
    
    @Override
    public String buscarPrimeiraEspecialidade(UUID medicoId) {
        List<EspecialidadeJpaEntity> especialidades = especialidadeJpaRepository.findByMedicoId(medicoId);
        if (!especialidades.isEmpty()) {
            return especialidades.get(0).getNome();
        }
        return "Especialidade não definida";
    }
}
