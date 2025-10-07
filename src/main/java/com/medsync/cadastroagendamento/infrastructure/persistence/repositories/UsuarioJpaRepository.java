package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.domain.enums.TipoRole;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.email = :email AND u.ativo = true")
    Optional<UsuarioJpaEntity> findByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.cpf = :cpf AND u.ativo = true")
    Optional<UsuarioJpaEntity> findByCpf(@Param("cpf") String cpf);
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.roleId = :roleId AND u.ativo = true")
    List<UsuarioJpaEntity> findByRoleId(@Param("roleId") UUID roleId);
    
    @Query("SELECT COUNT(u) > 0 FROM UsuarioJpaEntity u WHERE u.email = :email AND u.ativo = true")
    boolean existsByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(u) > 0 FROM UsuarioJpaEntity u WHERE u.cpf = :cpf AND u.ativo = true")
    boolean existsByCpf(@Param("cpf") String cpf);
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.ativo = true")
    List<UsuarioJpaEntity> findAllActive();
    
    @Query("SELECT u FROM UsuarioJpaEntity u LEFT JOIN FETCH u.telefones WHERE u.id = :id AND u.ativo = true")
    Optional<UsuarioJpaEntity> findByIdWithTelefones(@Param("id") UUID id);
    
    @Query("SELECT u FROM UsuarioJpaEntity u LEFT JOIN FETCH u.telefones WHERE u.email = :email AND u.ativo = true")
    Optional<UsuarioJpaEntity> findByEmailWithTelefones(@Param("email") String email);
    
    @Query("SELECT u FROM UsuarioJpaEntity u JOIN u.role r WHERE r.tipo = :roleTipo AND u.ativo = true")
    List<UsuarioJpaEntity> findByRoleTipo(@Param("roleTipo") TipoRole roleTipo);
    
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioJpaEntity u SET u.ativo = false, u.atualizadoEm = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(@Param("id") UUID id);
    
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioJpaEntity u SET u.ativo = :ativo, u.atualizadoEm = CURRENT_TIMESTAMP WHERE u.id = :id")
    void updateAtivoStatus(@Param("id") UUID id, @Param("ativo") boolean ativo);
    
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.ativo = false")
    List<UsuarioJpaEntity> findAllInactive();
    
    @Query("SELECT u FROM UsuarioJpaEntity u LEFT JOIN FETCH u.telefones WHERE u.id = :id")
    Optional<UsuarioJpaEntity> findByIdWithTelefonesIncludingInactive(@Param("id") UUID id);
}
