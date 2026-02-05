package br.gov.sus.sus.infrastructure.persistence.mapper;

import br.gov.sus.sus.domain.entity.Paciente;
import br.gov.sus.sus.infrastructure.client.dto.PacienteApiResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PacienteMapper {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    
    public Paciente toDomain(PacienteApiResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }
        
        return Paciente.builder()
                .id(apiResponse.getId())
                .nomeCompleto(apiResponse.getNomeCompleto())
                .cpf(apiResponse.getCpf())
                .cartaoSus(apiResponse.getCartaoSus())
                .dataNascimento(LocalDate.parse(apiResponse.getDataNascimento(), DATE_FORMATTER))
                .telefone(apiResponse.getTelefone())
                .email(apiResponse.getEmail())
                .endereco(apiResponse.getEndereco())
                .municipio(apiResponse.getMunicipio())
                .uf(apiResponse.getUf())
                .cep(apiResponse.getCep())
                .build();
    }
    
    public PacienteApiResponse toApiResponse(Paciente domain) {
        if (domain == null) {
            return null;
        }
        
        PacienteApiResponse response = new PacienteApiResponse();
        response.setId(domain.getId());
        response.setNomeCompleto(domain.getNomeCompleto());
        response.setCpf(domain.getCpf());
        response.setCartaoSus(domain.getCartaoSus());
        response.setDataNascimento(domain.getDataNascimento().format(DATE_FORMATTER));
        response.setTelefone(domain.getTelefone());
        response.setEmail(domain.getEmail());
        response.setEndereco(domain.getEndereco());
        response.setMunicipio(domain.getMunicipio());
        response.setUf(domain.getUf());
        response.setCep(domain.getCep());
        
        return response;
    }
}
