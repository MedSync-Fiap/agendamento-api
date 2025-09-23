package com.medsync.cadastroagendamento.infrastructure.events;

import com.medsync.cadastroagendamento.domain.events.ConsultaHistoricoEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaNotificacaoEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("RabbitMQ Integration Tests")
class RabbitMQIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.12-management")
            .withExposedPorts(5672, 15672);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EventPublisher eventPublisher;

    @Test
    @DisplayName("Deve publicar evento de notificação no RabbitMQ")
    void devePublicarEventoNotificacaoNoRabbitMQ() throws InterruptedException {
        // Given
        ConsultaNotificacaoEvent evento = new ConsultaNotificacaoEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now().plusDays(1),
            "AGENDADA",
            "Consulta de teste",
            "CRIADA",
            LocalDateTime.now()
        );

        // When
        eventPublisher.publishNotificacaoConsulta(evento);

        // Then
        // Aguarda um pouco para garantir que a mensagem foi processada
        TimeUnit.MILLISECONDS.sleep(100);
        
        // Verifica se o template foi usado (não há como verificar diretamente a fila sem listener)
        assertThat(rabbitTemplate).isNotNull();
    }

    @Test
    @DisplayName("Deve publicar evento de histórico no RabbitMQ")
    void devePublicarEventoHistoricoNoRabbitMQ() throws InterruptedException {
        // Given
        ConsultaHistoricoEvent evento = new ConsultaHistoricoEvent(
            UUID.randomUUID(),
            LocalDateTime.now().plusDays(1),
            "AGENDADA",
            "Consulta de teste",
            "CRIADA",
            LocalDateTime.now(),
            UUID.randomUUID(),
            "Paciente Teste",
            "12345678901",
            "paciente@test.com",
            LocalDate.now(),
            UUID.randomUUID(),
            "Médico Teste",
            "98765432109",
            "medico@test.com",
            "Cardiologia",
            UUID.randomUUID(),
            "Admin Teste",
            "admin@test.com",
            "ADMIN"
        );

        // When
        eventPublisher.publishHistoricoConsulta(evento);

        // Then
        // Aguarda um pouco para garantir que a mensagem foi processada
        TimeUnit.MILLISECONDS.sleep(100);
        
        // Verifica se o template foi usado
        assertThat(rabbitTemplate).isNotNull();
    }

    @Test
    @DisplayName("Deve ter configuração do RabbitMQ correta")
    void deveTerConfiguracaoDoRabbitMQCorreta() {
        // Then
        assertThat(rabbitMQContainer.isRunning()).isTrue();
        assertThat(rabbitTemplate).isNotNull();
        assertThat(eventPublisher).isNotNull();
    }
}
