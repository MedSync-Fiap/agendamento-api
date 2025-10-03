package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.RoleJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UsuarioMapper {
    
    @Mapping(target = "role", source = "role")
    Usuario toDomain(UsuarioJpaEntity jpaEntity);
    
    @Mapping(target = "role", source = "role")
    UsuarioJpaEntity toJpa(Usuario domain);
}
