package br.gov.sus.sus.infrastructure.persistence.mapper;

import br.gov.sus.sus.domain.entity.Profissional;
import br.gov.sus.sus.infrastructure.client.dto.ProfissionalApiResponse;
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
    
    public ProfissionalApiResponse toApiResponse(Profissional domain) {
        if (domain == null) {
            return null;
        }
        
        ProfissionalApiResponse response = new ProfissionalApiResponse();
        response.setId(domain.getId());
        response.setNomeCompleto(domain.getNomeCompleto());
        response.setRegistroConselho(domain.getRegistroConselho());
        response.setCodigoCnesUnidade(domain.getCodigoCnesUnidade());
        
        return response;
    }
}
