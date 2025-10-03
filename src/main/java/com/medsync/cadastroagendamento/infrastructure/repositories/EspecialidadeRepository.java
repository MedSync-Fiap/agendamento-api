package com.medsync.cadastroagendamento.infrastructure.repositories;

import com.medsync.cadastroagendamento.infrastructure.persistence.entities.EspecialidadeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EspecialidadeRepository extends JpaRepository<EspecialidadeJpaEntity, UUID> {
    Optional<EspecialidadeJpaEntity> findByNome(String nome);
    boolean existsByNome(String nome);
}
