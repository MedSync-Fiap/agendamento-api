package com.medsync.cadastroagendamento.presentation.mappers;

import com.medsync.cadastroagendamento.presentation.dto.AtualizarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import com.medsync.cadastroagendamento.domain.entities.Telefone;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.presentation.dto.TelefoneResponse;
import com.medsync.cadastroagendamento.presentation.dto.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioDtoMapper {
    
    
    AtualizarUsuarioRequest toUseCaseRequest(AtualizarUsuarioRequest dto);
    
    UsuarioResponse toResponse(Usuario usuario);

    
    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);
    
    TelefoneResponse toTelefoneResponse(Telefone telefone);
}
