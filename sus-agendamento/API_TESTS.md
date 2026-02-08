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
    "dataHoraAgendamento": "2026-02-09T10:00:00",
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

## 2. Buscar Agendamento por ID

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

## 3. Cancelar Agendamento

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
