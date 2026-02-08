package br.gov.sus.agendamento.domain.gateway;

import br.gov.sus.agendamento.domain.entity.Profissional;

import java.util.List;
import java.util.Optional;

/**
 * Gateway para acesso a dados de Profissionais.
 * Dados obtidos do json-server (mock externo).
 */
public interface ProfissionalGateway {

    Optional<Profissional> buscarPorId(Long id);

    List<Profissional> buscarPorEspecialidadeId(Long especialidadeId);
}
