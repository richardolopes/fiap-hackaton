package br.gov.sus.agendamento.application.dto.response;

import br.gov.sus.agendamento.domain.enums.StatusAgendamento;
import br.gov.sus.agendamento.domain.enums.TipoAtendimento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoResponse {

    private Long id;
    private String nomePaciente;
    private String cpfPaciente;
    private String cartaoSusPaciente;
    private String nomeProfissional;
    private String registroConselhoProfissional;
    private String nomeUnidadeSaude;
    private String enderecoUnidadeSaude;
    private String nomeEspecialidade;
    private LocalDateTime dataHoraAgendamento;
    private StatusAgendamento status;
    private TipoAtendimento tipoAtendimento;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private String motivoCancelamento;
}
