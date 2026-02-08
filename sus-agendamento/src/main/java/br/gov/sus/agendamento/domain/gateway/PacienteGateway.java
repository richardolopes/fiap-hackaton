package br.gov.sus.agendamento.domain.gateway;

import br.gov.sus.shared.domain.entity.Paciente;

import java.util.Optional;

public interface PacienteGateway {

    Optional<Paciente> buscarPorId(Long id);
}
