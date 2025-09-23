package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoFeignClient;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarHistoricoPacienteUseCase Tests")
class BuscarHistoricoPacienteUseCaseTest {

    @Mock
    private HistoricoFeignClient historicoFeignClient;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase;

    private UUID pacienteId;
    private UUID usuarioLogadoId;
    private HistoricoPacienteResponse historicoResponse;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        usuarioLogadoId = UUID.randomUUID();
        
        historicoResponse = new HistoricoPacienteResponse(
            pacienteId,
            "Paciente Teste",
            "12345678901",
            "paciente@test.com",
            List.of()
        );

        when(circuitBreakerFactory.create("historico-service")).thenReturn(circuitBreaker);
    }

    @Test
    @DisplayName("Deve buscar histórico com sucesso")
    void deveBuscarHistoricoComSucesso() {
        // Given
        when(historicoFeignClient.buscarHistoricoPaciente(pacienteId))
            .thenReturn(historicoResponse);
        
        when(circuitBreaker.run(any(Supplier.class), any(Function.class)))
            .thenAnswer(invocation -> {
                Supplier<HistoricoPacienteResponse> supplier = invocation.getArgument(0);
                return supplier.get();
            });

        // When
        HistoricoPacienteResponse resultado = buscarHistoricoPacienteUseCase
            .executar(pacienteId, usuarioLogadoId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.pacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.pacienteNome()).isEqualTo("Paciente Teste");
        assertThat(resultado.pacienteCpf()).isEqualTo("12345678901");
        assertThat(resultado.pacienteEmail()).isEqualTo("paciente@test.com");
        assertThat(resultado.consultas()).isEmpty();

        verify(historicoFeignClient).buscarHistoricoPaciente(pacienteId);
        verify(circuitBreaker).run(any(Supplier.class), any(Function.class));
    }

    @Test
    @DisplayName("Deve executar fallback quando cliente falha")
    void deveExecutarFallbackQuandoClienteFalha() {
        // Given
        HistoricoPacienteResponse fallbackResponse = new HistoricoPacienteResponse(
            pacienteId,
            "Histórico temporariamente indisponível",
            "",
            "",
            List.of()
        );

        when(circuitBreaker.run(any(Supplier.class), any(Function.class)))
            .thenAnswer(invocation -> {
                Function<Throwable, HistoricoPacienteResponse> fallback = invocation.getArgument(1);
                return fallback.apply(new RuntimeException("Serviço indisponível"));
            });

        // When
        HistoricoPacienteResponse resultado = buscarHistoricoPacienteUseCase
            .executar(pacienteId, usuarioLogadoId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.pacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.pacienteNome()).isEqualTo("Histórico temporariamente indisponível");
        assertThat(resultado.pacienteCpf()).isEmpty();
        assertThat(resultado.pacienteEmail()).isEmpty();
        assertThat(resultado.consultas()).isEmpty();

        verify(circuitBreaker).run(any(Supplier.class), any(Function.class));
    }

    @Test
    @DisplayName("Deve chamar cliente com parâmetros corretos")
    void deveChamarClienteComParametrosCorretos() {
        // Given
        when(historicoFeignClient.buscarHistoricoPaciente(any(UUID.class)))
            .thenReturn(historicoResponse);
        
        when(circuitBreaker.run(any(Supplier.class), any(Function.class)))
            .thenAnswer(invocation -> {
                Supplier<HistoricoPacienteResponse> supplier = invocation.getArgument(0);
                return supplier.get();
            });

        // When
        buscarHistoricoPacienteUseCase.executar(pacienteId, usuarioLogadoId);

        // Then
        verify(historicoFeignClient).buscarHistoricoPaciente(pacienteId);
        verify(historicoFeignClient, never()).buscarHistoricoPaciente(usuarioLogadoId);
        verify(circuitBreaker).run(any(Supplier.class), any(Function.class));
    }
}