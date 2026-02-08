# Schema do Banco de Dados - SUS Telemedicina

## Visão Geral

O sistema de telemedicina utiliza o mesmo banco de dados PostgreSQL do sistema de agendamentos, adicionando uma nova tabela para gerenciar as consultas virtuais.

## Tabelas

### 1. agendamentos (Existente)

Tabela criada pelo sistema de agendamentos.

```sql
CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    profissional_id BIGINT NOT NULL,
    codigo_cnes_unidade VARCHAR(20) NOT NULL,
    especialidade_id BIGINT NOT NULL,
    data_hora_agendamento TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    tipo_atendimento VARCHAR(50) NOT NULL,
    observacoes VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    motivo_cancelamento VARCHAR(500)
);
```

**Campos Relevantes para Telemedicina:**
- `tipo_atendimento`: Deve ser 'TELEMEDICINA' para consultas virtuais
- `status`: Valores possíveis: AGENDADO, CONFIRMADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO
- `data_hora_agendamento`: Data e hora da consulta

---

### 2. consultas_telemedicina (Nova)

Tabela criada pelo sistema de telemedicina.

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
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_agendamento FOREIGN KEY (agendamento_id) 
        REFERENCES agendamentos(id)
);

CREATE INDEX idx_consultas_agendamento_id 
    ON consultas_telemedicina(agendamento_id);
    
CREATE INDEX idx_consultas_notificacao 
    ON consultas_telemedicina(notificacao_enviada, data_envio_notificacao);
```

**Descrição dos Campos:**

| Campo | Tipo | Null | Descrição |
|-------|------|------|-----------|
| id | BIGSERIAL | NOT NULL | Identificador único da consulta |
| agendamento_id | BIGINT | NOT NULL | FK para agendamentos (único) |
| zoom_meeting_id | VARCHAR(255) | NOT NULL | ID da reunião no Zoom |
| zoom_join_url | VARCHAR(500) | NOT NULL | Link para participante entrar |
| zoom_start_url | VARCHAR(500) | NULL | Link para host iniciar (profissional) |
| notificacao_enviada | BOOLEAN | NOT NULL | Flag se WhatsApp foi enviado |
| data_envio_notificacao | TIMESTAMP | NULL | Data/hora do envio do WhatsApp |
| data_criacao | TIMESTAMP | NOT NULL | Data/hora de criação do registro |
| data_atualizacao | TIMESTAMP | NULL | Data/hora da última atualização |

---

## Relacionamentos

```
agendamentos (1) -----> (1) consultas_telemedicina
```

Um agendamento pode ter no máximo uma consulta de telemedicina associada (constraint UNIQUE).

---

## Queries Importantes

### Buscar Agendamentos para Notificar

```sql
SELECT a.* 
FROM agendamentos a
WHERE a.tipo_atendimento = 'TELEMEDICINA'
  AND a.status IN ('AGENDADO', 'CONFIRMADO')
  AND a.data_hora_agendamento BETWEEN 
      NOW() + INTERVAL '10 minutes' 
      AND NOW() + INTERVAL '20 minutes'
ORDER BY a.data_hora_agendamento ASC;
```

### Verificar Consultas sem Notificação

```sql
SELECT c.*, a.data_hora_agendamento
FROM consultas_telemedicina c
JOIN agendamentos a ON a.id = c.agendamento_id
WHERE c.notificacao_enviada = FALSE
  AND a.data_hora_agendamento > NOW()
ORDER BY a.data_hora_agendamento ASC;
```

### Estatísticas de Telemedicina

```sql
-- Total de consultas por status de notificação
SELECT 
    notificacao_enviada,
    COUNT(*) as total
FROM consultas_telemedicina
GROUP BY notificacao_enviada;

-- Consultas de telemedicina por dia
SELECT 
    DATE(a.data_hora_agendamento) as data,
    COUNT(*) as total_consultas
FROM agendamentos a
WHERE a.tipo_atendimento = 'TELEMEDICINA'
GROUP BY DATE(a.data_hora_agendamento)
ORDER BY data DESC;
```

---

## Migração (Flyway)

O sistema usa Flyway para gerenciar as migrações do banco de dados.

**Arquivo:** `src/main/resources/db/migration/V1__create_consultas_telemedicina.sql`

**Ordem de Execução:**
1. Sistema de agendamentos cria a tabela `agendamentos`
2. Sistema de telemedicina cria a tabela `consultas_telemedicina`
3. Flyway garante que as migrações sejam executadas apenas uma vez

**Versionamento:**
- V1: Criação inicial da tabela consultas_telemedicina
- Próximas versões: V2, V3, etc.

---

## Backup e Manutenção

### Backup das Tabelas

```bash
# Backup da tabela consultas_telemedicina
docker exec sus-postgres pg_dump -U sus -d susdb -t consultas_telemedicina > backup_telemedicina.sql

# Backup completo
docker exec sus-postgres pg_dump -U sus -d susdb > backup_completo.sql
```

### Restaurar Backup

```bash
docker exec -i sus-postgres psql -U sus -d susdb < backup_completo.sql
```

### Limpar Dados Antigos

```sql
-- Deletar consultas de agendamentos cancelados (manter histórico)
-- Não recomendado deletar, apenas para referência

-- Marcar consultas antigas como arquivadas (adicionar coluna se necessário)
-- ALTER TABLE consultas_telemedicina ADD COLUMN arquivada BOOLEAN DEFAULT FALSE;
```

---

## Índices e Performance

### Índices Criados

1. **idx_consultas_agendamento_id**: Melhora busca por agendamento_id
2. **idx_consultas_notificacao**: Otimiza busca de consultas sem notificação

### Recomendações

```sql
-- Analisar performance de queries
EXPLAIN ANALYZE 
SELECT * FROM consultas_telemedicina 
WHERE notificacao_enviada = FALSE;

-- Atualizar estatísticas
VACUUM ANALYZE consultas_telemedicina;
```

---

## Integridade Referencial

### Constraints

- **PRIMARY KEY (id)**: Garante unicidade do ID
- **UNIQUE (agendamento_id)**: Apenas uma consulta por agendamento
- **FOREIGN KEY (agendamento_id)**: Referência válida para agendamentos
- **NOT NULL**: Campos obrigatórios

### Comportamento ao Deletar

```sql
-- Se um agendamento for deletado, a constraint FK vai impedir
-- Recomenda-se usar ON DELETE CASCADE ou tratar no código
-- Atualmente: erro será lançado se tentar deletar agendamento com consulta
```

---

## Monitoramento

### Queries Úteis para Monitoramento

```sql
-- Total de consultas hoje
SELECT COUNT(*) 
FROM consultas_telemedicina 
WHERE DATE(data_criacao) = CURRENT_DATE;

-- Taxa de envio de notificações
SELECT 
    ROUND(
        COUNT(CASE WHEN notificacao_enviada THEN 1 END) * 100.0 / COUNT(*), 
        2
    ) as taxa_envio_percent
FROM consultas_telemedicina;

-- Consultas criadas mas sem notificação (pode indicar problema)
SELECT COUNT(*) as consultas_sem_notificacao
FROM consultas_telemedicina c
JOIN agendamentos a ON a.id = c.agendamento_id
WHERE c.notificacao_enviada = FALSE
  AND a.data_hora_agendamento < NOW();
```

---

## Conexão com o Banco

### Via Docker

```bash
docker exec -it sus-postgres psql -U sus -d susdb
```

### Via Aplicação

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/susdb
    username: sus
    password: sus123
```

---

## Diagrama ER

```
┌─────────────────────┐
│    agendamentos     │
├─────────────────────┤
│ id (PK)             │
│ paciente_id         │
│ profissional_id     │
│ tipo_atendimento    │◄────┐
│ status              │     │
│ data_hora_agend.    │     │ 1:1
│ ...                 │     │
└─────────────────────┘     │
                            │
                            │
            ┌───────────────────────────┐
            │ consultas_telemedicina    │
            ├───────────────────────────┤
            │ id (PK)                   │
            │ agendamento_id (FK,UNIQUE)│
            │ zoom_meeting_id           │
            │ zoom_join_url             │
            │ notificacao_enviada       │
            │ data_envio_notificacao    │
            │ ...                       │
            └───────────────────────────┘
```

---

## Considerações de Segurança

1. **Dados Sensíveis**: URLs do Zoom contêm tokens - não expor em logs
2. **Acesso ao Banco**: Usar usuários com privilégios mínimos em produção
3. **Backup**: Implementar backup automático diário
4. **Auditoria**: Tabela mantém histórico com timestamps
5. **GDPR/LGPD**: Considerar retenção de dados e anonimização

---

Para mais informações, consulte a documentação do PostgreSQL: https://www.postgresql.org/docs/

