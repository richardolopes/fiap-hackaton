package br.gov.sus.telemedicina.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponse {
    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String cartaoSus;
    private String dataNascimento;
    private String telefone;
    private String email;
    private String endereco;
    private String municipio;
    private String uf;
    private String cep;
}

