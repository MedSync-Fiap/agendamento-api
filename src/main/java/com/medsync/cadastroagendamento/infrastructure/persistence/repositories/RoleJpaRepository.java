package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.infrastructure.persistence.entities.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
    
    Optional<RoleJpaEntity> findByNome(String nome);
    
    @Query("SELECT r FROM RoleJpaEntity r LEFT JOIN FETCH r.permissoes WHERE r.id = :id")
    Optional<RoleJpaEntity> findByIdWithPermissions(@Param("id") UUID id);
}
