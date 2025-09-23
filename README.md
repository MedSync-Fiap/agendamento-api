# MedSync - Serviço de Cadastro e Agendamento

Este é o serviço de cadastro e agendamento do sistema MedSync, responsável por gerenciar usuários (médicos, enfermeiros, pacientes) e agendamento de consultas.

## 🏗️ Arquitetura

O projeto segue os princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**, com as seguintes camadas:

- **Domain**: Entidades, repositórios e regras de negócio
- **Application**: Casos de uso e orquestração
- **Infrastructure**: Persistência, configurações e integrações externas
- **Presentation**: Controllers REST e DTOs

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Cloud** (Feign, Circuit Breaker)
- **PostgreSQL** (banco de dados)
- **RabbitMQ** (message broker)
- **JWT** (autenticação)
- **MapStruct** (mapeamento de objetos)
- **Flyway** (migrations)
- **Resilience4j** (Circuit Breaker, Retry)
- **OpenFeign** (HTTP client)
- **Maven** (gerenciamento de dependências)

## 📋 Pré-requisitos

- Java 21
- Maven 3.8+
- Docker e Docker Compose
- PostgreSQL 15+
- RabbitMQ 3.12+

## 🛠️ Configuração e Execução

### 1. Usando Docker Compose (Recomendado)

```bash
# Clonar o repositório
git clone <repository-url>
cd medsync

# Executar com Docker Compose
docker-compose up -d

# O serviço estará disponível em http://localhost:8080
```

### 2. Execução Local

```bash
# 1. Iniciar PostgreSQL e RabbitMQ
docker-compose up -d postgres rabbitmq

# 2. Compilar o projeto
mvn clean compile

# 3. Executar as migrations
mvn flyway:migrate

# 4. Executar a aplicação
mvn spring-boot:run
```

## 📊 Banco de Dados

### Estrutura das Tabelas

- `tb_usuario`: Usuários do sistema (médicos, enfermeiros, pacientes)
- `tb_role`: Papéis/roles dos usuários
- `tb_permissao`: Permissões do sistema
- `tb_role_permissao`: Relacionamento N:N entre roles e permissões
- `tb_usuario_telefone`: Telefones dos usuários
- `tb_especialidade`: Especialidades médicas
- `tb_especialidade_medico`: Relacionamento entre médicos e especialidades
- `tb_consulta`: Consultas agendadas

### Dados Iniciais

O sistema já vem com dados iniciais:
- **Roles**: ADMIN, MEDICO, ENFERMEIRO, PACIENTE
- **Permissões**: CRIAR_CONSULTA, EDITAR_CONSULTA, etc.
- **Especialidades**: Cardiologia, Dermatologia, etc.

## 🔌 API Endpoints

### Autenticação
- `POST /api/auth/login` - Login de usuário

### Usuários
- `POST /api/usuarios` - Criar usuário
- `GET /api/usuarios/{id}` - Buscar usuário por ID
- `GET /api/usuarios` - Listar todos os usuários
- `GET /api/usuarios/medicos` - Listar médicos
- `GET /api/usuarios/pacientes` - Listar pacientes
- `PUT /api/usuarios/{id}` - Atualizar usuário

### Consultas
- `POST /api/consultas` - Criar consulta
- `GET /api/consultas/{id}` - Buscar consulta por ID
- `GET /api/consultas` - Listar todas as consultas
- `GET /api/consultas/paciente/{pacienteId}` - Consultas por paciente
- `GET /api/consultas/medico/{medicoId}` - Consultas por médico
- `PUT /api/consultas/{id}` - Atualizar consulta

## 🔐 Autenticação

O sistema usa JWT para autenticação. Para acessar endpoints protegidos:

```bash
# 1. Fazer login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@medsync.com", "senha": "123456"}'

# 2. Usar o token retornado
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer <token>"
```

## 📨 Sistema de Eventos e Mensageria

### Arquitetura de Eventos
O sistema implementa uma arquitetura de eventos robusta seguindo os princípios SOLID:

#### Eventos Específicos (Records)
- **`ConsultaNotificacaoEvent`**: Evento simples para notificações (dados básicos)
- **`ConsultaHistoricoEvent`**: Evento detalhado para histórico (dados completos das entidades)

#### Fluxo de Eventos
```
CriarConsultaUseCase → PublicarEventoConsultaUseCase → EventPublisher
├── ConsultaNotificacaoEvent (dados básicos)
└── ConsultaHistoricoEvent (dados completos)
```

### RabbitMQ Configuration

#### Exchanges
- `ex_consultas`: Exchange principal para eventos de consultas

#### Filas
- `q_historico_consultas`: Para o serviço de histórico
- `q_notificacoes_consultas`: Para o serviço de notificações

#### Routing Keys
- `consulta.historico`: Para eventos de histórico
- `consulta.notificacao`: Para eventos de notificação

### EventPublisher
- **Responsabilidade única**: Publica eventos específicos
- **Logging detalhado**: Rastreabilidade completa
- **Tratamento de erros**: Fallback em caso de falha

## 🌐 Comunicação entre Serviços

### Feign Clients
O sistema usa OpenFeign para comunicação HTTP entre microsserviços:

#### HistoricoFeignClient
- **Interface**: Define contratos de comunicação
- **Fallback**: Implementa fallback em caso de falha
- **Configuração**: Timeouts e retry configuráveis

#### Configuração
```yaml
feign:
  client:
    config:
      default:
        connect-timeout: 5000
        read-timeout: 10000
        logger-level: basic
```

### Circuit Breaker (Resilience4j)
Implementa padrão Circuit Breaker para resiliência:

#### Configuração
```yaml
resilience4j:
  circuitbreaker:
    instances:
      historico-service:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
  retry:
    instances:
      historico-service:
        max-attempts: 3
        wait-duration: 1s
```

### Arquitetura em Camadas (SOLID)

#### Controller Layer
- **Responsabilidade**: Receber requisições HTTP
- **Dependência**: Apenas Services
- **Não conhece**: Use Cases ou Clients

#### Service Layer
- **Responsabilidade**: Orquestração e validações de negócio
- **Dependência**: Use Cases
- **Não conhece**: Clients ou Infrastructure

#### Use Case Layer
- **Responsabilidade**: Lógica de negócio específica
- **Dependência**: Gateways e Clients
- **Implementa**: Circuit Breaker e Retry

#### Client Layer
- **Responsabilidade**: Comunicação externa
- **Implementa**: Feign interfaces
- **Fallback**: Em caso de falha

## 🧪 Testes

O projeto possui uma suíte robusta de testes unitários para todos os casos de uso, seguindo as melhores práticas de TDD e Clean Architecture.

### Estrutura dos Testes

```
src/test/java/com/medsync/cadastroagendamento/
├── application/
│   ├── services/
│   │   └── HistoricoServiceTest.java
│   └── usecases/
│       ├── CriarUsuarioUseCaseTest.java
│       ├── BuscarUsuarioUseCaseTest.java
│       ├── AtualizarUsuarioUseCaseTest.java
│       ├── CriarConsultaUseCaseTest.java
│       ├── AtualizarConsultaUseCaseTest.java
│       ├── AutenticarUsuarioUseCaseTest.java
│       ├── PublicarEventoConsultaUseCaseTest.java
│       └── BuscarHistoricoPacienteUseCaseTest.java
├── infrastructure/
│   ├── events/
│   │   └── EventPublisherTest.java
│   ├── clients/
│   │   ├── HistoricoFeignClientTest.java
│   │   └── HistoricoFeignClientFallbackTest.java
│   └── config/
│       ├── FeignConfigTest.java
│       └── CircuitBreakerConfigTest.java
└── presentation/
    └── controllers/
        ├── UsuarioControllerTest.java
        ├── ConsultaControllerTest.java
        └── HistoricoControllerTest.java
```

### Cobertura de Testes

Os testes cobrem todos os cenários importantes:

#### Testes de Use Cases
- ✅ **Cenários de sucesso**: Operações que devem funcionar corretamente
- ✅ **Cenários de erro**: Validações e exceções de negócio
- ✅ **Casos extremos**: Dados nulos, vazios, inválidos
- ✅ **Integração**: Verificação de chamadas entre camadas
- ✅ **Mocks**: Isolamento de dependências externas

#### Testes de Mensageria
- ✅ **EventPublisher**: Publicação de eventos específicos
- ✅ **Eventos Records**: Validação de estrutura de dados
- ✅ **RabbitMQ**: Configuração e binding de filas
- ✅ **Fallback**: Comportamento em caso de falha

#### Testes de Clients
- ✅ **Feign Client**: Comunicação HTTP
- ✅ **Circuit Breaker**: Estados (closed, open, half-open)
- ✅ **Retry**: Tentativas de reconexão
- ✅ **Fallback**: Resposta em caso de falha
- ✅ **Timeouts**: Configuração de timeouts

#### Testes de Services
- ✅ **Orquestração**: Chamadas corretas aos use cases
- ✅ **Validações**: Regras de negócio
- ✅ **Logging**: Rastreabilidade

### Executando os Testes

```bash
# Executar todos os testes
mvn test

# Executar apenas testes de use cases
mvn test -Dtest="*UseCaseTest"

# Executar teste específico
mvn test -Dtest="CriarUsuarioUseCaseTest"

# Executar com relatório de cobertura
mvn test jacoco:report

# Executar testes em modo verbose
mvn test -X
```

### Configuração de Teste

Os testes usam:
- **JUnit 5**: Framework de testes
- **Mockito**: Para mocks e stubs
- **AssertJ**: Para assertions fluentes
- **H2 Database**: Banco em memória para testes
- **Spring Test**: Contexto de teste do Spring

### Exemplo de Teste

```java
@Test
@DisplayName("Deve criar usuário com sucesso quando dados são válidos")
void deveCriarUsuarioComSucesso() {
    // Given
    when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
    when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);
    
    // When
    Usuario resultado = criarUsuarioUseCase.executar(request);
    
    // Then
    assertThat(resultado).isNotNull();
    assertThat(resultado.getNome()).isEqualTo("João Silva");
    verify(usuarioGateway).salvar(any(Usuario.class));
}
```

## 📝 Logs

Os logs são configurados para diferentes níveis:
- **DEBUG**: Para desenvolvimento
- **INFO**: Para produção
- **ERROR**: Para erros críticos

## 🔧 Configurações

### Variáveis de Ambiente

```bash
# Banco de dados
DB_USERNAME=medsync_user
DB_PASSWORD=medsync_pass
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/medsync_db

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000
```

## 🐛 Troubleshooting

### Problemas Comuns

1. **Erro de conexão com PostgreSQL**
   - Verificar se o PostgreSQL está rodando
   - Verificar credenciais no `application.yml`

2. **Erro de conexão com RabbitMQ**
   - Verificar se o RabbitMQ está rodando
   - Acessar http://localhost:15672 para gerenciamento

3. **Erro de migrations**
   - Verificar se o banco existe
   - Executar `mvn flyway:clean flyway:migrate`

## 📚 Documentação Adicional

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [JWT Documentation](https://jwt.io/)

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
