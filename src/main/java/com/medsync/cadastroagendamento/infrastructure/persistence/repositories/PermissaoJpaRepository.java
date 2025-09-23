package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.infrastructure.persistence.entities.PermissaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissaoJpaRepository extends JpaRepository<PermissaoJpaEntity, UUID> {
    
    Optional<PermissaoJpaEntity> findByNome(String nome);
}
