package com.medsync.cadastroagendamento.infrastructure.clients;

import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoricoFeignClient Tests")
class HistoricoFeignClientTest {

    @Mock
    private HistoricoFeignClient historicoFeignClient;

    private UUID pacienteId;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve ter anotação FeignClient configurada corretamente")
    void deveTerAnotacaoFeignClientConfiguradaCorretamente() {
        // Given
        Class<HistoricoFeignClient> clientClass = HistoricoFeignClient.class;

        // When
        FeignClient annotation = clientClass.getAnnotation(FeignClient.class);

        // Then
        assertNotNull(annotation);
        assertEquals("historico-service", annotation.name());
        assertEquals("${app.services.historico.url}", annotation.url());
        assertEquals(HistoricoFeignClientFallback.class, annotation.fallback());
    }

    @Test
    @DisplayName("Deve ter método buscarHistoricoPaciente com anotações corretas")
    void deveTerMetodoBuscarHistoricoPacienteComAnotacoesCorretas() throws NoSuchMethodException {
        // Given
        Method method = HistoricoFeignClient.class.getMethod("buscarHistoricoPaciente", UUID.class);

        // When
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PathVariable pathVariable = method.getParameters()[0].getAnnotation(PathVariable.class);

        // Then
        assertNotNull(getMapping);
        assertEquals("/historico/paciente/{pacienteId}", getMapping.value()[0]);
        assertNotNull(pathVariable);
        assertEquals("pacienteId", pathVariable.value());
    }

    @Test
    @DisplayName("Deve ter assinatura de método correta")
    void deveTerAssinaturaDeMetodoCorreta() throws NoSuchMethodException {
        // Given
        Method method = HistoricoFeignClient.class.getMethod("buscarHistoricoPaciente", UUID.class);

        // Then
        assertEquals(HistoricoPacienteResponse.class, method.getReturnType());
        assertEquals(1, method.getParameterCount());
        assertEquals(UUID.class, method.getParameterTypes()[0]);
    }

    @Test
    @DisplayName("Deve ser uma interface")
    void deveSerUmaInterface() {
        // Then
        assertTrue(HistoricoFeignClient.class.isInterface());
    }
}
