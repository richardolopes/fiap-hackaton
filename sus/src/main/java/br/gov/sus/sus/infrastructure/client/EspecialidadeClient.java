package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.EspecialidadeApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "especialidadeClient", url = "${api.mock-server.url}")
public interface EspecialidadeClient {

    @GetMapping("/especialidades")
    List<EspecialidadeApiResponse> buscarTodas();

    @GetMapping("/especialidades/{id}")
    EspecialidadeApiResponse buscarPorId(@PathVariable("id") Long id);

    @GetMapping("/especialidades")
    List<EspecialidadeApiResponse> buscarPorAtivo(@RequestParam("ativo") boolean ativo);
}
