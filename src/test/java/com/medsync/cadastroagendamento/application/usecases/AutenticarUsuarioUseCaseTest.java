package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.CredenciaisInvalidasException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutenticarUsuarioUseCase Tests")
class AutenticarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    private Usuario usuario;
    private String email;
    private String senha;
    private String senhaHash;
    private AutenticarUsuarioUseCase.AutenticarUsuarioRequest request;

    @BeforeEach
    void setUp() {
        email = "joao@email.com";
        senha = "senha123";
        senhaHash = "hash_da_senha";

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail(email);
        usuario.setSenhaHash(senhaHash);
        usuario.setRoleId(UUID.randomUUID());
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now().minusDays(1));
        usuario.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        request = new AutenticarUsuarioUseCase.AutenticarUsuarioRequest(email, senha);
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso quando credenciais são válidas")
    void deveAutenticarUsuarioComSucesso() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        Usuario resultado = autenticarUsuarioUseCase.executar(request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(usuario);
        assertThat(resultado.getEmail()).isEqualTo(email);
        assertThat(resultado.getNome()).isEqualTo("João Silva");

        verify(usuarioGateway).buscarPorEmail(email);
        verify(passwordEncoder).matches(senha, senhaHash);
    }

    @Test
    @DisplayName("Deve lançar CredenciaisInvalidasException quando usuário não existe")
    void deveLancarCredenciaisInvalidasExceptionQuandoUsuarioNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> autenticarUsuarioUseCase.executar(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Email ou senha inválidos");

        verify(usuarioGateway).buscarPorEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar CredenciaisInvalidasException quando senha está incorreta")
    void deveLancarCredenciaisInvalidasExceptionQuandoSenhaEstaIncorreta() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> autenticarUsuarioUseCase.executar(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Email ou senha inválidos");

        verify(usuarioGateway).buscarPorEmail(email);
        verify(passwordEncoder).matches(senha, senhaHash);
    }

    @Test
    @DisplayName("Deve lançar CredenciaisInvalidasException quando usuário está inativo")
    void deveLancarCredenciaisInvalidasExceptionQuandoUsuarioEstaInativo() {
        // Given
        usuario.setAtivo(false);
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));

        // When & Then
        assertThatThrownBy(() -> autenticarUsuarioUseCase.executar(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Usuário inativo");

        verify(usuarioGateway).buscarPorEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar usuário autenticado com informações corretas")
    void deveRetornarUsuarioAutenticadoComInformacoesCorretas() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        Usuario resultado = autenticarUsuarioUseCase.executar(request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo(email);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar usuário correto quando autenticação é bem-sucedida")
    void deveRetornarUsuarioCorretoQuandoAutenticacaoEBemSucedida() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        Usuario resultado = autenticarUsuarioUseCase.executar(request);

        // Then
        assertThat(resultado).isEqualTo(usuario);
        assertThat(resultado.getEmail()).isEqualTo(email);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve verificar senha usando PasswordEncoder")
    void deveVerificarSenhaUsandoPasswordEncoder() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        autenticarUsuarioUseCase.executar(request);

        // Then
        verify(passwordEncoder).matches(senha, senhaHash);
    }

    @Test
    @DisplayName("Deve buscar usuário por email no gateway")
    void deveBuscarUsuarioPorEmailNoGateway() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        autenticarUsuarioUseCase.executar(request);

        // Then
        verify(usuarioGateway).buscarPorEmail(email);
    }

    @Test
    @DisplayName("Deve funcionar com diferentes tipos de usuários (médico, paciente, enfermeiro)")
    void deveFuncionarComDiferentesTiposDeUsuarios() {
        // Given
        UUID roleMedico = UUID.randomUUID();
        usuario.setRoleId(roleMedico);
        
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        Usuario resultado = autenticarUsuarioUseCase.executar(request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getRoleId()).isEqualTo(roleMedico);
        assertThat(resultado.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Deve tratar email em diferentes formatos (maiúscula/minúscula)")
    void deveTratarEmailEmDiferentesFormatos() {
        // Given
        String emailMaiusculo = "JOAO@EMAIL.COM";
        AutenticarUsuarioUseCase.AutenticarUsuarioRequest requestMaiusculo = 
                new AutenticarUsuarioUseCase.AutenticarUsuarioRequest(emailMaiusculo, senha);
        
        when(usuarioGateway.buscarPorEmail(emailMaiusculo)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        Usuario resultado = autenticarUsuarioUseCase.executar(requestMaiusculo);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(usuario);
        verify(usuarioGateway).buscarPorEmail(emailMaiusculo);
    }

    @Test
    @DisplayName("Deve retornar usuário com estrutura correta")
    void deveRetornarUsuarioComEstruturaCorreta() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaHash)).thenReturn(true);

        // When
        Usuario resultado = autenticarUsuarioUseCase.executar(request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo(email);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getCpf()).isEqualTo("12345678901");
        assertThat(resultado.isAtivo()).isTrue();
    }
}
