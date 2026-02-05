package br.gov.sus.sus.application.dto.request;

import br.gov.sus.sus.domain.enums.TipoAtendimento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoRequest {
    
    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId;
    
    @NotNull(message = "ID do profissional é obrigatório")
    private Long profissionalId;
    
    @NotNull(message = "Código CNES da unidade de saúde é obrigatório")
    private String codigoCnesUnidade;
    
    @NotNull(message = "ID da especialidade é obrigatório")
    private Long especialidadeId;
    
    @NotNull(message = "Data e hora do agendamento são obrigatórios")
    @Future(message = "A data do agendamento deve ser futura")
    private LocalDateTime dataHoraAgendamento;
    
    @NotNull(message = "Tipo de atendimento é obrigatório")
    private TipoAtendimento tipoAtendimento;
    
    private String observacoes;
}
