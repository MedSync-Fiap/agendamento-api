package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UsuarioMapper {
    
    @Mapping(target = "role", source = "role")
    Usuario toDomain(UsuarioJpaEntity jpaEntity);
    
    @Mapping(target = "role", source = "role")
    @Mapping(target = "roleId", source = "role.id")
    UsuarioJpaEntity toJpa(Usuario domain);
}
