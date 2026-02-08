package br.gov.sus.telemedicina.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarConsultaRequest {

    @NotNull(message = "O ID do agendamento é obrigatório")
    private Long agendamentoId;
}

