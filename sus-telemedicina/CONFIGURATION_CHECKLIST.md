# ✅ Checklist de Configuração - SUS Telemedicina

Use este checklist para garantir que tudo está configurado corretamente.

---

## 📋 Pré-requisitos

- [ ] Docker instalado (versão 20.x ou superior)
- [ ] Docker Compose instalado (versão 2.x ou superior)
- [ ] Git instalado
- [ ] Editor de texto (VS Code, IntelliJ, etc.)

---

## 🔐 1. Credenciais do Zoom

### Obter Credenciais:

1. [ ] Acessar https://marketplace.zoom.us/
2. [ ] Fazer login com sua conta Zoom
3. [ ] Clicar em "Develop" > "Build App"
4. [ ] Selecionar "Server-to-Server OAuth"
5. [ ] Criar nova aplicação:
   - [ ] Nome: "SUS Telemedicina"
   - [ ] Company Name: Seu nome/organização
   - [ ] Aceitar os termos
6. [ ] Copiar as credenciais:
   - [ ] Account ID: `_________________________`
   - [ ] Client ID: `_________________________`
   - [ ] Client Secret: `_________________________`
7. [ ] Configurar Scopes (Permissões):
   - [ ] `meeting:write:admin`
   - [ ] `meeting:read:admin`
8. [ ] Ativar a aplicação

### Testar Credenciais:

```bash
# Teste rápido (substitua as credenciais)
curl -X POST https://zoom.us/oauth/token \
  -H "Authorization: Basic $(echo -n 'CLIENT_ID:CLIENT_SECRET' | base64)" \
  -d "grant_type=account_credentials&account_id=ACCOUNT_ID"

# Se retornar access_token, está correto!
```

- [ ] Credenciais testadas e funcionando

---

## 📱 2. Credenciais do Twilio WhatsApp

### Obter Credenciais:

1. [ ] Acessar https://www.twilio.com/
2. [ ] Criar conta (trial gratuita disponível)
3. [ ] Verificar seu telefone pessoal
4. [ ] Acessar o Console: https://console.twilio.com/
5. [ ] Copiar as credenciais:
   - [ ] Account SID: `_________________________`
   - [ ] Auth Token: `_________________________`

### Configurar WhatsApp Sandbox (Para Testes):

6. [ ] Acessar: https://console.twilio.com/us1/develop/sms/try-it-out/whatsapp-learn
7. [ ] Seguir instruções para conectar seu WhatsApp:
   - [ ] Enviar mensagem para o número do Twilio
   - [ ] Código de ativação: "join [código]"
   - [ ] Aguardar confirmação
8. [ ] Copiar o número do Twilio:
   - [ ] From Number: `whatsapp:+14155238886` (exemplo)

### Testar Twilio:

```bash
# Teste rápido (substitua as credenciais)
curl -X POST https://api.twilio.com/2010-04-01/Accounts/ACCOUNT_SID/Messages.json \
  -u "ACCOUNT_SID:AUTH_TOKEN" \
  -d "From=whatsapp:+14155238886" \
  -d "To=whatsapp:+5511999999999" \
  -d "Body=Teste SUS Telemedicina"

# Se retornar status "queued" ou "sent", está correto!
```

- [ ] Credenciais testadas e funcionando
- [ ] WhatsApp conectado ao sandbox
- [ ] Mensagem de teste recebida

---

## 🐳 3. Configurar Docker Compose

1. [ ] Abrir arquivo: `docker-compose.yml`
2. [ ] Localizar seção `app-telemedicina`
3. [ ] Substituir valores das variáveis:

```yaml
environment:
  # Zoom
  ZOOM_ACCOUNT_ID: "COLAR_SEU_ACCOUNT_ID_AQUI"
  ZOOM_CLIENT_ID: "COLAR_SEU_CLIENT_ID_AQUI"
  ZOOM_CLIENT_SECRET: "COLAR_SEU_CLIENT_SECRET_AQUI"
  
  # Twilio
  TWILIO_ACCOUNT_SID: "COLAR_SEU_ACCOUNT_SID_AQUI"
  TWILIO_AUTH_TOKEN: "COLAR_SEU_AUTH_TOKEN_AQUI"
  TWILIO_WHATSAPP_FROM: "whatsapp:+14155238886"  # Seu número Twilio
```

4. [ ] Salvar arquivo

---

## 🚀 4. Executar o Sistema

### Iniciar Todos os Serviços:

```bash
# Na raiz do projeto (fiap-hackaton)
docker-compose up -d
```

- [ ] Comando executado sem erros

### Verificar Status dos Containers:

```bash
docker ps
```

Você deve ver:
- [ ] `sus-postgres` - Status: Up
- [ ] `sus-mock-api` - Status: Up
- [ ] `sus-agendamento` - Status: Up
- [ ] `sus-telemedicina` - Status: Up

### Verificar Logs:

```bash
# Logs do Telemedicina
docker logs sus-telemedicina -f

# Procurar por:
# ✅ "Started TelemedicinaApplication"
# ✅ "Starting scheduled check for telemedicine appointments"
```

- [ ] Aplicação iniciou sem erros
- [ ] Scheduler está rodando

### Testar Health Check:

```bash
curl http://localhost:8081/api/telemedicina/health
```

Resposta esperada: `SUS Telemedicina Service is running`

- [ ] Health check respondeu corretamente

---

## 🧪 5. Testar Funcionalidades

### 5.1. Criar Agendamento de Teste

Via sistema de agendamento (porta 8080) ou direto no banco:

```sql
-- Conectar ao banco
docker exec -it sus-postgres psql -U sus -d susdb

-- Inserir agendamento de teste (consulta em 20 minutos)
INSERT INTO agendamentos (
    paciente_id, profissional_id, codigo_cnes_unidade, 
    especialidade_id, data_hora_agendamento, status, 
    tipo_atendimento, data_criacao
) VALUES (
    1, 1, '2269767', 
    1, NOW() + INTERVAL '20 minutes', 'AGENDADO', 
    'TELEMEDICINA', NOW()
);

-- Verificar
SELECT id, tipo_atendimento, status, data_hora_agendamento 
FROM agendamentos 
WHERE tipo_atendimento = 'TELEMEDICINA';
```

- [ ] Agendamento criado com sucesso
- [ ] Tipo: TELEMEDICINA
- [ ] Status: AGENDADO
- [ ] Data: Futuro (próximos 20 minutos)

### 5.2. Criar Consulta Manualmente (Teste)

```bash
# Criar consulta para o agendamento
curl -X POST http://localhost:8081/api/telemedicina/consultas \
  -H "Content-Type: application/json" \
  -d '{"agendamentoId": 1}'
```

Resposta esperada:
```json
{
  "id": 1,
  "agendamentoId": 1,
  "zoomMeetingId": "...",
  "zoomJoinUrl": "https://zoom.us/j/...",
  "notificacaoEnviada": false,
  ...
}
```

- [ ] Consulta criada com sucesso
- [ ] Link do Zoom gerado
- [ ] Dados salvos no banco

### 5.3. Enviar Notificação Manualmente (Teste)

```bash
# Enviar notificação
curl -X POST http://localhost:8081/api/telemedicina/consultas/1/notificar
```

- [ ] Requisição retornou 200 OK
- [ ] WhatsApp recebido no seu telefone
- [ ] Mensagem contém link do Zoom
- [ ] Link está acessível

### 5.4. Processar Automaticamente (Teste)

```bash
# Processar agendamentos manualmente
curl -X POST "http://localhost:8081/api/telemedicina/processar?antecedenciaMinutos=15"
```

- [ ] Requisição retornou 200 OK
- [ ] Logs mostram processamento

### 5.5. Verificar Scheduler Automático

Aguardar 5 minutos e verificar logs:

```bash
docker logs sus-telemedicina -f | grep -i "scheduled"
```

- [ ] Scheduler executou automaticamente
- [ ] Logs mostram busca de agendamentos

---

## ✅ 6. Validação Final

### 6.1. Verificar Banco de Dados

```sql
docker exec -it sus-postgres psql -U sus -d susdb

-- Ver consultas criadas
SELECT * FROM consultas_telemedicina;

-- Ver agendamentos processados
SELECT a.id, a.tipo_atendimento, c.zoom_join_url, c.notificacao_enviada
FROM agendamentos a
LEFT JOIN consultas_telemedicina c ON c.agendamento_id = a.id
WHERE a.tipo_atendimento = 'TELEMEDICINA';
```

- [ ] Tabela `consultas_telemedicina` existe
- [ ] Registros estão sendo criados
- [ ] Relacionamento com `agendamentos` funciona

### 6.2. Verificar APIs

```bash
# Health check
curl http://localhost:8081/api/telemedicina/health

# Deve retornar: "SUS Telemedicina Service is running"
```

- [ ] API está respondendo
- [ ] Porta 8081 acessível

### 6.3. Teste Completo End-to-End

1. [ ] Criar agendamento de telemedicina (20 min no futuro)
2. [ ] Aguardar scheduler processar OU processar manualmente
3. [ ] Verificar consulta criada no banco
4. [ ] Verificar notificação WhatsApp recebida
5. [ ] Acessar link do Zoom
6. [ ] Verificar reunião funcionando

---

## 🎯 7. Troubleshooting

### Problemas Comuns:

#### ❌ Erro: "Failed to get Zoom access token"
- [ ] Verificar credenciais do Zoom
- [ ] Verificar se aplicação Zoom está ativada
- [ ] Verificar scopes configurados

#### ❌ Erro: "Failed to send WhatsApp message"
- [ ] Verificar credenciais do Twilio
- [ ] Verificar se WhatsApp está conectado ao sandbox
- [ ] Verificar formato do telefone (+55...)

#### ❌ Erro: "Appointment not found"
- [ ] Verificar se agendamento existe no banco
- [ ] Verificar ID correto

#### ❌ Erro: "Table doesn't exist"
- [ ] Reiniciar container: `docker-compose restart sus-telemedicina`
- [ ] Verificar logs do Flyway

#### ❌ Container não inicia
- [ ] Verificar logs: `docker logs sus-telemedicina`
- [ ] Verificar se PostgreSQL está rodando
- [ ] Verificar credenciais do banco

---

## 📊 8. Monitoramento

### Logs para Acompanhar:

```bash
# Logs em tempo real
docker logs sus-telemedicina -f

# Filtrar logs importantes
docker logs sus-telemedicina 2>&1 | grep -E "(Creating Zoom|WhatsApp|scheduled)"
```

### Métricas a Acompanhar:

- [ ] Consultas criadas por dia
- [ ] Notificações enviadas vs. total
- [ ] Erros de integração (Zoom/Twilio)
- [ ] Tempo de resposta da API

---

## 🎉 Conclusão

Se todos os itens acima estão marcados ✅, seu sistema está:

- ✅ **Configurado corretamente**
- ✅ **Integrado com Zoom e WhatsApp**
- ✅ **Processando agendamentos automaticamente**
- ✅ **Enviando notificações**
- ✅ **Pronto para produção!**

---

## 📞 Ajuda Adicional

Consulte:
- `README.md` - Documentação completa
- `QUICK_START.md` - Guia rápido
- `API_DOCUMENTATION.md` - API REST
- `DATABASE_SCHEMA.md` - Banco de dados
- `PROJECT_SUMMARY.md` - Resumo do projeto

---

**Data da Configuração:** ___/___/______

**Configurado por:** _________________________

**Status:** ⬜ Em Progresso  ⬜ Concluído ✅

---

**Boa sorte! 🚀**

