package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.PacienteApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pacienteClient", url = "${api.mock-server.url}")
public interface PacienteClient {

    @GetMapping("/pacientes/{id}")
    PacienteApiResponse buscarPorId(@PathVariable("id") Long id);
}
