package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Permissao;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.PermissaoJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.RoleJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    Role toDomain(RoleJpaEntity jpaEntity);
    
    RoleJpaEntity toJpa(Role domain);
    
    @Named("permissaoJpaToPermissao")
    default List<Permissao> permissaoJpaToPermissao(List<PermissaoJpaEntity> permissoesJpa) {
        if (permissoesJpa == null) {
            return null;
        }
        return permissoesJpa.stream()
                .map(this::permissaoJpaToPermissao)
                .toList();
    }
    
    @Named("permissaoToPermissaoJpa")
    default List<PermissaoJpaEntity> permissaoToPermissaoJpa(List<Permissao> permissoes) {
        if (permissoes == null) {
            return null;
        }
        return permissoes.stream()
                .map(this::permissaoToPermissaoJpa)
                .toList();
    }
    
    Permissao permissaoJpaToPermissao(PermissaoJpaEntity permissaoJpa);
    
    PermissaoJpaEntity permissaoToPermissaoJpa(Permissao permissao);
}
