# Mock External API - Sistema SUS

## 📋 Descrição

Este módulo é um **mock server** que simula as APIs externas do Sistema Único de Saúde (SUS), fornecendo dados fictícios para desenvolvimento e testes. Ele simula os serviços que retornariam informações sobre:

- 🏥 **Unidades Básicas de Saúde (UBS)**
- 👨‍⚕️ **Profissionais de Saúde** (médicos, enfermeiros, etc.)
- 📅 **Horários e Plantões**
- 🩺 **Especialidades Médicas**
- 👤 **Pacientes**
- 📍 **Dados de Localização**

## 🎯 Objetivo

Em um ambiente real, essas informações viriam de diversas APIs governamentais do SUS, como:
- CNES (Cadastro Nacional de Estabelecimentos de Saúde)
- CNS (Cartão Nacional de Saúde)
- Sistemas regionais de saúde

Para facilitar o desenvolvimento e testes sem depender de APIs externas, este mock server fornece uma base de dados simulada que replica o comportamento esperado dessas integrações.

## 🛠️ Tecnologias

- **[JSON Server](https://github.com/typicode/json-server)**: Framework para criar APIs CRUD em REST a partir de um arquivo JSON
- **Node.js**: Runtime JavaScript

## 📦 Instalação

```bash
cd mock-sus-api
npm install
```

## 🚀 Como Executar

### Modo Básico
```bash
npm start
```

O servidor será iniciado em: `http://localhost:3000`

## 📊 Estrutura de Dados

### Especialidades (`/especialidades`)

Contém as especialidades médicas disponíveis no sistema.

**Campos:**
- `id`: Identificador único
- `nome`: Nome da especialidade
- `descricao`: Descrição detalhada
- `tempoConsultaMinutos`: Duração média da consulta
- `ativo`: Status da especialidade

**Exemplo:**
```json
{
  "id": 1,
  "nome": "Clínica Geral",
  "descricao": "Atendimento médico geral e preventivo",
  "tempoConsultaMinutos": 30,
  "ativo": true
}
```

**Especialidades disponíveis:**
- Clínica Geral
- Pediatria
- Cardiologia
- Dermatologia
- Ginecologia
- Ortopedia
- Oftalmologia
- Neurologia
- Psiquiatria
- Endocrinologia

### Profissionais (`/profissionais`)

Cadastro de profissionais de saúde da rede SUS.

**Campos:**
- `id`: Identificador único
- `nomeCompleto`: Nome completo do profissional
- `registroConselho`: Registro profissional (CRM, CRO, etc.)
- `especialidadeId`: Referência à especialidade
- `codigoCnesUnidade`: Código CNES da unidade de saúde
- `ativo`: Status do profissional

**Exemplo:**
```json
{
  "id": 1,
  "nomeCompleto": "Dr. João Silva",
  "registroConselho": "CRM-SP 123456",
  "especialidadeId": 1,
  "codigoCnesUnidade": "2269473",
  "ativo": true
}
```

### Horários (`/horarios`)

Horários de atendimento dos profissionais.

**Campos:**
- `id`: Identificador único
- `profissionalId`: Referência ao profissional
- `diaSemana`: Dia da semana (MONDAY, TUESDAY, etc.)
- `horaInicio`: Hora de início do expediente
- `horaFim`: Hora de término do expediente
- `ativo`: Status do horário

**Exemplo:**
```json
{
  "id": 1,
  "profissionalId": 1,
  "diaSemana": "MONDAY",
  "horaInicio": "08:00",
  "horaFim": "12:00",
  "ativo": true
}
```

### Pacientes (`/pacientes`)

Cadastro de pacientes do SUS.

**Campos:**
- `id`: Identificador único
- `nomeCompleto`: Nome completo do paciente
- `cpf`: CPF do paciente
- `cartaoSus`: Número do Cartão Nacional de Saúde
- `dataNascimento`: Data de nascimento
- `telefone`: Telefone de contato
- `email`: E-mail
- `endereco`: Endereço completo
- `municipio`: Município
- `uf`: Unidade Federativa
- `cep`: CEP

**Exemplo:**
```json
{
  "id": 1,
  "nomeCompleto": "José da Silva",
  "cpf": "12345678901",
  "cartaoSus": "123456789012345",
  "dataNascimento": "1985-05-15",
  "telefone": "11987654321",
  "email": "jose.silva@email.com",
  "endereco": "Rua das Flores, 100",
  "municipio": "São Paulo",
  "uf": "SP",
  "cep": "01310100"
}
```

## 🔌 Endpoints Disponíveis

O JSON Server cria automaticamente endpoints RESTful para cada entidade:

### Especialidades
- `GET /especialidades` - Lista todas as especialidades
- `GET /especialidades/:id` - Busca uma especialidade por ID
- `POST /especialidades` - Cria uma nova especialidade
- `PUT /especialidades/:id` - Atualiza uma especialidade
- `PATCH /especialidades/:id` - Atualiza parcialmente uma especialidade
- `DELETE /especialidades/:id` - Remove uma especialidade

### Profissionais
- `GET /profissionais` - Lista todos os profissionais
- `GET /profissionais/:id` - Busca um profissional por ID
- `GET /profissionais?especialidadeId=:id` - Filtra por especialidade
- `GET /profissionais?codigoCnesUnidade=:codigo` - Filtra por unidade
- `POST /profissionais` - Cria um novo profissional
- `PUT /profissionais/:id` - Atualiza um profissional
- `DELETE /profissionais/:id` - Remove um profissional

### Horários
- `GET /horarios` - Lista todos os horários
- `GET /horarios/:id` - Busca um horário por ID
- `GET /horarios?profissionalId=:id` - Filtra horários por profissional
- `GET /horarios?diaSemana=:dia` - Filtra horários por dia da semana
- `POST /horarios` - Cria um novo horário
- `PUT /horarios/:id` - Atualiza um horário
- `DELETE /horarios/:id` - Remove um horário

### Pacientes
- `GET /pacientes` - Lista todos os pacientes
- `GET /pacientes/:id` - Busca um paciente por ID
- `GET /pacientes?cpf=:cpf` - Busca por CPF
- `GET /pacientes?cartaoSus=:cartao` - Busca por Cartão SUS
- `POST /pacientes` - Cria um novo paciente
- `PUT /pacientes/:id` - Atualiza um paciente
- `DELETE /pacientes/:id` - Remove um paciente

## 🔍 Recursos Avançados

### Filtros e Buscas

O JSON Server suporta diversos parâmetros de query:

```bash
# Filtrar por campo
GET /profissionais?especialidadeId=1

# Busca em texto (full-text search)
GET /pacientes?q=Silva

# Paginação
GET /profissionais?_page=1&_limit=10

# Ordenação
GET /especialidades?_sort=nome&_order=asc

# Operadores
GET /horarios?profissionalId_gte=5  # maior ou igual
GET /horarios?profissionalId_lte=10 # menor ou igual
GET /horarios?profissionalId_ne=3   # diferente
```

### Relacionamentos

```bash
# Incluir relacionamentos (expand)
GET /profissionais?_embed=horarios
GET /horarios?_expand=profissional
```

## 🐳 Docker

Este mock server pode ser executado via Docker Compose junto com os outros serviços do projeto.

```bash
# Na raiz do projeto
docker-compose up mock-sus-api
```

## 📝 Exemplos de Uso

### Listar especialidades ativas
```bash
curl http://localhost:3000/especialidades?ativo=true
```

### Buscar profissionais de uma UBS específica
```bash
curl http://localhost:3000/profissionais?codigoCnesUnidade=2269473
```

### Buscar horários de um profissional específico
```bash
curl http://localhost:3000/horarios?profissionalId=1
```

### Criar um novo paciente
```bash
curl -X POST http://localhost:3000/pacientes \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCompleto": "João da Silva",
    "cpf": "12345678900",
    "cartaoSus": "123456789012345",
    "dataNascimento": "1990-01-01",
    "telefone": "11999999999",
    "email": "joao@email.com",
    "endereco": "Rua Teste, 123",
    "municipio": "São Paulo",
    "uf": "SP",
    "cep": "01310000"
  }'
```

## 📚 Dados de Teste

O arquivo `db.json` contém:
- **10 especialidades** médicas
- **18 profissionais** distribuídos em diferentes UBS
- **28 horários** de atendimento
- **10 pacientes** cadastrados

Todos os dados são fictícios e gerados para fins de teste.

## 🎓 Observações

- Este é um ambiente de **desenvolvimento/teste**
- Em produção, estas APIs seriam substituídas por integrações reais com sistemas do SUS
- Os códigos CNES são fictícios e não correspondem a unidades reais
- Os dados de profissionais e pacientes são totalmente fictícios
