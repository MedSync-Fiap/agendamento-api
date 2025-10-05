package com.medsync.cadastroagendamento.infrastructure.repositories;

import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByEmail(String email);
    Optional<UsuarioJpaEntity> findByCpf(String cpf);
    List<UsuarioJpaEntity> findByRoleTipo(String role);
    List<UsuarioJpaEntity> findByRoleId(UUID roleId);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
