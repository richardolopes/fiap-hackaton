package br.gov.sus.agendamento.infrastructure.persistence.mapper;

import br.gov.sus.agendamento.domain.entity.Profissional;
import br.gov.sus.agendamento.infrastructure.client.dto.ProfissionalApiResponse;
import org.springframework.stereotype.Component;

@Component
public class ProfissionalMapper {

    private final EspecialidadeMapper especialidadeMapper;

    public ProfissionalMapper(EspecialidadeMapper especialidadeMapper) {
        this.especialidadeMapper = especialidadeMapper;
    }

    public Profissional toDomain(ProfissionalApiResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }

        return Profissional.builder()
                .id(apiResponse.getId())
                .nomeCompleto(apiResponse.getNomeCompleto())
                .registroConselho(apiResponse.getRegistroConselho())
                .codigoCnesUnidade(apiResponse.getCodigoCnesUnidade())
                .build();
    }
}
