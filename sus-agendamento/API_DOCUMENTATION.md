# SUS Agendamento - Testes de API

## Pré-requisitos

Subir os containers com Docker Compose:

```bash
docker compose up --build -d
```

Aguardar todos os serviços ficarem saudáveis:

```bash
docker compose ps
```

---

## 1. Agendar Consulta por CEP

**Endpoint:** `POST /api/v1/agendamentos/por-cep`

Busca automaticamente a UBS mais próxima do CEP informado e agenda a consulta.

```bash
curl -s -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "cep": "01310100",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-13T10:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta de rotina"
  }'
```

**Campos do request:**

| Campo                 | Tipo           | Obrigatório | Descrição                                  |
|-----------------------|----------------|-------------|--------------------------------------------|
| `pacienteId`          | Long           | Sim         | ID do paciente                             |
| `cep`                 | String         | Sim         | CEP para localizar UBS próxima (8 dígitos) |
| `especialidadeId`     | Long           | Sim         | ID da especialidade médica                 |
| `dataHoraAgendamento` | LocalDateTime  | Sim         | Data/hora futura no formato ISO 8601       |
| `tipoAtendimento`     | Enum           | Sim         | `PRESENCIAL` ou `TELEMEDICINA`             |
| `observacoes`         | String         | Não         | Observações adicionais                     |

**Resposta esperada (201 Created):**

```json
{
    "id": 1,
    "nomePaciente": "José da Silva",
    "cpfPaciente": "12345678901",
    "cartaoSusPaciente": "123456789012345",
    "nomeProfissional": "Dr. João Silva",
    "registroConselhoProfissional": "CRM-SP 123456",
    "nomeUnidadeSaude": "UBS NOSSA SENHORA DO BRASIL ARMANDO DARIENZO",
    "enderecoUnidadeSaude": "RUA ALMIRANTE MARQUES DE LEAO, 684",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-02-09T10:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta de rotina",
    "dataCriacao": "2026-02-07T18:15:37.67439825",
    "motivoCancelamento": null
}
```

---

## 2. Agendar Múltiplos Pacientes no Mesmo Horário (Mesmo Especialidade)

Quando existem vários profissionais com a mesma especialidade, o sistema deve alocar automaticamente um profissional disponível. Se o primeiro já está ocupado no horário, deve atribuir o próximo.

### 2.1. Primeiro agendamento — profissional 1 disponível

```bash
curl -s -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "cep": "01310100",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Primeiro agendamento - deve alocar profissional 1"
  }'
```

**Resposta esperada (201 Created):**

```json
{
    "id": 1,
    "nomePaciente": "José da Silva",
    "nomeProfissional": "Dr. João Silva",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Primeiro agendamento - deve alocar profissional 1"
}
```

### 2.2. Segundo agendamento mesmo horário — deve alocar outro profissional

```bash
curl -s -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 2,
    "cep": "01310200",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Segundo agendamento mesmo horário - deve alocar profissional diferente"
  }'
```

**Resposta esperada (201 Created):**

O sistema deve retornar sucesso com um profissional **diferente** do primeiro agendamento, pois o Dr. João Silva já está ocupado às 14h.

```json
{
    "id": 2,
    "nomePaciente": "Maria Oliveira",
    "nomeProfissional": "Dra. Ana Rodrigues",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Segundo agendamento mesmo horário - deve alocar profissional diferente"
}
```

### 2.3. Terceiro agendamento mesmo horário — deve alocar mais um profissional

```bash
curl -s -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 3,
    "cep": "01310300",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Terceiro agendamento mesmo horário"
  }'
```d

**Resposta esperada (201 Created):**

Deve alocar um terceiro profissional disponível de Clínica Geral.

```json
{
    "id": 3,
    "nomePaciente": "Carlos Santos",
    "nomeProfissional": "Dr. Fernando Lima",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Terceiro agendamento mesmo horário"
}
```

### 2.4. Verificação — agendamento em horário diferente reutiliza o primeiro profissional

```bash
curl -s -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 4,
    "cep": "01310100",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-03-15T15:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Horário diferente - profissional 1 deve estar disponível novamente"
  }'
```

**Resposta esperada (201 Created):**

Como o horário é diferente (15h), o primeiro profissional (Dr. João Silva) está livre novamente.

```json
{
    "id": 4,
    "nomePaciente": "Ana Pereira",
    "nomeProfissional": "Dr. João Silva",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-03-15T15:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL"
}
```

### 2.5. Cancelamento libera horário do profissional

Cancelar o primeiro agendamento e verificar que o profissional fica disponível novamente:

```bash
# Cancelar o agendamento do profissional 1 às 14h
curl -s -X PATCH http://localhost:8080/api/v1/agendamentos/1/cancelar \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Paciente solicitou cancelamento"
  }'
```

**Resposta esperada (200 OK):**

```json
{
    "id": 1,
    "status": "CANCELADO_PACIENTE",
    "motivoCancelamento": "Paciente solicitou cancelamento"
}
```

Agora um novo agendamento às 14h deve poder usar o Dr. João Silva novamente:

```bash
curl -s -X POST http://localhost:8080/api/v1/agendamentos/por-cep \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 5,
    "cep": "01310100",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Após cancelamento - profissional 1 deve estar disponível"
  }'
```

**Resposta esperada (201 Created):**

```json
{
    "id": 5,
    "nomeProfissional": "Dr. João Silva",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-03-15T14:00:00",
    "status": "AGENDADO"
}
```

> **Nota:** A especialidade Clínica Geral (ID 1) possui 7 profissionais cadastrados (IDs: 1, 4, 7, 11, 13, 15, 17). O sistema alocará profissionais disponíveis na ordem retornada pela API até esgotar todos.

---

## 3. Buscar Agendamento por ID

**Endpoint:** `GET /api/v1/agendamentos/{id}`

```bash
curl -s http://localhost:8080/api/v1/agendamentos/1
```

**Resposta esperada (200 OK):**

```json
{
    "id": 1,
    "nomePaciente": "José da Silva",
    "cpfPaciente": "12345678901",
    "cartaoSusPaciente": "123456789012345",
    "nomeProfissional": "Dr. João Silva",
    "registroConselhoProfissional": "CRM-SP 123456",
    "nomeUnidadeSaude": "UBS NOSSA SENHORA DO BRASIL ARMANDO DARIENZO",
    "enderecoUnidadeSaude": "RUA ALMIRANTE MARQUES DE LEAO, 684",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-02-09T10:00:00",
    "status": "AGENDADO",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta de rotina",
    "dataCriacao": "2026-02-07T18:15:37.674398",
    "motivoCancelamento": null
}
```

---

## 4. Cancelar Agendamento

**Endpoint:** `PATCH /api/v1/agendamentos/{id}/cancelar`

```bash
curl -s -X PATCH http://localhost:8080/api/v1/agendamentos/1/cancelar \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Não poderei comparecer por motivo pessoal"
  }'
```

**Campos do request:**

| Campo   | Tipo   | Obrigatório | Descrição                  |
|---------|--------|-------------|----------------------------|
| `motivo`| String | Sim         | Motivo do cancelamento     |

**Resposta esperada (200 OK):**

```json
{
    "id": 1,
    "nomePaciente": "José da Silva",
    "cpfPaciente": "12345678901",
    "cartaoSusPaciente": "123456789012345",
    "nomeProfissional": "Dr. João Silva",
    "registroConselhoProfissional": "CRM-SP 123456",
    "nomeUnidadeSaude": "UBS NOSSA SENHORA DO BRASIL ARMANDO DARIENZO",
    "enderecoUnidadeSaude": "RUA ALMIRANTE MARQUES DE LEAO, 684",
    "nomeEspecialidade": "Clínica Geral",
    "dataHoraAgendamento": "2026-02-09T10:00:00",
    "status": "CANCELADO_PACIENTE",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta de rotina",
    "dataCriacao": "2026-02-07T18:15:37.674398",
    "motivoCancelamento": "Não poderei comparecer por motivo pessoal"
}
```

---

## Dados de Referência (Mock Server)

### Pacientes disponíveis (IDs 1-10)

| ID | Nome             | CPF          | CEP       | Município       |
|----|------------------|--------------|-----------|-----------------|
| 1  | José da Silva    | 12345678901  | 01310100  | São Paulo       |
| 2  | Maria Oliveira   | 23456789012  | 01310200  | São Paulo       |
| 3  | Carlos Santos    | 34567890123  | 01310300  | São Paulo       |
| 9  | Marcos Pereira   | 90123456789  | 20040020  | Rio de Janeiro  |
| 10 | Juliana Ribeiro  | 01234567890  | 30130000  | Belo Horizonte  |

### Especialidades disponíveis (IDs 1-10)

| ID | Nome            |
|----|-----------------|
| 1  | Clínica Geral   |
| 2  | Pediatria       |
| 3  | Cardiologia     |
| 4  | Dermatologia    |
| 5  | Ginecologia     |
| 6  | Ortopedia       |
| 7  | Oftalmologia    |
| 8  | Neurologia      |
| 9  | Psiquiatria     |
| 10 | Endocrinologia  |

### Tipos de Atendimento

- `PRESENCIAL`
- `TELEMEDICINA`
