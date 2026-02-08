# SUS Telemedicina

Sistema de Telemedicina e Atendimento Remoto do SUS - Plataforma que possibilita a realização de consultas ou orientações à distância, aumentando o alcance do SUS e diminuindo a sobrecarga das unidades de saúde.

## 🎯 Funcionalidades

- ✅ Integração com Zoom para criar reuniões virtuais
- ✅ Envio automático de notificações via WhatsApp (Twilio)
- ✅ Busca automática de agendamentos de telemedicina no PostgreSQL
- ✅ Scheduler automático para enviar notificações antes das consultas
- ✅ API REST para gerenciar consultas de telemedicina

## 🚀 Tecnologias

- Java 21
- Spring Boot 3.2.2
- PostgreSQL
- Zoom API
- Twilio WhatsApp API
- Maven

## 📋 Pré-requisitos

- Java 21
- PostgreSQL (via Docker Compose)
- Conta Zoom com credenciais API
- Conta Twilio com WhatsApp habilitado

## ⚙️ Configuração

### 1. Variáveis de Ambiente

Configure as seguintes variáveis de ambiente:

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/susdb
DATABASE_USERNAME=sus
DATABASE_PASSWORD=sus123

# Zoom API
ZOOM_ACCOUNT_ID=your-account-id
ZOOM_CLIENT_ID=your-client-id
ZOOM_CLIENT_SECRET=your-client-secret

# Twilio WhatsApp
TWILIO_ACCOUNT_SID=your-account-sid
TWILIO_AUTH_TOKEN=your-auth-token
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886

# Scheduler
SCHEDULER_ENABLED=true
SCHEDULER_CHECK_INTERVAL=5
NOTIFICATION_ADVANCE_MINUTES=15

# Mock SUS API
MOCK_SUS_API_URL=http://localhost:3000
```

### 2. Obter Credenciais do Zoom

1. Acesse [Zoom Marketplace](https://marketplace.zoom.us/)
2. Crie uma aplicação Server-to-Server OAuth
3. Copie as credenciais: Account ID, Client ID e Client Secret

### 3. Obter Credenciais do Twilio

1. Crie uma conta em [Twilio](https://www.twilio.com/)
2. Habilite o WhatsApp Sandbox ou configure um número próprio
3. Copie as credenciais: Account SID e Auth Token

## 🏃 Como Executar

### Via Docker Compose (Recomendado)

```bash
# Na raiz do projeto
docker-compose up -d
```

### Via Maven

```bash
cd sus-telemedicina
./mvnw spring-boot:run
```

## 📡 API Endpoints

### Criar Consulta de Telemedicina

```http
POST /api/telemedicina/consultas
Content-Type: application/json

{
  "agendamentoId": 1
}
```

**Resposta:**
```json
{
  "id": 1,
  "agendamentoId": 1,
  "zoomMeetingId": "123456789",
  "zoomJoinUrl": "https://zoom.us/j/123456789",
  "notificacaoEnviada": false,
  "dataEnvioNotificacao": null,
  "dataCriacao": "08/02/2026 10:30:00"
}
```

### Enviar Notificação WhatsApp

```http
POST /api/telemedicina/consultas/{id}/notificar
```

### Processar Agendamentos Manualmente

```http
POST /api/telemedicina/processar?antecedenciaMinutos=15
```

### Health Check

```http
GET /api/telemedicina/health
```

## 🔄 Funcionamento

1. **Scheduler Automático**: A cada 5 minutos, o sistema verifica agendamentos de telemedicina
2. **Criação de Reunião**: Para cada agendamento, uma reunião Zoom é criada automaticamente
3. **Notificação WhatsApp**: 15 minutos antes da consulta, o paciente recebe um WhatsApp com o link da reunião
4. **Link Personalizado**: Cada consulta possui um link único do Zoom

## 📊 Estrutura do Banco de Dados

### Tabela: consultas_telemedicina

```sql
CREATE TABLE consultas_telemedicina (
    id BIGSERIAL PRIMARY KEY,
    agendamento_id BIGINT NOT NULL UNIQUE,
    zoom_meeting_id VARCHAR(255) NOT NULL,
    zoom_join_url VARCHAR(500) NOT NULL,
    zoom_start_url VARCHAR(500),
    notificacao_enviada BOOLEAN NOT NULL DEFAULT FALSE,
    data_envio_notificacao TIMESTAMP,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP
);
```

## 🔧 Configurações Avançadas

### Intervalo do Scheduler

Altere a frequência de verificação dos agendamentos:

```yaml
scheduler:
  check-interval-minutes: 5  # Em minutos
```

### Antecedência da Notificação

Configure quando as notificações devem ser enviadas:

```yaml
scheduler:
  notification-advance-minutes: 15  # Minutos antes da consulta
```

### Desabilitar Scheduler

Para desabilitar o scheduler automático:

```yaml
scheduler:
  enabled: false
```

## 📝 Logs

O sistema registra todas as operações importantes:

- Criação de reuniões Zoom
- Envio de notificações WhatsApp
- Processamento de agendamentos
- Erros e exceções

## 🧪 Testes

```bash
./mvnw test
```

## 🐛 Troubleshooting

### Erro de autenticação Zoom

Verifique se as credenciais estão corretas e se a aplicação tem as permissões necessárias.

### Notificações WhatsApp não enviadas

1. Verifique se o número do paciente está no formato internacional (+55...)
2. Confirme se o WhatsApp Sandbox está configurado corretamente
3. Verifique os logs para mensagens de erro específicas

### Agendamentos não sendo processados

1. Verifique se o scheduler está habilitado
2. Confirme se há agendamentos de telemedicina no banco
3. Verifique se o horário dos agendamentos está dentro da janela de notificação

## 📄 Licença

Este projeto é parte do Sistema Único de Saúde (SUS) e está licenciado para uso público.

## 👥 Contribuidores

Desenvolvido para o Hackathon FIAP - Sistema SUS

## 📞 Suporte

Para dúvidas ou problemas, entre em contato com a equipe de desenvolvimento.

