# 🏗️ Arquitetura do Sistema - SUS Telemedicina

## Visão Geral da Arquitetura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         SISTEMA SUS TELEMEDICINA                        │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                          APIS EXTERNAS                                  │
├──────────────────────┬─────────────────────┬──────────────────────────┤
│   🎥 Zoom API        │  📱 Twilio WhatsApp  │  🏥 Mock SUS API         │
│   - OAuth 2.0        │  - SMS/WhatsApp      │  - Pacientes             │
│   - Meetings         │  - Messaging API     │  - Profissionais         │
└──────────────────────┴─────────────────────┴──────────────────────────┘
           ▲                      ▲                      ▲
           │                      │                      │
           └──────────────────────┴──────────────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │   Infrastructure Layer     │
                    │   - ZoomClient            │
                    │   - WhatsAppClient        │
                    │   - MockSusApiClient      │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │      Domain Layer          │
                    │   - TelemedicinaService   │
                    │   - Business Logic        │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │   Application Layer        │
                    │   - Controllers           │
                    │   - DTOs                  │
                    │   - Exception Handlers    │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │      REST API             │
                    │   Port: 8081              │
                    └───────────────────────────┘
                                  ▲
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
          ┌─────────▼─────────┐     ┌──────────▼──────────┐
          │   Scheduler        │     │   HTTP Clients      │
          │   (Auto 5min)      │     │   (Manual calls)    │
          └────────────────────┘     └─────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                           PERSISTÊNCIA                                  │
├──────────────────────┬──────────────────────┬──────────────────────────┤
│  📊 PostgreSQL       │  📁 Repositories     │  🔄 Flyway Migrations    │
│  - agendamentos      │  - JPA/Hibernate     │  - V1__create_tables     │
│  - consultas_tele... │  - CRUD operations   │  - Version control       │
└──────────────────────┴──────────────────────┴──────────────────────────┘
```

---

## Fluxo de Dados Detalhado

### 1. Criação de Consulta

```
[Cliente HTTP]
     │
     │ POST /api/telemedicina/consultas
     │ { "agendamentoId": 1 }
     ▼
[TelemedicinaController]
     │
     │ criarConsulta()
     ▼
[TelemedicinaService]
     │
     ├─► [AgendamentoRepository] ──► [PostgreSQL]
     │   └─ Buscar agendamento
     │
     ├─► [ZoomClient]
     │   ├─ getAccessToken() ──► [Zoom OAuth]
     │   └─ createMeeting() ──► [Zoom API]
     │       └─ Retorna: meeting_id, join_url
     │
     └─► [ConsultaTelemedicinaRepository] ──► [PostgreSQL]
         └─ Salvar consulta
     
     │
     ▼
[Response JSON]
{
  "id": 1,
  "zoomJoinUrl": "https://zoom.us/j/...",
  ...
}
```

### 2. Envio de Notificação

```
[Scheduler] OU [Cliente HTTP]
     │
     │ Trigger: 15 min antes
     ▼
[TelemedicinaService]
     │
     ├─► [ConsultaTelemedicinaRepository]
     │   └─ Buscar consulta
     │
     ├─► [AgendamentoRepository]
     │   └─ Buscar agendamento
     │
     ├─► [MockSusApiClient]
     │   └─ getPaciente() ──► [Mock API]
     │       └─ Retorna: nome, telefone
     │
     ├─► [WhatsAppClient]
     │   └─ sendMessage() ──► [Twilio API]
     │       ├─ From: whatsapp:+14155238886
     │       ├─ To: whatsapp:+5511999999999
     │       └─ Body: Mensagem + Link Zoom
     │
     └─► [ConsultaTelemedicinaRepository]
         └─ Atualizar: notificacao_enviada = true
```

### 3. Processamento Automático (Scheduler)

```
[TelemedicinaScheduler]
     │
     │ @Scheduled (a cada 5 min)
     ▼
[TelemedicinaService.processarAgendamentos()]
     │
     ├─► [AgendamentoRepository]
     │   └─ findAgendamentosParaTelemedicina()
     │       Filtros:
     │       - tipo = TELEMEDICINA
     │       - status IN (AGENDADO, CONFIRMADO)
     │       - data BETWEEN now+10min AND now+20min
     │
     └─► Para cada agendamento:
         │
         ├─► [ConsultaTelemedicinaRepository]
         │   └─ Verificar se consulta existe
         │
         ├─► Se NÃO existe:
         │   └─► criarConsultaTelemedicina()
         │
         └─► Se notificação NÃO enviada:
             └─► enviarNotificacaoWhatsApp()
```

---

## Arquitetura em Camadas

### Camada de Aplicação (Application Layer)

```
application/
├── controller/
│   └── TelemedicinaController.java
│       - Endpoints REST
│       - Validação de entrada
│       - Mapeamento de DTOs
│
├── dto/
│   ├── CriarConsultaRequest.java
│   └── ConsultaTelemedicinaResponse.java
│
└── exception/
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java
        - Tratamento centralizado de erros
        - Respostas padronizadas
```

**Responsabilidades:**
- Receber requisições HTTP
- Validar dados de entrada
- Transformar entre DTOs e entidades de domínio
- Retornar respostas HTTP

---

### Camada de Domínio (Domain Layer)

```
domain/
├── service/
│   └── TelemedicinaService.java
│       - Lógica de negócio principal
│       - Orquestração de operações
│       - Regras de negócio
│
└── enums/
    ├── StatusAgendamento.java
    └── TipoAtendimento.java
```

**Responsabilidades:**
- Implementar regras de negócio
- Orquestrar chamadas a serviços externos
- Garantir integridade dos dados
- Implementar casos de uso

---

### Camada de Infraestrutura (Infrastructure Layer)

```
infrastructure/
├── client/
│   ├── ZoomClient.java
│   │   - Autenticação OAuth
│   │   - Criação de reuniões
│   │   - Cache de tokens
│   │
│   ├── WhatsAppClient.java
│   │   - Envio de mensagens
│   │   - Formatação de mensagens
│   │
│   ├── MockSusApiClient.java
│   │   - Busca dados de pacientes
│   │
│   └── dto/
│       └── [DTOs de APIs externas]
│
├── persistence/
│   ├── entity/
│   │   ├── AgendamentoJpaEntity.java
│   │   └── ConsultaTelemedicinaJpaEntity.java
│   │
│   └── repository/
│       ├── AgendamentoRepository.java
│       └── ConsultaTelemedicinaRepository.java
│
├── scheduler/
│   └── TelemedicinaScheduler.java
│       - Execução periódica
│       - Processamento automático
│
└── config/
    └── JacksonConfig.java
        - Configuração de serialização JSON
```

**Responsabilidades:**
- Integração com APIs externas (Zoom, Twilio)
- Persistência de dados (PostgreSQL)
- Configurações técnicas
- Tarefas agendadas

---

## Integrações Externas

### 1. Zoom API

```
┌──────────────────────────────────────────┐
│           Zoom Integration                │
├──────────────────────────────────────────┤
│                                          │
│  1. Authentication (OAuth 2.0)           │
│     POST /oauth/token                    │
│     ├─ grant_type: account_credentials   │
│     ├─ account_id                        │
│     └─ Returns: access_token             │
│                                          │
│  2. Create Meeting                       │
│     POST /v2/users/me/meetings           │
│     ├─ Authorization: Bearer {token}     │
│     ├─ Body: meeting details             │
│     └─ Returns: meeting_id, join_url     │
│                                          │
│  3. Token Cache                          │
│     └─ Cache token até expiração (-5min) │
│                                          │
└──────────────────────────────────────────┘
```

**Endpoints Utilizados:**
- `POST https://zoom.us/oauth/token` - Autenticação
- `POST https://api.zoom.us/v2/users/me/meetings` - Criar reunião

**Credenciais Necessárias:**
- Account ID
- Client ID
- Client Secret

---

### 2. Twilio WhatsApp

```
┌──────────────────────────────────────────┐
│        Twilio WhatsApp Integration        │
├──────────────────────────────────────────┤
│                                          │
│  1. Authentication (Basic Auth)          │
│     Base64(account_sid:auth_token)       │
│                                          │
│  2. Send Message                         │
│     POST /2010-04-01/Accounts/{sid}/     │
│          Messages.json                   │
│     ├─ From: whatsapp:+14155238886       │
│     ├─ To: whatsapp:+55...               │
│     ├─ Body: message text                │
│     └─ Returns: message_sid, status      │
│                                          │
│  3. Message Format                       │
│     └─ Template personalizado com:       │
│        - Nome do paciente                │
│        - Dados da consulta               │
│        - Link do Zoom                    │
│        - Instruções                      │
│                                          │
└──────────────────────────────────────────┘
```

**Endpoints Utilizados:**
- `POST https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json`

**Credenciais Necessárias:**
- Account SID
- Auth Token
- From Number (WhatsApp)

---

### 3. Mock SUS API

```
┌──────────────────────────────────────────┐
│          Mock SUS API Integration         │
├──────────────────────────────────────────┤
│                                          │
│  1. Get Patient Data                     │
│     GET /pacientes/{id}                  │
│     └─ Returns: patient details          │
│                                          │
│  2. Data Retrieved                       │
│     ├─ nome                              │
│     ├─ telefone                          │
│     ├─ cpf                               │
│     └─ outros dados                      │
│                                          │
└──────────────────────────────────────────┘
```

**Endpoints Utilizados:**
- `GET http://localhost:3000/pacientes/{id}`

---

## Banco de Dados

### Modelo de Dados

```
┌─────────────────────────────────────────────────┐
│                 agendamentos                    │
├─────────────────────────────────────────────────┤
│ id (PK)                        BIGSERIAL        │
│ paciente_id                    BIGINT           │
│ profissional_id                BIGINT           │
│ codigo_cnes_unidade            VARCHAR(20)      │
│ especialidade_id               BIGINT           │
│ data_hora_agendamento          TIMESTAMP        │
│ status                         VARCHAR(50)      │
│ tipo_atendimento               VARCHAR(50)      │◄────┐
│ observacoes                    VARCHAR(500)     │     │
│ data_criacao                   TIMESTAMP        │     │
│ data_atualizacao               TIMESTAMP        │     │
│ motivo_cancelamento            VARCHAR(500)     │     │
└─────────────────────────────────────────────────┘     │
                                                        │
                                                        │ FK
                                                        │ (1:1)
                                                        │
┌─────────────────────────────────────────────────┐     │
│          consultas_telemedicina                 │     │
├─────────────────────────────────────────────────┤     │
│ id (PK)                        BIGSERIAL        │     │
│ agendamento_id (FK, UNIQUE)    BIGINT           │─────┘
│ zoom_meeting_id                VARCHAR(255)     │
│ zoom_join_url                  VARCHAR(500)     │
│ zoom_start_url                 VARCHAR(500)     │
│ notificacao_enviada            BOOLEAN          │
│ data_envio_notificacao         TIMESTAMP        │
│ data_criacao                   TIMESTAMP        │
│ data_atualizacao               TIMESTAMP        │
└─────────────────────────────────────────────────┘

Índices:
- idx_consultas_agendamento_id (agendamento_id)
- idx_consultas_notificacao (notificacao_enviada, data_envio_notificacao)
```

---

## Deployment

### Containerização (Docker)

```
┌─────────────────────────────────────────────────┐
│              Docker Compose                      │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌─────────────────┐  ┌────────────────────┐   │
│  │   PostgreSQL    │  │   Mock SUS API     │   │
│  │   Port: 5432    │  │   Port: 3000       │   │
│  └────────┬────────┘  └────────┬───────────┘   │
│           │                    │               │
│           │         ┌──────────▼──────────┐    │
│           │         │  SUS Agendamento    │    │
│           │         │  Port: 8080         │    │
│           │         └─────────────────────┘    │
│           │                                     │
│           └──────────┬──────────────────────┐   │
│                      │                      │   │
│              ┌───────▼─────────┐            │   │
│              │ SUS Telemedicina│            │   │
│              │  Port: 8081     │◄───────────┘   │
│              └─────────────────┘                │
│                      │                          │
│                      ▼                          │
│              ┌─────────────────┐                │
│              │  External APIs  │                │
│              │  - Zoom         │                │
│              │  - Twilio       │                │
│              └─────────────────┘                │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## Segurança

### Práticas Implementadas

1. **Credenciais Externas**: Variáveis de ambiente
2. **Tokens**: Cache com renovação automática
3. **Dados Sensíveis**: Não expor em logs
4. **Validação**: Input validation com Bean Validation
5. **Exception Handling**: Tratamento centralizado
6. **Database**: Prepared statements (JPA)

---

## Escalabilidade

### Pontos de Melhoria Futura

```
┌─────────────────────────────────────────────────┐
│          Possíveis Melhorias                     │
├─────────────────────────────────────────────────┤
│                                                 │
│  1. Cache Distribuído                           │
│     └─ Redis para tokens e sessões             │
│                                                 │
│  2. Fila de Mensagens                           │
│     └─ RabbitMQ/Kafka para processamento async │
│                                                 │
│  3. Load Balancer                               │
│     └─ Nginx para múltiplas instâncias          │
│                                                 │
│  4. Monitoramento                               │
│     └─ Prometheus + Grafana                     │
│                                                 │
│  5. API Gateway                                 │
│     └─ Rate limiting e autenticação             │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

Para mais detalhes, consulte:
- `README.md` - Documentação geral
- `DATABASE_SCHEMA.md` - Detalhes do banco
- `API_DOCUMENTATION.md` - API REST

