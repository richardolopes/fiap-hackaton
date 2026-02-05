package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.PacienteApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "pacienteClient", url = "${api.mock-server.url}")
public interface PacienteClient {

    @GetMapping("/pacientes")
    List<PacienteApiResponse> buscarTodos();

    @GetMapping("/pacientes/{id}")
    PacienteApiResponse buscarPorId(@PathVariable("id") Long id);

    @GetMapping("/pacientes")
    List<PacienteApiResponse> buscarPorCpf(@RequestParam("cpf") String cpf);

    @GetMapping("/pacientes")
    List<PacienteApiResponse> buscarPorCartaoSus(@RequestParam("cartaoSus") String cartaoSus);

    @GetMapping("/pacientes")
    List<PacienteApiResponse> buscarPorMunicipio(@RequestParam("municipio") String municipio);

    @PostMapping("/pacientes")
    PacienteApiResponse criar(@RequestBody PacienteApiResponse paciente);

    @PutMapping("/pacientes/{id}")
    PacienteApiResponse atualizar(@PathVariable("id") Long id, @RequestBody PacienteApiResponse paciente);

    @DeleteMapping("/pacientes/{id}")
    void deletar(@PathVariable("id") Long id);
}
