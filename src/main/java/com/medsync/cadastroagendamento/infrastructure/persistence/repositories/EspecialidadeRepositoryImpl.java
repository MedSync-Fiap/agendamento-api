package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.domain.gateways.EspecialidadeGateway;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.EspecialidadeJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.mappers.EspecialidadeMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EspecialidadeRepositoryImpl implements EspecialidadeGateway {
    
    private final EspecialidadeJpaRepository jpaRepository;
    private final EspecialidadeMapper mapper;
    
    public EspecialidadeRepositoryImpl(EspecialidadeJpaRepository jpaRepository, EspecialidadeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Especialidade salvar(Especialidade especialidade) {
        EspecialidadeJpaEntity jpaEntity = mapper.toJpa(especialidade);
        EspecialidadeJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Especialidade> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Especialidade> buscarPorNome(String nome) {
        return jpaRepository.findByNome(nome)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Especialidade> buscarTodas() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNome(nome);
    }
}
