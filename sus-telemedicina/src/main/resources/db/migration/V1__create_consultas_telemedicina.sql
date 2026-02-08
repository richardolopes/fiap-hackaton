-- Create consultas_telemedicina table
CREATE TABLE IF NOT EXISTS consultas_telemedicina (
    id BIGSERIAL PRIMARY KEY,
    agendamento_id BIGINT NOT NULL UNIQUE,
    zoom_meeting_id VARCHAR(255) NOT NULL,
    zoom_join_url VARCHAR(500) NOT NULL,
    zoom_start_url VARCHAR(500),
    notificacao_enviada BOOLEAN NOT NULL DEFAULT FALSE,
    data_envio_notificacao TIMESTAMP,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_agendamento FOREIGN KEY (agendamento_id) REFERENCES agendamentos(id)
);

-- Create index for better query performance
CREATE INDEX idx_consultas_agendamento_id ON consultas_telemedicina(agendamento_id);
CREATE INDEX idx_consultas_notificacao ON consultas_telemedicina(notificacao_enviada, data_envio_notificacao);

