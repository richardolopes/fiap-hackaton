package br.gov.sus.agendamento.infrastructure.client;

import br.gov.sus.agendamento.infrastructure.client.dto.PacienteApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacienteClient", url = "${api.mock-sus-api.url}")
public interface PacienteClient {

    @GetMapping("/pacientes/{id}")
    PacienteApiResponse buscarPorId(@PathVariable("id") Long id);
}
