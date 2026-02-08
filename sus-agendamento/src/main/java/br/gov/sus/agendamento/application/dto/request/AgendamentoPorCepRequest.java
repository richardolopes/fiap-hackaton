package br.gov.sus.agendamento.application.dto.request;

import br.gov.sus.shared.domain.enums.TipoAtendimento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request para agendamento usando CEP para localizar UBS mais próxima
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoPorCepRequest {

    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}|\\d{5}-\\d{3}", message = "CEP deve ter formato válido (12345678 ou 12345-678)")
    private String cep;

    @NotNull(message = "ID da especialidade é obrigatório")
    private Long especialidadeId;

    @NotNull(message = "Data e hora do agendamento são obrigatórios")
    @Future(message = "A data do agendamento deve ser futura")
    private LocalDateTime dataHoraAgendamento;

    @NotNull(message = "Tipo de atendimento é obrigatório")
    private TipoAtendimento tipoAtendimento;

    private String observacoes;
}
