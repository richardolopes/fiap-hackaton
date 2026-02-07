package br.gov.sus.sus.infrastructure.persistence.mapper;

import br.gov.sus.sus.domain.entity.Especialidade;
import br.gov.sus.sus.infrastructure.client.dto.EspecialidadeApiResponse;
import org.springframework.stereotype.Component;

@Component
public class EspecialidadeMapper {

    public Especialidade toDomain(EspecialidadeApiResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }

        return Especialidade.builder()
                .id(apiResponse.getId())
                .nome(apiResponse.getNome())
                .descricao(apiResponse.getDescricao())
                .tempoConsultaMinutos(apiResponse.getTempoConsultaMinutos())
                .build();
    }
}
