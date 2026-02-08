package br.gov.sus.telemedicina.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalResponse {
    private Long id;
    private String nomeCompleto;
    private String registroConselho;
    private String especialidadeId;
    private String codigoCnesUnidade;
    private String ativo;
}

