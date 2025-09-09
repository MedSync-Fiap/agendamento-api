package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    
    Optional<UsuarioJpaEntity> findByEmail(String email);
    
    Optional<UsuarioJpaEntity> findByCpf(String cpf);
    
    List<UsuarioJpaEntity> findByRoleId(UUID roleId);
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.roleId = :roleId AND u.ativo = true")
    List<UsuarioJpaEntity> findMedicosByRoleId(@Param("roleId") UUID roleId);
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.roleId = :roleId AND u.ativo = true")
    List<UsuarioJpaEntity> findPacientesByRoleId(@Param("roleId") UUID roleId);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.ativo = true")
    List<UsuarioJpaEntity> findAllActive();
}
