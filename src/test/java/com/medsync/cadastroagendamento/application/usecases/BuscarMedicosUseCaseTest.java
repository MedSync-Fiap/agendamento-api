package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.TipoRole;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarMedicosUseCase Tests")
class BuscarMedicosUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarMedicosUseCase buscarMedicosUseCase;

    private List<Usuario> medicos;

    @BeforeEach
    void setUp() {
        Role roleMedico1 = new Role();
        roleMedico1.setId(UUID.randomUUID());
        roleMedico1.setTipo(TipoRole.MEDICO);
        roleMedico1.setDescricao("Médico");
        roleMedico1.setCriadoEm(LocalDateTime.now().minusDays(1));
        roleMedico1.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        Role roleMedico2 = new Role();
        roleMedico2.setId(UUID.randomUUID());
        roleMedico2.setTipo(TipoRole.MEDICO);
        roleMedico2.setDescricao("Médico");
        roleMedico2.setCriadoEm(LocalDateTime.now().minusDays(2));
        roleMedico2.setAtualizadoEm(LocalDateTime.now().minusDays(2));

        Usuario medico1 = new Usuario();
        medico1.setId(UUID.randomUUID());
        medico1.setNome("Dr. João Silva");
        medico1.setCpf("12345678901");
        medico1.setEmail("joao@email.com");
        medico1.setSenhaHash("senha_hash");
        medico1.setRole(roleMedico1);
        medico1.setAtivo(true);
        medico1.setCriadoEm(LocalDateTime.now().minusDays(1));
        medico1.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        Usuario medico2 = new Usuario();
        medico2.setId(UUID.randomUUID());
        medico2.setNome("Dra. Maria Santos");
        medico2.setCpf("98765432109");
        medico2.setEmail("maria@email.com");
        medico2.setSenhaHash("senha_hash");
        medico2.setRole(roleMedico2);
        medico2.setAtivo(true);
        medico2.setCriadoEm(LocalDateTime.now().minusDays(2));
        medico2.setAtualizadoEm(LocalDateTime.now().minusDays(2));

        medicos = Arrays.asList(medico1, medico2);
    }

    @Test
    @DisplayName("Deve buscar médicos com sucesso")
    void deveBuscarMedicosComSucesso() {
        // Given
        when(usuarioGateway.buscarMedicos()).thenReturn(medicos);

        // When
        List<Usuario> resultado = buscarMedicosUseCase.executar();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Dr. João Silva");
        assertThat(resultado.get(1).getNome()).isEqualTo("Dra. Maria Santos");

        verify(usuarioGateway).buscarMedicos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há médicos")
    void deveRetornarListaVaziaQuandoNaoHaMedicos() {
        // Given
        when(usuarioGateway.buscarMedicos()).thenReturn(Arrays.asList());

        // When
        List<Usuario> resultado = buscarMedicosUseCase.executar();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(usuarioGateway).buscarMedicos();
    }
}
