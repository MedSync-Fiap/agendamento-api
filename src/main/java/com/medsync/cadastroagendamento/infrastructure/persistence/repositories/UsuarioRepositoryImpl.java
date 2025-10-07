package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.TipoRole;
import com.medsync.cadastroagendamento.domain.exception.DatabaseException;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.mappers.UsuarioMapper;
import com.medsync.cadastroagendamento.infrastructure.persistence.repositories.RoleJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepositoryImpl implements UsuarioGateway {
    
    private static final Logger log = LoggerFactory.getLogger(UsuarioRepositoryImpl.class);
    
    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;
    private final RoleJpaRepository roleJpaRepository;
    
    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper, RoleJpaRepository roleJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.roleJpaRepository = roleJpaRepository;
    }
    
    @Override
    public Usuario salvar(Usuario usuario) {
        try {
            UsuarioJpaEntity jpaEntity = mapper.toJpa(usuario);
            UsuarioJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return mapper.toDomain(savedEntity);
        } catch (Exception e) {
            log.error("Erro ao salvar usuário: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao salvar usuário no banco de dados", e);
        }
    }
    
    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        try {
            return jpaRepository.findByIdWithTelefones(id)
                    .map(jpaEntity -> {
                        // Carregar role com permissões
                        if (jpaEntity.getRoleId() != null) {
                            roleJpaRepository.findByIdWithPermissions(jpaEntity.getRoleId())
                                    .ifPresent(jpaEntity::setRole);
                        }
                        return mapper.toDomain(jpaEntity);
                    });
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar usuário no banco de dados", e);
        }
    }
    
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmailWithTelefones(email)
                .map(jpaEntity -> {
                    // Carregar role com permissões
                    if (jpaEntity.getRoleId() != null) {
                        roleJpaRepository.findByIdWithPermissions(jpaEntity.getRoleId())
                                .ifPresent(jpaEntity::setRole);
                    }
                    return mapper.toDomain(jpaEntity);
                });
    }
    
    @Override
    public Optional<Usuario> buscarPorCpf(String cpf) {
        return jpaRepository.findByCpf(cpf)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Usuario> buscarPorRole(UUID roleId) {
        return jpaRepository.findByRoleId(roleId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Usuario> buscarTodos() {
        try {
            return jpaRepository.findAllActive()
                    .stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (Exception e) {
            log.error("Erro ao buscar todos os usuários: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar usuários no banco de dados", e);
        }
    }
    
    
    @Override
    public void deletar(UUID id) {
        try {
            // Soft delete - marca como inativo ao invés de deletar fisicamente
            jpaRepository.softDeleteById(id);
            log.info("Usuário {} marcado como inativo (soft delete)", id);
        } catch (Exception e) {
            log.error("Erro ao deletar usuário {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Falha ao deletar usuário no banco de dados", e);
        }
    }
    
    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existePorCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }
    
    @Override
    public List<Usuario> buscarUsuariosPorRole(TipoRole role) {
        return jpaRepository.findByRoleTipo(role)
                .stream()
                .map(jpaEntity -> {
                    // Carregar role com permissões
                    if (jpaEntity.getRoleId() != null) {
                        roleJpaRepository.findByIdWithPermissions(jpaEntity.getRoleId())
                                .ifPresent(jpaEntity::setRole);
                    }
                    return mapper.toDomain(jpaEntity);
                })
                .toList();
    }
    
    // Métodos adicionais para soft delete
    public void reativarUsuario(UUID id) {
        try {
            jpaRepository.updateAtivoStatus(id, true);
            log.info("Usuário {} reativado", id);
        } catch (Exception e) {
            log.error("Erro ao reativar usuário {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Falha ao reativar usuário no banco de dados", e);
        }
    }
    
    public List<Usuario> buscarUsuariosInativos() {
        try {
            return jpaRepository.findAllInactive()
                    .stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (Exception e) {
            log.error("Erro ao buscar usuários inativos: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar usuários inativos no banco de dados", e);
        }
    }
    
    public Optional<Usuario> buscarPorIdIncluindoInativos(UUID id) {
        try {
            return jpaRepository.findByIdWithTelefonesIncludingInactive(id)
                    .map(jpaEntity -> {
                        // Carregar role com permissões
                        if (jpaEntity.getRoleId() != null) {
                            roleJpaRepository.findByIdWithPermissions(jpaEntity.getRoleId())
                                    .ifPresent(jpaEntity::setRole);
                        }
                        return mapper.toDomain(jpaEntity);
                    });
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID (incluindo inativos) {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Falha ao buscar usuário no banco de dados", e);
        }
    }
}
