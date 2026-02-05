package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.HorarioApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "horarioClient", url = "${api.mock-server.url}")
public interface HorarioClient {

    @GetMapping("/horarios")
    List<HorarioApiResponse> buscarTodos();

    @GetMapping("/horarios/{id}")
    HorarioApiResponse buscarPorId(@PathVariable("id") Long id);

    @GetMapping("/horarios")
    List<HorarioApiResponse> buscarPorProfissional(@RequestParam("profissionalId") Long profissionalId);

    @GetMapping("/horarios")
    List<HorarioApiResponse> buscarPorProfissionalEDia(
            @RequestParam("profissionalId") Long profissionalId,
            @RequestParam("diaSemana") String diaSemana);

    @GetMapping("/horarios")
    List<HorarioApiResponse> buscarPorAtivo(@RequestParam("ativo") boolean ativo);
}
