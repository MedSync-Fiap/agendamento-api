package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.TipoRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioGateway {
    Optional<Usuario> buscarPorId(UUID id);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorCpf(String cpf);
    Usuario salvar(Usuario usuario);
    List<Usuario> buscarTodos();
    List<Usuario> buscarPorRole(UUID roleId);
    List<Usuario> buscarUsuariosPorRole(TipoRole role);
    void deletar(UUID id);
    boolean existePorEmail(String email);
    boolean existePorCpf(String cpf);
    
    // Métodos para soft delete
    void reativarUsuario(UUID id);
    List<Usuario> buscarUsuariosInativos();
    Optional<Usuario> buscarPorIdIncluindoInativos(UUID id);
}