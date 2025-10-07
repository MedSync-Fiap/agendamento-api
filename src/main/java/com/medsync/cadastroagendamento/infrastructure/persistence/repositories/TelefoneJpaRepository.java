package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.infrastructure.persistence.entities.TelefoneJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TelefoneJpaRepository extends JpaRepository<TelefoneJpaEntity, UUID> {
    
    @Query("SELECT t FROM TelefoneJpaEntity t WHERE t.usuario.id = :usuarioId")
    List<TelefoneJpaEntity> findByUsuarioId(@Param("usuarioId") UUID usuarioId);
    
    @Query("DELETE FROM TelefoneJpaEntity t WHERE t.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") UUID usuarioId);
}

