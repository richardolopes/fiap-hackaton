package br.gov.sus.agendamento.infrastructure.client;

import br.gov.sus.agendamento.infrastructure.client.dto.EspecialidadeApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "especialidadeClient", url = "${api.mock-sus-api.url}")
public interface EspecialidadeClient {

    @GetMapping("/especialidades/{id}")
    EspecialidadeApiResponse buscarPorId(@PathVariable("id") Long id);
}
