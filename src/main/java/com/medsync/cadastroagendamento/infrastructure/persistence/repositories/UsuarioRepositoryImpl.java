package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.mappers.UsuarioMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepositoryImpl implements UsuarioGateway {
    
    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;
    
    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity jpaEntity = mapper.toJpa(usuario);
        UsuarioJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
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
        return jpaRepository.findAllActive()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Usuario> buscarMedicos() {
        // Assumindo que o roleId para médicos é conhecido
        // Em uma implementação real, você buscaria o roleId do enum TipoRole.MEDICO
        return jpaRepository.findByRoleId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Usuario> buscarPacientes() {
        // Assumindo que o roleId para pacientes é conhecido
        return jpaRepository.findByRoleId(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existePorCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }
}
