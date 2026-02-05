package br.gov.sus.sus.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivelResponse {
    
    private LocalDateTime dataHora;
    private Long profissionalId;
    private String nomeProfissional;
    private String codigoCnesUnidade;
    private String nomeUnidadeSaude;
    private Long especialidadeId;
    private String nomeEspecialidade;
    private Boolean disponivel;
}
