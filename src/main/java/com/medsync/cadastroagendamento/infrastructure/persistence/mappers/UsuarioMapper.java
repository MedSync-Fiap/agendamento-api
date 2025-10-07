package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.RoleJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, TelefoneMapper.class})
public interface UsuarioMapper {
    
    @Mapping(target = "role", source = "role")
    Usuario toDomain(UsuarioJpaEntity jpaEntity);
    
    @Mapping(target = "role", source = "role")
    @Mapping(target = "roleId", source = "role", qualifiedByName = "roleToRoleId")
    @Mapping(target = "telefones", ignore = true)
    UsuarioJpaEntity toJpa(Usuario domain);
    
    @Named("roleToRoleId")
    default UUID roleToRoleId(Role role) {
        return role != null ? role.getId() : null;
    }
}
