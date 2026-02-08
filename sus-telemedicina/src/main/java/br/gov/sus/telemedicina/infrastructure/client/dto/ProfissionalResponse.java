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
    private String nome;
    private String cpf;
    private String crm;
    private String especialidade;
    private String telefone;
    private String email;
}

