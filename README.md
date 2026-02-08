# 🏥 SUS Hackathon FIAP - Sistema Integrado de Saúde Digital

<p align="center">
  <img src="hackaton.drawio.png" alt="Arquitetura do Sistema" width="800">
</p>

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Módulos do Sistema](#módulos-do-sistema)
- [Benefícios e Impacto](#benefícios-e-impacto)
- [Tecnologias](#tecnologias)
- [Como Executar](#como-executar)
- [APIs Disponíveis](#apis-disponíveis)
- [Equipe](#equipe)

---

## 🎯 Sobre o Projeto

Sistema integrado de saúde digital desenvolvido para o **Hackathon FIAP 2026**, com foco em modernizar e democratizar o acesso aos serviços do **SUS (Sistema Único de Saúde)**.

O projeto consiste em duas aplicações principais que trabalham de forma integrada:

1. **SUS Agendamento** - Sistema completo de agendamento de consultas e exames
2. **SUS Telemedicina** - Plataforma de atendimento remoto com integração Zoom e WhatsApp

### 🌟 Problema que Resolvemos

- **Longas filas de espera** em unidades de saúde
- **Dificuldade de acesso** para população em áreas remotas
- **Falta de integração** entre sistemas do SUS
- **Sobrecarga** das unidades de saúde presenciais
- **Ausência de notificações** automáticas para pacientes

### 💡 Nossa Solução

Sistema integrado que permite:
- ✅ Agendamento online de consultas presenciais e remotas
- ✅ Telemedicina com videoconferência via Zoom
- ✅ Notificações automáticas via WhatsApp
- ✅ Busca inteligente por especialidades e unidades próximas
- ✅ Redução de filas e otimização de recursos

---

## 🏗️ Arquitetura

### Visão Geral

O sistema segue uma **arquitetura de microsserviços**, com módulos independentes mas integrados, utilizando os princípios de **Clean Architecture**.

```
┌─────────────────────────────────────────────────────────────────┐
│                        CAMADA DE USUÁRIO                         │
│  👤 Pacientes  │  👨‍⚕️ Médicos  │  🏥 Unidades de Saúde         │
└─────────────────────────────────────────────────────────────────┘
                              ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────────┐
│                     CAMADA DE APLICAÇÃO                          │
│  ┌──────────────────┐         ┌─────────────────────┐          │
│  │ SUS Agendamento  │←──────→│ SUS Telemedicina    │          │
│  │   (Port 8080)    │         │    (Port 8081)      │          │
│  └──────────────────┘         └─────────────────────┘          │
└─────────────────────────────────────────────────────────────────┘
                    ↓                        ↓
┌─────────────────────────────────────────────────────────────────┐
│                    CAMADA DE DOMÍNIO                             │
│              sus-shared-domain (Entidades e Enums)              │
└─────────────────────────────────────────────────────────────────┘
                    ↓                        ↓
┌─────────────────────────────────────────────────────────────────┐
│                  CAMADA DE INFRAESTRUTURA                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │PostgreSQL│  │Mock APIs │  │Zoom API  │  │Twilio    │       │
│  │(susdb)   │  │(Port 3000│  │          │  │WhatsApp  │       │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

### Arquitetura Detalhada

Conforme ilustrado no diagrama acima, o sistema possui:

1. **Frontend/Interface** - Camada de apresentação (web/mobile)
2. **API Gateway** - Roteamento e balanceamento de carga
3. **Microsserviços**:
   - **Agendamento**: Gerencia consultas e exames
   - **Telemedicina**: Gerencia consultas remotas
4. **Domínio Compartilhado**: Entidades e regras de negócio comuns
5. **Integrações**:
   - PostgreSQL (dados persistentes)
   - Mock APIs SUS (dados mestres)
   - Zoom (videoconferência)
   - Twilio (notificações WhatsApp)

### Princípios Arquiteturais

- **Clean Architecture**: Separação clara de responsabilidades
- **Domain-Driven Design**: Modelo de domínio rico
- **SOLID**: Código manutenível e extensível
- **API-First**: Documentação e contratos bem definidos
- **Cloud-Ready**: Preparado para deploy em containers

---

## 📦 Módulos do Sistema

### 1. 🗓️ SUS Agendamento (Port 8080)

Sistema completo para gerenciamento de consultas e exames.

#### Funcionalidades

| Funcionalidade | Descrição |
|---------------|-----------|
| **Criar Agendamento** | Agendar consultas presenciais ou telemedicina |
| **Buscar por CEP** | Encontrar unidades de saúde próximas ao paciente |
| **Consultar Disponibilidade** | Ver horários disponíveis por profissional |
| **Confirmar Agendamento** | Paciente ou unidade confirma a consulta |
| **Cancelar Agendamento** | Cancelamento com motivo registrado |
| **Reagendar** | Alterar data/hora de consulta existente |
| **Listar Agendamentos** | Buscar por paciente, unidade ou profissional |

#### Endpoints Principais

```http
POST   /api/agendamentos                    # Criar agendamento
POST   /api/agendamentos/por-cep            # Buscar e agendar por CEP
GET    /api/agendamentos/{id}               # Consultar agendamento
GET    /api/agendamentos/paciente/{id}      # Listar por paciente
GET    /api/agendamentos/unidade/{cnes}     # Listar por unidade
PUT    /api/agendamentos/{id}/confirmar     # Confirmar
PUT    /api/agendamentos/{id}/cancelar      # Cancelar
PUT    /api/agendamentos/{id}/reagendar     # Reagendar
GET    /api/horarios/disponiveis            # Consultar disponibilidade
```

#### Integrações

- **API DataSUS**: Dados reais de unidades de saúde
- **Mock SUS API**: Dados de especialidades, profissionais e pacientes
- **PostgreSQL**: Persistência de agendamentos

### 2. 📹 SUS Telemedicina (Port 8081)

Plataforma de atendimento remoto com automação completa.

#### Funcionalidades

| Funcionalidade | Descrição |
|---------------|-----------|
| **Criar Consulta Virtual** | Gera link de reunião no Zoom automaticamente |
| **Notificações Automáticas** | Envia link via WhatsApp antes da consulta |
| **Scheduler Inteligente** | Busca agendamentos e notifica com antecedência |
| **Histórico de Consultas** | Rastreabilidade completa das teleconsultas |
| **Status em Tempo Real** | Acompanhamento do status da consulta |

#### Endpoints Principais

```http
POST   /api/telemedicina/consultas                      # Criar consulta virtual
GET    /api/telemedicina/consultas/{id}                 # Consultar consulta
GET    /api/telemedicina/consultas/agendamento/{id}     # Por agendamento
PUT    /api/telemedicina/consultas/{id}/status          # Atualizar status
```

#### Integrações

- **Zoom API**: Criação automática de reuniões
- **Twilio WhatsApp**: Envio de notificações
- **PostgreSQL**: Busca de agendamentos de telemedicina
- **Scheduler**: Execução automática a cada 5 minutos

#### Fluxo de Atendimento

```
1. Paciente agenda consulta de TELEMEDICINA
   ↓
2. Sistema cria automaticamente a consulta virtual
   ↓
3. Integração Zoom gera link da reunião
   ↓
4. Sistema persiste dados da consulta
   ↓
5. Scheduler monitora horário da consulta
   ↓
6. 15 minutos antes: envia WhatsApp ao paciente
   ↓
7. Paciente clica no link e inicia consulta
   ↓
8. Após consulta: sistema atualiza status
```

### 3. 🗂️ SUS Shared Domain

Módulo compartilhado com entidades e regras de negócio.

#### Entidades Compartilhadas

- **Agendamento**: Informações da consulta agendada
- **Paciente**: Dados cadastrais do paciente
- **Profissional**: Médicos e profissionais de saúde
- **Especialidade**: Especialidades médicas
- **UnidadeSaude**: Postos e hospitais

#### Enums Compartilhados

- **StatusAgendamento**: AGENDADO, CONFIRMADO, EM_ATENDIMENTO, CONCLUIDO, CANCELADO_PACIENTE, CANCELADO_UNIDADE, NAO_COMPARECEU
- **TipoAtendimento**: PRESENCIAL, TELEMEDICINA

---

## 🎁 Benefícios e Impacto

### Para Pacientes 👥

| Benefício | Impacto |
|-----------|---------|
| **Acesso Facilitado** | Agendar consultas de casa, sem filas |
| **Atendimento Remoto** | Consultas sem deslocamento |
| **Notificações Automáticas** | Lembrete da consulta via WhatsApp |
| **Transparência** | Acompanhamento do status em tempo real |
| **Redução de Custos** | Economia com transporte |

### Para Profissionais de Saúde 👨‍⚕️

| Benefício | Impacto |
|-----------|---------|
| **Agenda Organizada** | Visualização clara dos agendamentos |
| **Telemedicina Integrada** | Ferramenta profissional para atendimento remoto |
| **Redução de Faltas** | Notificações diminuem abstenções |
| **Otimização de Tempo** | Menos tempo em tarefas administrativas |
| **Atendimento Ampliado** | Alcançar pacientes em áreas remotas |

### Para Gestão do SUS 🏥

| Benefício | Impacto |
|-----------|---------|
| **Redução de Filas** | Menor sobrecarga nas unidades |
| **Dados Estruturados** | Métricas e relatórios precisos |
| **Otimização de Recursos** | Melhor aproveitamento de profissionais |
| **Escalabilidade** | Atendimento de mais pacientes |
| **Integração** | Sistema unificado e interoperável |

### Impacto Social 🌍

- **Democratização**: Acesso à saúde para áreas remotas
- **Inclusão Digital**: Aproximação da tecnologia com a população
- **Sustentabilidade**: Redução de deslocamentos e emissões
- **Eficiência**: Melhor uso de recursos públicos
- **Qualidade**: Atendimento mais ágil e humanizado

---

## 🛠️ Tecnologias

### Backend

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 3.3.0 | Framework web |
| **Spring Data JPA** | 3.3.0 | Persistência de dados |
| **Spring Cloud OpenFeign** | 2023.0.3 | Cliente HTTP declarativo |
| **Hibernate** | 6.5.2 | ORM |
| **Maven** | 3.9+ | Gerenciamento de dependências |

### Banco de Dados

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| **PostgreSQL** | 16 | Banco principal |
| **H2** | 2.2.224 | Testes e desenvolvimento |
| **Flyway** | (removido) | Migrations |

### Integrações

| Serviço | Versão | Uso |
|---------|--------|-----|
| **Zoom API** | v2 | Videoconferência |
| **Twilio WhatsApp** | API 2010-04-01 | Notificações |
| **JSON Server** | 0.17.4 | Mock APIs SUS |

### Infraestrutura

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| **Docker** | 24+ | Containerização |
| **Docker Compose** | 2+ | Orquestração local |
| **Eclipse Temurin** | 21 | Runtime Java |

### Qualidade e Documentação

| Ferramenta | Uso |
|------------|-----|
| **Lombok** | Redução de boilerplate |
| **SLF4J + Logback** | Logging |
| **Swagger/OpenAPI** | Documentação de APIs |

---

## 🚀 Como Executar

### Pré-requisitos

- **Java 21** instalado
- **Docker** e **Docker Compose** instalados
- **Maven 3.9+** (ou usar mvnw)
- Credenciais **Zoom API** (opcional para demo)
- Credenciais **Twilio** (opcional para demo)

### Opção 1: Docker Compose (Recomendado)

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/fiap-hackaton.git
cd fiap-hackaton

# 2. Configure as variáveis de ambiente (opcional)
# Edite o docker-compose.yml e adicione suas credenciais Zoom e Twilio

# 3. Inicie todos os serviços
docker-compose up -d

# 4. Acompanhe os logs
docker-compose logs -f

# 5. Verifique o status
docker-compose ps
```

**Serviços disponíveis:**
- SUS Agendamento: http://localhost:8080
- SUS Telemedicina: http://localhost:8081
- Mock SUS API: http://localhost:3000
- PostgreSQL: localhost:5432

### Opção 2: Executar Localmente

```bash
# 1. Instale o sus-shared-domain
cd sus-shared-domain
mvn clean install
cd ..

# 2. Inicie o PostgreSQL
docker-compose up -d postgres

# 3. Inicie o Mock API
cd mock-sus-api
npm install
npm start &
cd ..

# 4. Inicie o SUS Agendamento
cd sus-agendamento
./mvnw spring-boot:run &
cd ..

# 5. Inicie o SUS Telemedicina
cd sus-telemedicina
./mvnw spring-boot:run
```

### Opção 3: Build Manual dos Containers

```bash
# Build todos os serviços
docker-compose build

# Build serviço específico
docker-compose build app-agendamento
docker-compose build app-telemedicina
```

### Parar os Serviços

```bash
# Parar todos os serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

---

## 📡 APIs Disponíveis

### SUS Agendamento API

**Base URL**: `http://localhost:8080/api`

#### Criar Agendamento

```http
POST /agendamentos
Content-Type: application/json

{
  "pacienteId": 1,
  "profissionalId": 1,
  "especialidadeId": 1,
  "codigoCnesUnidade": "2269651",
  "dataHoraAgendamento": "2026-02-15T14:00:00",
  "tipoAtendimento": "PRESENCIAL",
  "observacoes": "Primeira consulta"
}
```

#### Buscar Unidade por CEP e Agendar

```http
POST /agendamentos/por-cep
Content-Type: application/json

{
  "pacienteId": 1,
  "cep": "01310-100",
  "especialidadeId": 1,
  "dataHoraAgendamento": "2026-02-15T14:00:00",
  "tipoAtendimento": "TELEMEDICINA"
}
```

#### Consultar Agendamento

```http
GET /agendamentos/1
```

**Resposta:**
```json
{
  "id": 1,
  "paciente": {
    "id": 1,
    "nomeCompleto": "João da Silva",
    "cpf": "123.456.789-00"
  },
  "profissional": {
    "id": 1,
    "nomeCompleto": "Dr. Carlos Santos"
  },
  "dataHoraAgendamento": "2026-02-15T14:00:00",
  "status": "AGENDADO",
  "tipoAtendimento": "TELEMEDICINA"
}
```

#### Confirmar Agendamento

```http
PUT /agendamentos/1/confirmar
```

#### Cancelar Agendamento

```http
PUT /agendamentos/1/cancelar
Content-Type: application/json

{
  "motivo": "Paciente não poderá comparecer",
  "canceladoPeloPaciente": true
}
```

### SUS Telemedicina API

**Base URL**: `http://localhost:8081/api`

#### Criar Consulta Virtual

```http
POST /telemedicina/consultas
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
  "zoomMeetingId": "85749065872",
  "zoomJoinUrl": "https://zoom.us/j/85749065872",
  "zoomStartUrl": "https://zoom.us/s/85749065872",
  "status": "AGENDADA",
  "notificacaoEnviada": false,
  "dataCriacao": "2026-02-08T20:00:00"
}
```

#### Consultar Consulta

```http
GET /telemedicina/consultas/1
```

#### Buscar por Agendamento

```http
GET /telemedicina/consultas/agendamento/1
```

### Mock SUS API

**Base URL**: `http://localhost:3000`

#### Endpoints Disponíveis

```http
GET /especialidades          # Lista todas as especialidades
GET /especialidades/1        # Busca especialidade por ID
GET /profissionais           # Lista todos os profissionais
GET /profissionais/1         # Busca profissional por ID
GET /pacientes              # Lista todos os pacientes
GET /pacientes/1            # Busca paciente por ID
GET /horarios               # Lista horários disponíveis
```

---

## 📊 Modelo de Dados

### Diagrama ER Simplificado

```
┌─────────────────┐         ┌──────────────────┐
│   Paciente      │         │  Profissional    │
├─────────────────┤         ├──────────────────┤
│ id              │         │ id               │
│ nomeCompleto    │         │ nomeCompleto     │
│ cpf             │         │ registroConselho │
│ cartaoSus       │         │ especialidadeId  │
│ telefone        │         │ codigoCnesUnidade│
└─────────────────┘         └──────────────────┘
         │                           │
         └───────┬───────────────────┘
                 │
         ┌───────▼────────┐
         │  Agendamento   │
         ├────────────────┤
         │ id             │
         │ pacienteId     │
         │ profissionalId │
         │ especialidadeId│
         │ unidadeId      │
         │ dataHora       │
         │ status         │
         │ tipoAtendimento│
         └────────────────┘
                 │
                 │ 1:1
                 │
    ┌────────────▼─────────────┐
    │ ConsultaTelemedicina     │
    ├──────────────────────────┤
    │ id                       │
    │ agendamentoId (unique)   │
    │ zoomMeetingId            │
    │ zoomJoinUrl              │
    │ zoomStartUrl             │
    │ status                   │
    │ notificacaoEnviada       │
    │ dataNotificacao          │
    └──────────────────────────┘
```

---

## 🧪 Testando a Aplicação

### 1. Verificar Serviços

```bash
# Status de todos os containers
docker-compose ps

# Logs em tempo real
docker-compose logs -f

# Logs de um serviço específico
docker-compose logs -f app-agendamento
```

### 2. Testar Agendamento

```bash
# Criar um agendamento de telemedicina
curl -X POST http://localhost:8080/api/agendamentos \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "profissionalId": 1,
    "especialidadeId": 1,
    "codigoCnesUnidade": "2269651",
    "dataHoraAgendamento": "2026-02-15T14:00:00",
    "tipoAtendimento": "TELEMEDICINA"
  }'
```

### 3. Verificar Mock API

```bash
# Listar especialidades
curl http://localhost:3000/especialidades

# Listar profissionais
curl http://localhost:3000/profissionais

# Listar pacientes
curl http://localhost:3000/pacientes
```

### 4. Testar Telemedicina

```bash
# Criar consulta virtual (use o ID do agendamento criado)
curl -X POST http://localhost:8081/api/telemedicina/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "agendamentoId": 1
  }'
```

---

## 🔧 Configuração Avançada

### Variáveis de Ambiente

Edite o `docker-compose.yml` para configurar:

#### Zoom API

```yaml
ZOOM_ACCOUNT_ID: "sua-account-id"
ZOOM_CLIENT_ID: "seu-client-id"
ZOOM_CLIENT_SECRET: "seu-client-secret"
```

**Como obter:**
1. Acesse https://marketplace.zoom.us/
2. Crie um app Server-to-Server OAuth
3. Copie as credenciais

#### Twilio WhatsApp

```yaml
TWILIO_ACCOUNT_SID: "seu-account-sid"
TWILIO_AUTH_TOKEN: "seu-auth-token"
TWILIO_WHATSAPP_FROM: "whatsapp:+seu-numero"
```

**Como obter:**
1. Crie conta em https://www.twilio.com/
2. Configure WhatsApp Sandbox
3. Copie as credenciais

#### Scheduler

```yaml
SCHEDULER_ENABLED: "true"                    # Habilitar scheduler
SCHEDULER_CHECK_INTERVAL: "5"                # Intervalo em minutos
NOTIFICATION_ADVANCE_MINUTES: "15"           # Notificar 15 min antes
```

---

## 📚 Documentação Adicional

- [Documentação SUS Agendamento](./sus-agendamento/README.md)
- [Documentação SUS Telemedicina](./sus-telemedicina/README.md)
- [Guia Docker Build](./DOCKER_BUILD.md)
- [API Tests](./sus-agendamento/API_TESTS.md)
- [Arquitetura Detalhada](./sus-telemedicina/ARCHITECTURE.md)

---

## 📝 Licença

Este projeto foi desenvolvido como MVP para o Hackathon FIAP e é de uso educacional.

---

<p align="center">
  Desenvolvido com ❤️ para o SUS e para o Brasil 🇧🇷
</p>

<p align="center">
  <strong>Hackathon FIAP 2026</strong>
</p>

