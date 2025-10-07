package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-07T10:08:45-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Usuario toDomain(UsuarioJpaEntity jpaEntity) {
        if ( jpaEntity == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setRole( roleMapper.toDomain( jpaEntity.getRole() ) );
        usuario.setId( jpaEntity.getId() );
        usuario.setNome( jpaEntity.getNome() );
        usuario.setCpf( jpaEntity.getCpf() );
        usuario.setEmail( jpaEntity.getEmail() );
        usuario.setSenhaHash( jpaEntity.getSenhaHash() );
        usuario.setDataNascimento( jpaEntity.getDataNascimento() );
        usuario.setAtivo( jpaEntity.isAtivo() );
        usuario.setCriadoEm( jpaEntity.getCriadoEm() );
        usuario.setAtualizadoEm( jpaEntity.getAtualizadoEm() );

        return usuario;
    }

    @Override
    public UsuarioJpaEntity toJpa(Usuario domain) {
        if ( domain == null ) {
            return null;
        }

        UsuarioJpaEntity usuarioJpaEntity = new UsuarioJpaEntity();

        usuarioJpaEntity.setRole( roleMapper.toJpa( domain.getRole() ) );
        usuarioJpaEntity.setRoleId( roleToRoleId( domain.getRole() ) );
        usuarioJpaEntity.setId( domain.getId() );
        usuarioJpaEntity.setNome( domain.getNome() );
        usuarioJpaEntity.setCpf( domain.getCpf() );
        usuarioJpaEntity.setEmail( domain.getEmail() );
        usuarioJpaEntity.setSenhaHash( domain.getSenhaHash() );
        usuarioJpaEntity.setDataNascimento( domain.getDataNascimento() );
        usuarioJpaEntity.setAtivo( domain.isAtivo() );
        usuarioJpaEntity.setCriadoEm( domain.getCriadoEm() );
        usuarioJpaEntity.setAtualizadoEm( domain.getAtualizadoEm() );

        return usuarioJpaEntity;
    }
}
