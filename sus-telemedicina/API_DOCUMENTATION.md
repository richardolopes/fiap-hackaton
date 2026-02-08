# API de Telemedicina - Documentação

## Descrição

API REST para gerenciar consultas de telemedicina, com integração ao Zoom para criação de reuniões virtuais e envio de notificações via WhatsApp.

## Base URL

```
http://localhost:8081/api/telemedicina
```

## Endpoints

### 1. Health Check

Verifica se o serviço está ativo.

```http
GET /health
```

**Resposta de Sucesso (200 OK):**
```
SUS Telemedicina Service is running
```

---

### 2. Criar Consulta de Telemedicina

Cria uma nova consulta de telemedicina com reunião Zoom para um agendamento existente.

```http
POST /consultas
Content-Type: application/json

{
  "agendamentoId": 1
}
```

**Parâmetros do Body:**

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| agendamentoId | Long | Sim | ID do agendamento de telemedicina |

**Resposta de Sucesso (201 Created):**
```json
{
  "id": 1,
  "agendamentoId": 1,
  "zoomMeetingId": "123456789",
  "zoomJoinUrl": "https://zoom.us/j/123456789?pwd=xxxxx",
  "notificacaoEnviada": false,
  "dataEnvioNotificacao": null,
  "dataCriacao": "08/02/2026 10:30:00"
}
```

**Resposta de Erro (500):**
```json
{
  "timestamp": "2026-02-08T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro ao criar consulta de telemedicina: ...",
  "path": "/api/telemedicina/consultas"
}
```

**Regras de Negócio:**
- O agendamento deve existir no banco de dados
- O agendamento deve ser do tipo `TELEMEDICINA`
- Não é possível criar consulta duplicada para o mesmo agendamento

---

### 3. Enviar Notificação WhatsApp

Envia notificação via WhatsApp para o paciente com o link da reunião Zoom.

```http
POST /consultas/{id}/notificar
```

**Parâmetros de Path:**

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| id | Long | Sim | ID da consulta de telemedicina |

**Resposta de Sucesso (200 OK):**
```
(Sem corpo de resposta)
```

**Resposta de Erro (500):**
```json
{
  "timestamp": "2026-02-08T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro ao enviar notificação: ...",
  "path": "/api/telemedicina/consultas/1/notificar"
}
```

**Formato da Mensagem WhatsApp:**
```
🏥 *Lembrete de Consulta - SUS Telemedicina*

Olá [Nome do Paciente],

Sua consulta por telemedicina está próxima!

📋 *Detalhes da Consulta:*
👨‍⚕️ Profissional: [Nome do Profissional]
🕐 Horário: [DD/MM/YYYY às HH:MM]

🔗 *Link da Reunião:*
[Link do Zoom]

⚠️ *Instruções:*
1. Clique no link alguns minutos antes do horário
2. Certifique-se de ter uma boa conexão de internet
3. Tenha seus documentos e exames em mãos

Em caso de dúvidas, entre em contato com a unidade de saúde.

Atenciosamente,
Sistema SUS Telemedicina
```

**Regras de Negócio:**
- A consulta deve existir no banco de dados
- O paciente deve ter um telefone cadastrado
- Notificação só é enviada uma vez (flag `notificacaoEnviada`)

---

### 4. Processar Agendamentos

Processa manualmente os agendamentos que estão próximos, criando consultas e enviando notificações.

```http
POST /processar?antecedenciaMinutos=15
```

**Parâmetros de Query:**

| Campo | Tipo | Obrigatório | Default | Descrição |
|-------|------|-------------|---------|-----------|
| antecedenciaMinutos | Integer | Não | 15 | Minutos de antecedência para buscar agendamentos |

**Resposta de Sucesso (200 OK):**
```
(Sem corpo de resposta)
```

**Resposta de Erro (500):**
```json
{
  "timestamp": "2026-02-08T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro ao processar agendamentos: ...",
  "path": "/api/telemedicina/processar"
}
```

**Regras de Negócio:**
- Busca agendamentos de telemedicina com status `AGENDADO` ou `CONFIRMADO`
- Considera apenas agendamentos dentro da janela de tempo especificada
- Para cada agendamento:
  1. Cria a consulta se não existir
  2. Envia a notificação se ainda não foi enviada

---

## Scheduler Automático

O sistema possui um scheduler que executa automaticamente a cada 5 minutos (configurável).

**Configuração:**
```yaml
scheduler:
  enabled: true
  check-interval-minutes: 5
  notification-advance-minutes: 15
```

**Funcionamento:**
1. A cada 5 minutos, o scheduler é executado
2. Busca agendamentos de telemedicina que ocorrerão em 15 minutos (±5 min)
3. Cria consultas Zoom para agendamentos sem consulta
4. Envia notificações WhatsApp para consultas sem notificação enviada

---

## Exemplos de Uso

### Criar Consulta com cURL

```bash
curl -X POST http://localhost:8081/api/telemedicina/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "agendamentoId": 1
  }'
```

### Enviar Notificação com cURL

```bash
curl -X POST http://localhost:8081/api/telemedicina/consultas/1/notificar
```

### Processar Agendamentos com cURL

```bash
curl -X POST "http://localhost:8081/api/telemedicina/processar?antecedenciaMinutos=15"
```

### Health Check com cURL

```bash
curl http://localhost:8081/api/telemedicina/health
```

---

## Códigos de Status HTTP

| Código | Descrição |
|--------|-----------|
| 200 | OK - Requisição bem-sucedida |
| 201 | Created - Recurso criado com sucesso |
| 400 | Bad Request - Dados inválidos |
| 500 | Internal Server Error - Erro no servidor |

---

## Observações

1. **Configuração do Zoom**: É necessário configurar as credenciais do Zoom nas variáveis de ambiente
2. **Configuração do Twilio**: É necessário configurar as credenciais do Twilio nas variáveis de ambiente
3. **Formato do Telefone**: O telefone deve estar no formato internacional (+55...)
4. **Persistência**: Todas as consultas são salvas no banco de dados PostgreSQL
5. **Idempotência**: Não é possível criar consultas duplicadas para o mesmo agendamento

---

## Fluxo Completo

1. **Agendamento Criado**: Sistema de agendamento cria um agendamento do tipo `TELEMEDICINA`
2. **Scheduler Verifica**: A cada 5 minutos, o scheduler busca agendamentos próximos
3. **Consulta Criada**: Sistema cria automaticamente uma reunião no Zoom
4. **Notificação Enviada**: 15 minutos antes, paciente recebe WhatsApp com o link
5. **Consulta Realizada**: Paciente e profissional acessam o link do Zoom

---

## Monitoramento

Logs importantes para monitorar:
- Criação de reuniões Zoom
- Envio de notificações WhatsApp
- Execução do scheduler
- Erros de integração com APIs externas

Verifique os logs com:
```bash
docker logs sus-telemedicina -f
```

