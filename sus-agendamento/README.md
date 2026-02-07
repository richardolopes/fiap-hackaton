# 🏥 SUS Agendamento - Sistema de Agendamento de Consultas e Exames

## 📋 Sobre o Projeto

Sistema de agendamento de consultas e exames para o SUS (Sistema Único de Saúde), desenvolvido como MVP para o **Hackathon FIAP**. O projeto utiliza **Clean Architecture** e integra múltiplas fontes de dados mockadas para simular um ambiente realista de integração com sistemas do SUS.

### Funcionalidades Principais

- ✅ Agendamento de consultas presenciais e telemedicina
- ✅ Confirmação de agendamentos
- ✅ Cancelamento por paciente ou unidade de saúde
- ✅ Reagendamento de consultas
- ✅ Consulta de horários disponíveis
- ✅ Busca de agendamentos por paciente ou unidade
- ✅ Integração com dados de especialidades, profissionais e unidades de saúde

---

## 🏗️ Arquitetura

### Clean Architecture

O projeto segue os princípios de **Clean Architecture**, separando responsabilidades em camadas:

```
src/main/java/br/gov/sus/sus/
├── domain/                    # 🎯 Camada de Domínio (regras de negócio)
│   ├── entity/                	# Entidades de negócio puras
│   ├── enums/                 	# Enumerações (StatusAgendamento, TipoAtendimento)
│   ├── gateway/               	# Interfaces dos gateways (portas)
│   └── usecase/               	# Casos de uso (regras de negócio)
│
├── infrastructure/            # 🔧 Camada de Infraestrutura
│   ├── client/                	# Feign clients (APIs externas mockadas)
│   │   ├── dto/               	# DTOs das respostas das APIs
│   │   └── mapper/            	# Mapeadores API Response -> Domain Entity
│   ├── config/                	# Configurações Spring
│   ├── gateway/               	# Implementações dos gateways (APIs externas)
│   └── persistence/           	# JPA (banco de dados local)
│       ├── entity/            	# Entidades JPA
│       ├── gateway/           	# Implementação gateway de Agendamento
│       ├── mapper/            	# Mapeadores JPA <-> Domain
│       └── repository/        	# Repositórios Spring Data JPA
│
└── application/               # 📱 Camada de Aplicação
    ├── controller/            	# Controllers REST API
    ├── dto/                   	# DTOs de request e response
    │   ├── request/
    │   └── response/
    └── exception/             # ❌ Tratamento global de exceções
```

### Fontes de Dados

| Dados | Fonte | Descrição |
|-------|-------|-----------|
| **Unidades de Saúde** | API DataSUS (Real) | `https://apidadosabertos.saude.gov.br` |
| **Especialidades** | json-server (Mock) | `http://localhost:3000/especialidades` |
| **Profissionais** | json-server (Mock) | `http://localhost:3000/profissionais` |
| **Pacientes** | json-server (Mock) | `http://localhost:3000/pacientes` |
| **Horários** | json-server (Mock) | `http://localhost:3000/horarios` |
| **Agendamentos** | Banco H2 (Local) | Persistido localmente |

---

## 🚀 Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Node.js 18+ (para json-server)

### 1. Iniciar o Mock Server (json-server)

```bash
cd mock-server
npm install
npm start
```

O json-server estará disponível em `http://localhost:3000`

### 2. Iniciar a Aplicação Spring Boot

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

### 3. Acessar a Documentação da API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:susdb`
  - Username: `sa`
  - Password: (vazio)

---

## 📚 Documentação da API

### Base URL

```
http://localhost:8080/api/v1
```

---

### 🗓️ Agendamentos

#### Criar Agendamento

```http
POST /agendamentos
Content-Type: application/json

{
    "pacienteId": 1,
    "profissionalId": 1,
    "codigoCnesUnidade": "2269473",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-15T10:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Primeira consulta"
}
```

**Tipos de Atendimento:** `PRESENCIAL`, `TELEMEDICINA`

**Response (201 Created):**

```json
{
    "id": 1,
    "nomePaciente": "José da Silva",
    "cpfPaciente": "12345678901",
    "cartaoSusPaciente": "123456789012345",
    "nomeProfissional": "Dr. João Silva",
    "registroConselhoProfissional": "CRM-SP 123456",
    "nomeUnidadeSaude": "UBS Jardim São Paulo",
    "enderecoUnidadeSaude": "Rua das Flores, 100",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-02-15T10:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Primeira consulta",
    "dataCriacao": "2026-02-05T17:31:30.120226956"
}
```

---

#### Buscar Agendamento por ID

```http
GET /agendamentos/{id}
```

**Response (200 OK):**

```json
{
    "id": 1,
    "nomePaciente": "José da Silva",
    "cpfPaciente": "12345678901",
    "cartaoSusPaciente": "123456789012345",
    "nomeProfissional": "Dr. João Silva",
    "registroConselhoProfissional": "CRM-SP 123456",
    "nomeUnidadeSaude": "UBS Jardim São Paulo",
    "enderecoUnidadeSaude": "Rua das Flores, 100",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-02-15T10:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": null,
    "dataCriacao": "2026-02-05T17:31:30"
}
```

---

#### Buscar Agendamentos por Paciente

```http
GET /agendamentos/paciente/{pacienteId}
```

**Response (200 OK):** Array de agendamentos

---

#### Buscar Agendamentos por Unidade de Saúde

```http
GET /agendamentos/unidade/{codigoCnesUnidade}
```

**Response (200 OK):** Array de agendamentos

---

#### Confirmar Agendamento

```http
PATCH /agendamentos/{id}/confirmar
```

**Response (200 OK):**

```json
{
    "id": 1,
    "status": "CONFIRMADO",
    ...
}
```

---

#### Cancelar Agendamento (pelo Paciente)

```http
PATCH /agendamentos/{id}/cancelar/paciente
Content-Type: application/json

{
    "motivo": "Não poderei comparecer"
}
```

**Response (200 OK):**

```json
{
    "id": 1,
    "status": "CANCELADO_PACIENTE",
    ...
}
```

---

#### Cancelar Agendamento (pela Unidade)

```http
PATCH /agendamentos/{id}/cancelar/unidade
Content-Type: application/json

{
    "motivo": "Profissional indisponível"
}
```

**Response (200 OK):**

```json
{
    "id": 1,
    "status": "CANCELADO_UNIDADE",
    ...
}
```

---

#### Reagendar Consulta

```http
PATCH /agendamentos/{id}/reagendar?novaDataHora=2026-02-20T14:00:00
```

**Response (200 OK):**

```json
{
    "id": 1,
    "status": "AGENDADO",
    "dataHoraAgendamento": "2026-02-20T14:00:00",
    ...
}
```

---

#### Buscar Horários Disponíveis

```http
GET /agendamentos/horarios-disponiveis?codigoCnesUnidade=2269473&especialidadeId=1&data=2026-02-15
```

**Response (200 OK):**

```json
[
    {
        "dataHora": "2026-02-15T08:00:00",
        "profissionalId": 1,
        "nomeProfissional": "Dr. João Silva",
        "codigoCnesUnidade": "2269473",
        "nomeUnidadeSaude": "UBS Jardim São Paulo",
        "especialidadeId": 1,
        "nomeEspecialidade": "Clínica Geral",
        "disponivel": true
    }
]
```

---

### 🏥 Unidades de Saúde

#### Listar Unidades por UF

```http
GET /unidades-saude/uf/{codigoUf}
```

#### Buscar por Código CNES

```http
GET /unidades-saude/cnes/{codigoCnes}
```

---

### 👨‍⚕️ Profissionais

#### Listar Todos os Profissionais

```http
GET /profissionais
```

#### Buscar por ID

```http
GET /profissionais/{id}
```

#### Listar por Unidade de Saúde

```http
GET /profissionais/unidade/{codigoCnesUnidade}
```

#### Listar por Especialidade

```http
GET /profissionais/especialidade/{especialidadeId}
```

---

### 🩺 Especialidades

#### Listar Todas as Especialidades

```http
GET /especialidades
```

**Response (200 OK):**

```json
[
    {
        "id": 1,
        "nome": "Clínica Geral",
        "descricao": "Atendimento geral e preventivo",
        "ativa": true
    },
    {
        "id": 2,
        "nome": "Cardiologia",
        "descricao": "Especialidade do coração",
        "ativa": true
    }
]
```

#### Buscar por ID

```http
GET /especialidades/{id}
```

---

### 👤 Pacientes

#### Listar Todos os Pacientes

```http
GET /pacientes
```

#### Buscar por ID

```http
GET /pacientes/{id}
```

#### Buscar por CPF

```http
GET /pacientes/cpf/{cpf}
```

#### Buscar por Cartão SUS

```http
GET /pacientes/cartao-sus/{cartaoSus}
```

---

## 📊 Status do Agendamento

| Status               | Descrição                                  |
|----------------------|--------------------------------------------|
| `AGENDADO`           | Agendamento criado, aguardando confirmação |
| `CONFIRMADO`         | Paciente confirmou presença                |
| `CANCELADO_PACIENTE` | Cancelado pelo paciente                    |
| `CANCELADO_UNIDADE`  | Cancelado pela unidade de saúde            |
| `EM_ATENDIMENTO`     | Consulta em andamento                      |
| `CONCLUIDO`          | Consulta finalizada                        |
| `NAO_COMPARECEU`     | Paciente não compareceu                    |

---

## 🗄️ Banco de Dados

### Desenvolvimento (H2 In-Memory)

- **URL:** `jdbc:h2:mem:susdb`
- **Console:** `http://localhost:8080/h2-console`
- **User:** `sa`
- **Password:** (vazio)

### Produção (PostgreSQL)

Configure as variáveis de ambiente:

```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=localhost
DB_PORT=5432
DB_NAME=susdb
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
```

---

## 🧪 Dados de Teste (Mock Server)

### Unidades de Saúde Disponíveis

| Código CNES | Nome                       | Tipo     |
|-------------|----------------------------|----------|
| `2269473`   | UBS Jardim São Paulo       | UBS      |
| `2078015`   | Hospital Municipal Central | Hospital |
| `3456789`   | UPA 24h Vila Maria         | UPA      |

### Pacientes de Teste

| ID | Nome          | CPF         |
|----|---------------|-------------|
| 1  | José da Silva | 12345678901 |
| 2  | Maria Santos  | 98765432100 |
| 3  | João Oliveira | 11122233344 |

### Profissionais de Teste

| ID | Nome              | Especialidade |
|----|-------------------|---------------|
| 1  | Dr. João Silva    | Clínica Geral |
| 2  | Dra. Maria Santos | Cardiologia   |
| 3  | Dr. Pedro Costa   | Pediatria     |

### Especialidades

| ID | Nome          |
|----|---------------|
| 1  | Clínica Geral |
| 2  | Cardiologia   |
| 3  | Pediatria     |
| 4  | Ortopedia     |
| 5  | Dermatologia  |

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Cloud OpenFeign** - Clients HTTP declarativos
- **Spring Data JPA** - Persistência
- **H2 Database** - Banco em memória (dev)
- **PostgreSQL** - Banco de produção
- **Lombok** - Redução de boilerplate
- **json-server** - Mock de APIs externas
- **Maven** - Gerenciamento de dependências

---

## 📁 Estrutura de Arquivos

```
sus-agendamento/
├── pom.xml                          # Dependências Maven
├── mvnw                             # Maven Wrapper
├── README.md                        # Esta documentação
│
├── mock-server/                     # Mock das APIs externas
│   ├── package.json
│   └── db.json                      # Dados mockados
│
└── src/
    ├── main/
    │   ├── java/br/gov/sus/sus/
    │   │   ├── SusApplication.java
    │   │   ├── application/
    │   │   ├── domain/
    │   │   └── infrastructure/
    │   └── resources/
    │       ├── application.yml
    │       └── data.sql
    └── test/
        └── java/
```

---

## 🔧 Configuração

### application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:susdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true

api:
  mock-server:
    url: http://localhost:3000
```

---

## 📝 Exemplos de Uso com cURL

### Fluxo Completo de Agendamento

```bash
# 1. Listar especialidades disponíveis
curl http://localhost:8080/api/v1/especialidades

# 2. Buscar profissionais da especialidade
curl http://localhost:8080/api/v1/profissionais/especialidade/1

# 3. Verificar horários disponíveis
curl "http://localhost:8080/api/v1/agendamentos/horarios-disponiveis?codigoCnesUnidade=2269473&especialidadeId=1&data=2026-02-15"

# 4. Criar agendamento
curl -X POST http://localhost:8080/api/v1/agendamentos \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "profissionalId": 1,
    "codigoCnesUnidade": "2269473",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-15T10:00:00",
    "tipoAtendimento": "PRESENCIAL"
  }'

# 5. Confirmar agendamento
curl -X PATCH http://localhost:8080/api/v1/agendamentos/1/confirmar

# 6. Consultar agendamentos do paciente
curl http://localhost:8080/api/v1/agendamentos/paciente/1
```

---

## 👥 Equipe

Desenvolvido para o **Hackathon FIAP 2026**

---

## 📄 Licença

Este projeto é apenas para fins educacionais e de demonstração.
