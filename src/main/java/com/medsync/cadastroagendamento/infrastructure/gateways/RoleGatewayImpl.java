package com.medsync.cadastroagendamento.infrastructure.gateways;

import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.gateways.RoleGateway;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.RoleJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.mappers.RoleMapper;
import com.medsync.cadastroagendamento.infrastructure.persistence.repositories.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoleGatewayImpl implements RoleGateway {
    
    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;
    
    public RoleGatewayImpl(RoleJpaRepository roleJpaRepository, RoleMapper roleMapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.roleMapper = roleMapper;
    }
    
    @Override
    public Optional<Role> buscarPorId(UUID id) {
        return roleJpaRepository.findByIdWithPermissions(id)
                .map(roleMapper::toDomain);
    }
    
    @Override
    public List<Role> buscarTodas() {
        return roleJpaRepository.findAll()
                .stream()
                .map(roleMapper::toDomain)
                .toList();
    }
    
    @Override
    public Role salvar(Role role) {
        RoleJpaEntity jpaEntity = roleMapper.toJpa(role);
        RoleJpaEntity savedEntity = roleJpaRepository.save(jpaEntity);
        return roleMapper.toDomain(savedEntity);
    }
}
