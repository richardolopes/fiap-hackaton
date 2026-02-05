# 🏥 Guia de Testes - Sistema de Agendamento SUS

Este documento explica como testar o sistema de agendamento do SUS que encontra a UBS mais próxima do paciente baseada no CEP.

## 📋 Índice

1. [Pré-requisitos](#pré-requisitos)
2. [Arquitetura](#arquitetura)
3. [Iniciando o Sistema](#iniciando-o-sistema)
4. [Endpoints Disponíveis](#endpoints-disponíveis)
5. [Exemplos de Teste com cURL](#exemplos-de-teste-com-curl)
6. [Dados de Teste](#dados-de-teste)
7. [Troubleshooting](#troubleshooting)

---

## 🔧 Pré-requisitos

- **Java 21** instalado
- **Node.js** (para o json-server mock)
- **cURL** ou **Postman** para testar os endpoints

Verificar instalação:
```bash
java -version    # Deve mostrar Java 21
node -v          # Deve mostrar versão do Node.js
```

---

## 🏗️ Arquitetura

O sistema utiliza:

| Componente | Descrição | Porta |
|------------|-----------|-------|
| **Spring Boot App** | Aplicação principal | `8080` |
| **json-server (mock)** | Simula dados de pacientes, profissionais, especialidades | `3000` |
| **API Real do SUS** | Dados reais de UBS via CNES | Externa |
| **ViaCEP** | Conversão de CEP para código IBGE | Externa |
| **Nominatim (OpenStreetMap)** | Geocodificação de CEP para coordenadas | Externa |
| **H2 Database** | Banco em memória para agendamentos | Embarcado |

### Fluxo do Agendamento por CEP:

```
1. Recebe CEP do paciente
2. ViaCEP → obtém código IBGE do município
3. Nominatim → obtém coordenadas (lat/lon) do CEP
4. API CNES/SUS → busca todas as UBS do município
5. Cálculo Haversine → encontra UBS mais próxima
6. json-server → busca profissional disponível na UBS
7. Cria e persiste o agendamento
```

---

## 🚀 Iniciando o Sistema

### Terminal 1 - Mock Server (json-server)

```bash
cd sus/mock-server
npm install       # Apenas na primeira vez
npm start
```

O servidor mock estará disponível em: `http://localhost:3000`

### Terminal 2 - Aplicação Spring Boot

```bash
cd sus
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### Verificar se está funcionando:

```bash
# Verificar mock server
curl http://localhost:3000/pacientes | jq

# Verificar aplicação (deve retornar 404, mas significa que está rodando)
curl -I http://localhost:8080/api/v1/agendamentos/1
```

---

## 📡 Endpoints Disponíveis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/agendamentos/por-cep` | Criar agendamento buscando UBS mais próxima |
| `GET` | `/api/v1/agendamentos/{id}` | Buscar agendamento por ID |

---

## 🧪 Exemplos de Teste com cURL

### 1. Criar Agendamento por CEP (São Paulo - Itaquera)

```bash
curl -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "cep": "03694050",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-27T10:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta de rotina"
  }' | jq
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nomePaciente": "José da Silva",
  "cpfPaciente": "12345678901",
  "cartaoSusPaciente": "123456789012345",
  "nomeProfissional": "Dr. João Silva",
  "registroConselhoProfissional": "CRM-SP 123456",
  "nomeUnidadeSaude": "UBS ANTONIO ESTEVAO DE CARVALHO",
  "enderecoUnidadeSaude": "RUA JAPANI, 7",
  "nomeEspecialidade": "Clínica Geral",
  "dataHoraAgendamento": "2026-02-27T10:00:00",
  "status": "AGENDADO",
  "tipoAtendimento": "PRESENCIAL",
  "observacoes": "Consulta de rotina",
  "dataCriacao": "2026-02-05T20:18:55.307059794"
}
```

### 2. Criar Agendamento - Zona Leste SP (Itaquera)

```bash
curl -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 2,
    "cep": "03694050",
    "especialidadeId": 2,
    "dataHoraAgendamento": "2026-02-21T14:30:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta pediátrica"
  }' | jq
```

### 3. Criar Agendamento - Zona Sul SP (Santo Amaro)

```bash
curl -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 3,
    "cep": "04714000",
    "especialidadeId": 2,
    "dataHoraAgendamento": "2026-02-22T09:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta infantil"
  }' | jq
```

### 4. Criar Agendamento - Rio de Janeiro (Copacabana)

```bash
curl -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 4,
    "cep": "22041080",
    "especialidadeId": 3,
    "dataHoraAgendamento": "2026-02-23T11:00:00",
    "tipoAtendimento": "TELEMEDICINA",
    "observacoes": "Consulta cardiológica online"
  }' | jq
```

### 5. Criar Agendamento - Belo Horizonte (Savassi)

```bash
curl -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 5,
    "cep": "30130000",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-24T08:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Check-up geral"
  }' | jq
```

### 6. Buscar Agendamento por ID

```bash
# Buscar agendamento criado (substitua o ID)
curl http://localhost:8080/api/v1/agendamentos/1 | jq
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nomePaciente": "José da Silva",
  "cpfPaciente": "12345678901",
  "cartaoSusPaciente": "123456789012345",
  "nomeProfissional": "Dr. João Silva",
  "registroConselhoProfissional": "CRM-SP 123456",
  "nomeUnidadeSaude": "UBS ANTONIO ESTEVAO DE CARVALHO",
  "enderecoUnidadeSaude": "RUA JAPANI, 7",
  "nomeEspecialidade": "Clínica Geral",
  "dataHoraAgendamento": "2026-02-20T10:00:00",
  "status": "AGENDADO",
  "tipoAtendimento": "PRESENCIAL",
  "observacoes": "Consulta de rotina",
  "dataCriacao": "2026-02-05T20:18:55.307059794"
}
```

### 7. Buscar Agendamento Inexistente (Teste de erro)

```bash
curl -i http://localhost:8080/api/v1/agendamentos/99999
```

**Resposta esperada (400):**
```json
{
  "status": 400,
  "message": "Agendamento não encontrado",
  "timestamp": "2026-02-05T20:14:58.203249293"
}
```

---

## 📊 Dados de Teste Disponíveis

### Pacientes (mock-server)

| ID | Nome | CPF | CEP |
|----|------|-----|-----|
| 1 | José da Silva | 12345678901 | 01310100 |
| 2 | Maria Oliveira | 23456789012 | 01310200 |
| 3 | Carlos Santos | 34567890123 | 01310300 |
| 4 | Ana Costa | 45678901234 | 01310400 |
| 5 | Pedro Ferreira | 56789012345 | 01310500 |

### Especialidades (mock-server)

| ID | Nome | Tempo Consulta |
|----|------|----------------|
| 1 | Clínica Geral | 30 min |
| 2 | Pediatria | 30 min |
| 3 | Cardiologia | 45 min |
| 4 | Dermatologia | 30 min |
| 5 | Ginecologia | 30 min |
| 6 | Ortopedia | 30 min |
| 7 | Oftalmologia | 30 min |
| 8 | Neurologia | 45 min |
| 9 | Psiquiatria | 60 min |
| 10 | Endocrinologia | 30 min |

### Tipos de Atendimento

- `PRESENCIAL` - Atendimento presencial na unidade
- `TELEMEDICINA` - Atendimento via telemedicina

### CEPs para Teste

| Cidade | Bairro | CEP |
|--------|--------|-----|
| São Paulo | Centro (Av. Paulista) | 01310100 |
| São Paulo | Itaquera | 03694050 |
| São Paulo | Santo Amaro | 04714000 |
| São Paulo | Pinheiros | 05422000 |
| Rio de Janeiro | Copacabana | 22041080 |
| Rio de Janeiro | Centro | 20040020 |
| Belo Horizonte | Savassi | 30130000 |
| Curitiba | Centro | 80010000 |

---

## 🔍 Verificando os Dados do Mock Server

```bash
# Listar todos os pacientes
curl http://localhost:3000/pacientes | jq

# Listar todas as especialidades
curl http://localhost:3000/especialidades | jq

# Listar todos os profissionais
curl http://localhost:3000/profissionais | jq

# Buscar profissionais de uma especialidade específica
curl "http://localhost:3000/profissionais?especialidadeId=1" | jq

# Buscar paciente por ID
curl http://localhost:3000/pacientes/1 | jq
```

---

## ✅ Exemplo de Resposta de Sucesso

```json
{
  "id": 1,
  "nomePaciente": "José da Silva",
  "cpfPaciente": "12345678901",
  "cartaoSusPaciente": "123456789012345",
  "nomeProfissional": "Dr. João Silva",
  "registroConselhoProfissional": "CRM-SP 123456",
  "nomeUnidadeSaude": "UBS VILA MARIA STELLA",
  "enderecoUnidadeSaude": "RUA GASTAO VIDIGAL, 135 - VILA MARIA ALTA",
  "nomeEspecialidade": "Clínica Geral",
  "dataHoraAgendamento": "2026-02-20T10:00:00",
  "status": "AGENDADO",
  "tipoAtendimento": "CONSULTA",
  "observacoes": "Consulta de rotina",
  "dataCriacao": "2026-02-05T19:45:00"
}
```

---

## 🛠️ Troubleshooting

### Erro: "Port 8080 already in use"

```bash
# Encontrar e matar processo na porta 8080
lsof -i :8080
kill -9 <PID>
```

### Erro: "Port 3000 already in use"

```bash
# Encontrar e matar processo na porta 3000
lsof -i :3000
kill -9 <PID>
```

### Erro: "Paciente não encontrado"

Verifique se o `pacienteId` existe no mock-server:
```bash
curl http://localhost:3000/pacientes | jq '.[].id'
```

### Erro: "Nenhuma UBS encontrada"

- Verifique se o CEP é válido
- Alguns municípios podem não ter UBS cadastradas na API do SUS

### Erro: "Connection refused" no mock-server

```bash
# Certifique-se que o json-server está rodando
cd sus/mock-server
npm start
```

### Logs da Aplicação

Os logs mostram informações úteis sobre:
- CEP sendo processado
- Código IBGE do município
- Coordenadas obtidas
- Quantidade de UBS encontradas
- UBS mais próxima e distância

---

## 📝 Script de Teste Completo

Salve como `test.sh` e execute:

```bash
#!/bin/bash

echo "=== Testando Sistema de Agendamento SUS ==="
echo ""

# Teste 1: Criar agendamento
echo "1. Criando agendamento para CEP 03694050 (Itaquera-SP)..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "cep": "03694050",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-20T10:00:00",
    "tipoAtendimento": "CONSULTA",
    "observacoes": "Teste automatizado"
  }')

echo "$RESPONSE" | jq

# Extrair ID do agendamento
ID=$(echo "$RESPONSE" | jq -r '.id')
echo ""

# Teste 2: Buscar agendamento criado
echo "2. Buscando agendamento ID: $ID..."
curl -s http://localhost:8080/api/v1/agendamentos/$ID | jq

echo ""
echo "=== Testes concluídos ==="
```

Tornar executável e rodar:
```bash
chmod +x test.sh
./test.sh
```

---

## 🎯 Resumo

1. **Inicie o mock-server** (`npm start` na pasta `mock-server`)
2. **Inicie a aplicação** (`./mvnw spring-boot:run`)
3. **Teste com cURL** usando os exemplos acima
4. **Verifique os logs** para informações sobre a UBS encontrada

A aplicação encontrará automaticamente a UBS mais próxima do CEP informado usando dados reais da API do SUS! 🏥
