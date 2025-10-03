package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.infrastructure.persistence.entities.EspecialidadeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EspecialidadeJpaRepository extends JpaRepository<EspecialidadeJpaEntity, UUID> {
    
    Optional<EspecialidadeJpaEntity> findByNome(String nome);
    
    boolean existsByNome(String nome);
    
    @Query(value = "SELECT e.* FROM tb_especialidade e " +
                   "INNER JOIN tb_especialidade_medico em ON e.id = em.especialidade_id " +
                   "WHERE em.medico_id = :medicoId", nativeQuery = true)
    List<EspecialidadeJpaEntity> findByMedicoId(@Param("medicoId") UUID medicoId);
}
