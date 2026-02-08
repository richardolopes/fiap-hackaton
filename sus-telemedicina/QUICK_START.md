# Guia Rápido - SUS Telemedicina

## 🚀 Como Começar em 5 Minutos

### 1. Pré-requisitos

- Docker e Docker Compose instalados
- Conta Zoom (https://marketplace.zoom.us/)
- Conta Twilio (https://www.twilio.com/)

### 2. Configurar Credenciais

Edite o arquivo `docker-compose.yml` na raiz do projeto e substitua os valores:

```yaml
ZOOM_ACCOUNT_ID: "SEU-ACCOUNT-ID"
ZOOM_CLIENT_ID: "SEU-CLIENT-ID"
ZOOM_CLIENT_SECRET: "SEU-CLIENT-SECRET"
TWILIO_ACCOUNT_SID: "SEU-ACCOUNT-SID"
TWILIO_AUTH_TOKEN: "SEU-AUTH-TOKEN"
TWILIO_WHATSAPP_FROM: "whatsapp:+SEU-NUMERO"
```

### 3. Iniciar o Sistema

Na raiz do projeto:

```bash
docker-compose up -d
```

### 4. Verificar Status

```bash
# Verificar logs
docker logs sus-telemedicina -f

# Verificar health
curl http://localhost:8081/api/telemedicina/health
```

### 5. Criar um Agendamento de Teste

Primeiro, crie um agendamento no sistema de agendamentos (porta 8080) do tipo TELEMEDICINA.

### 6. Testar Manualmente

```bash
# Criar consulta
curl -X POST http://localhost:8081/api/telemedicina/consultas \
  -H "Content-Type: application/json" \
  -d '{"agendamentoId": 1}'

# Processar agendamentos manualmente
curl -X POST http://localhost:8081/api/telemedicina/processar
```

## 📋 Obter Credenciais

### Zoom API

1. Acesse https://marketplace.zoom.us/
2. Clique em "Develop" > "Build App"
3. Escolha "Server-to-Server OAuth"
4. Preencha as informações da aplicação
5. Copie: Account ID, Client ID e Client Secret
6. Em "Scopes", adicione: `meeting:write:admin`, `meeting:read:admin`

### Twilio WhatsApp

1. Crie conta em https://www.twilio.com/
2. Acesse o Console
3. Copie: Account SID e Auth Token
4. Configure WhatsApp:
   - Para testes: Use o Sandbox (https://www.twilio.com/console/sms/whatsapp/sandbox)
   - Para produção: Configure um número próprio
5. Siga as instruções para conectar seu WhatsApp ao sandbox

## 🔧 Configuração Local (sem Docker)

### 1. Configurar Banco de Dados

```bash
# Iniciar apenas o PostgreSQL
docker-compose up -d postgres
```

### 2. Configurar Variáveis de Ambiente

Copie o arquivo `.env.example` para `.env` e configure:

```bash
cp .env.example .env
# Edite o arquivo .env com suas credenciais
```

### 3. Executar a Aplicação

```bash
./mvnw spring-boot:run
```

## ✅ Checklist de Configuração

- [ ] Docker instalado e rodando
- [ ] PostgreSQL iniciado (porta 5432)
- [ ] Credenciais Zoom configuradas
- [ ] Credenciais Twilio configuradas
- [ ] Mock API rodando (porta 3000)
- [ ] Sistema de agendamento rodando (porta 8080)
- [ ] Sistema de telemedicina rodando (porta 8081)
- [ ] Health check respondendo

## 🐛 Troubleshooting Rápido

### Erro ao criar reunião Zoom
```
Verifique se as credenciais estão corretas no docker-compose.yml
```

### Erro ao enviar WhatsApp
```
Verifique se o número do paciente está no formato internacional: +5511999999999
Verifique se conectou seu WhatsApp ao Sandbox do Twilio
```

### Tabela não existe
```
docker-compose restart sus-telemedicina
# Flyway criará as tabelas automaticamente
```

### Scheduler não está rodando
```
Verifique os logs: docker logs sus-telemedicina -f
Verifique se SCHEDULER_ENABLED=true no docker-compose.yml
```

## 📞 Suporte

Para mais informações, consulte:
- README.md - Documentação completa
- API_DOCUMENTATION.md - Documentação da API

## 🎯 Próximos Passos

1. ✅ Sistema configurado
2. 📝 Criar agendamentos de teste
3. 🧪 Testar criação de consultas
4. 📱 Testar envio de notificações
5. 🚀 Colocar em produção

---

**Boa sorte!** 🎉

