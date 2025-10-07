package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.domain.entities.Telefone;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.gateways.TelefoneGateway;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.TelefoneJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.mappers.TelefoneMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TelefoneRepositoryImpl implements TelefoneGateway {
    
    private static final Logger log = LoggerFactory.getLogger(TelefoneRepositoryImpl.class);
    
    private final TelefoneJpaRepository jpaRepository;
    private final TelefoneMapper mapper;
    
    public TelefoneRepositoryImpl(TelefoneJpaRepository jpaRepository, TelefoneMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public List<Telefone> buscarPorUsuarioId(UUID usuarioId) {
        try {
            List<TelefoneJpaEntity> jpaEntities = jpaRepository.findByUsuarioId(usuarioId);
            return jpaEntities.stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (Exception e) {
            log.error("Erro ao buscar telefones por usuário ID {}: {}", usuarioId, e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar telefones no banco de dados", e);
        }
    }
    
    @Override
    public Telefone salvar(Telefone telefone) {
        try {
            TelefoneJpaEntity jpaEntity = mapper.toJpa(telefone);
            TelefoneJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return mapper.toDomain(savedEntity);
        } catch (Exception e) {
            log.error("Erro ao salvar telefone: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao salvar telefone no banco de dados", e);
        }
    }
    
    @Override
    public void deletar(UUID telefoneId) {
        try {
            jpaRepository.deleteById(telefoneId);
        } catch (Exception e) {
            log.error("Erro ao deletar telefone {}: {}", telefoneId, e.getMessage(), e);
            throw new DatabaseException("Falha ao deletar telefone no banco de dados", e);
        }
    }
    
    @Override
    public void deletarPorUsuarioId(UUID usuarioId) {
        try {
            jpaRepository.deleteByUsuarioId(usuarioId);
        } catch (Exception e) {
            log.error("Erro ao deletar telefones por usuário ID {}: {}", usuarioId, e.getMessage(), e);
            throw new DatabaseException("Falha ao deletar telefones no banco de dados", e);
        }
    }
}
