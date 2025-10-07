# MedSync - Postman Collections

Este diretório contém as coleções do Postman para o sistema MedSync, incluindo testes integrados, de carga e de contrato da API.

## 📁 Arquivos Disponíveis

### 1. **MedSync_Environment.postman_environment.json**
Environment do Postman com todas as variáveis necessárias para os testes.

**Variáveis principais:**
- `base_url`: URL base da API de agendamento (http://localhost:8080)
- `historico_url`: URL do serviço de histórico (http://localhost:8081)
- `notificacao_url`: URL do serviço de notificação (http://localhost:8082)
- `jwt_token`: Token JWT para autenticação
- `m2m_token`: Token M2M para comunicação entre serviços
- IDs de usuários, pacientes, médicos, consultas e especialidades
- Credenciais de teste para diferentes tipos de usuários

### 2. **MedSync_Agendamento_Complete_Collection.postman_collection.json**
Coleção completa da API de agendamento com todos os endpoints organizados por funcionalidade.

**Seções:**
- 🔐 **Autenticação**: Login e gerenciamento de tokens
- 👥 **Usuários**: CRUD completo de usuários
- 🏥 **Especialidades**: Listagem de especialidades médicas
- 👤 **Pacientes**: Gerenciamento via serviço de histórico
- 📋 **Consultas**: CRUD de consultas médicas
- 📊 **Histórico Médico**: Acesso a dados históricos
- 🔧 **Utilitários**: Health check e documentação

### 3. **MedSync_Integration_Tests_Collection.postman_collection.json**
Coleção robusta de testes integrados end-to-end.

**Fluxos testados:**
- Setup e autenticação
- Criação e gerenciamento de usuários
- Criação e gerenciamento de pacientes
- Criação e gerenciamento de consultas
- Acesso ao histórico médico
- Testes de segurança e permissões
- Testes de performance
- Cleanup e relatórios

**Características:**
- ✅ 19 testes integrados sequenciais
- 🔄 Extração automática de IDs entre requests
- 📊 Relatórios detalhados de performance
- 🧹 Limpeza automática de dados de teste
- 🔐 Testes de segurança e permissões

### 4. **MedSync_Load_Tests_Collection.postman_collection.json**
Coleção de testes de carga e stress para o sistema.

**Testes incluídos:**
- Teste de carga para listagem de usuários
- Teste de carga para listagem de especialidades
- Teste de carga para health check
- Geração de relatórios de performance

**Métricas coletadas:**
- Tempo de resposta por endpoint
- Taxa de sucesso
- Análise de throttling
- Estatísticas por endpoint
- Relatórios de performance

### 5. **MedSync_API_Contract_Tests_Collection.postman_collection.json**
Coleção de testes de contrato da API para validar estruturas de requests/responses.

**Testes de contrato:**
- Estrutura de requests de autenticação
- Estrutura de responses de usuários
- Validação de UUIDs e formatos
- Estrutura de respostas de especialidades
- Contratos de pacientes e consultas
- Estrutura de tokens M2M
- Validação de respostas de erro

## 🚀 Como Usar

### 1. Importar no Postman
1. Abra o Postman
2. Clique em "Import"
3. Selecione todos os arquivos `.json`
4. Configure o environment "MedSync - Environment"

### 2. Configurar Environment
1. Selecione o environment "MedSync - Environment"
2. Ajuste as URLs conforme necessário:
   - `base_url`: URL da API de agendamento
   - `historico_url`: URL do serviço de histórico
   - `notificacao_url`: URL do serviço de notificação

### 3. Executar Testes

#### Testes Integrados (Recomendado)
1. Execute a collection "MedSync - Integration Tests"
2. Os testes rodam em sequência e criam dados de teste
3. Verifique os logs no console do Postman
4. Analise o relatório final

#### Testes de Carga
1. Execute a collection "MedSync - Load Tests"
2. Configure para rodar múltiplas iterações
3. Monitore as métricas de performance
4. Analise o relatório final de carga

#### Testes de Contrato
1. Execute a collection "MedSync - API Contract Tests"
2. Verifique se todas as estruturas estão corretas
3. Analise os logs de validação

### 4. Monitoramento
- **Console do Postman**: Logs detalhados de cada teste
- **Test Results**: Resultados dos testes automatizados
- **Collection Runner**: Para execução em lote
- **Newman**: Para execução via linha de comando

## 📊 Métricas e Relatórios

### Testes Integrados
- ✅ Taxa de sucesso dos fluxos end-to-end
- ⏱️ Tempo de resposta por operação
- 🔄 Validação de integração entre serviços
- 🧹 Limpeza automática de dados

### Testes de Carga
- 📈 Performance sob carga
- 🚨 Detecção de throttling
- 📊 Estatísticas por endpoint
- ⚡ Análise de tempo de resposta

### Testes de Contrato
- 📋 Validação de estruturas de dados
- 🔍 Verificação de tipos e formatos
- ✅ Conformidade com contratos da API
- 🛡️ Validação de respostas de erro

## 🔧 Configurações Avançadas

### Variables de Ambiente
```json
{
  "base_url": "http://localhost:8080",
  "jwt_token": "{{$randomJWT}}",
  "usuario_id": "{{$randomUUID}}"
}
```

### Scripts de Teste
```javascript
// Exemplo de teste personalizado
pm.test('Custom validation', function () {
    const response = pm.response.json();
    pm.expect(response.data).to.exist;
});
```

### Execução via Newman
```bash
# Instalar Newman
npm install -g newman

# Executar testes integrados
newman run MedSync_Integration_Tests_Collection.postman_collection.json \
  -e MedSync_Environment.postman_environment.json \
  --reporters cli,json \
  --reporter-json-export results.json

# Executar testes de carga
newman run MedSync_Load_Tests_Collection.postman_collection.json \
  -e MedSync_Environment.postman_environment.json \
  --iteration-count 100 \
  --reporters cli,json
```

## 🐛 Troubleshooting

### Problemas Comuns

1. **Erro 401 - Unauthorized**
   - Verifique se o token JWT está configurado
   - Execute o login antes dos outros testes

2. **Erro 404 - Not Found**
   - Verifique se as URLs estão corretas
   - Confirme se os serviços estão rodando

3. **Erro 500 - Internal Server Error**
   - Verifique os logs da aplicação
   - Confirme se o banco de dados está acessível

4. **Timeout nos testes**
   - Aumente o timeout no Postman
   - Verifique a performance da aplicação

### Logs Importantes
- **Console do Postman**: Logs detalhados de cada request
- **Application Logs**: Logs da aplicação Spring Boot
- **Database Logs**: Logs do PostgreSQL/MongoDB
- **RabbitMQ Logs**: Logs do message broker

## 📈 Próximos Passos

1. **CI/CD Integration**: Integrar com pipelines de CI/CD
2. **Monitoring**: Adicionar métricas de monitoramento
3. **Alerting**: Configurar alertas para falhas
4. **Performance**: Otimizar baseado nos resultados dos testes
5. **Security**: Adicionar testes de segurança específicos

## 🤝 Contribuição

Para adicionar novos testes:
1. Crie uma nova collection ou adicione à existente
2. Siga os padrões de nomenclatura
3. Adicione documentação adequada
4. Teste localmente antes de commitar
5. Atualize este README se necessário

