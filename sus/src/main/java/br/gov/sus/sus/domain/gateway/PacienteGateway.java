package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.Paciente;

import java.util.Optional;

public interface PacienteGateway {
    
    Optional<Paciente> buscarPorId(Long id);
}
