package br.gov.sus.telemedicina.infrastructure.client;

import br.gov.sus.telemedicina.infrastructure.client.dto.PacienteResponse;
import br.gov.sus.telemedicina.infrastructure.client.dto.ProfissionalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class MockSusApiClient {

    private final WebClient webClient;

    public MockSusApiClient(@Value("${mock.sus.api.url:http://localhost:3000}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public PacienteResponse getPaciente(Long pacienteId) {
        try {
            log.info("Fetching patient data for ID: {}", pacienteId);
            return webClient.get()
                    .uri("/pacientes/{id}", pacienteId)
                    .retrieve()
                    .bodyToMono(PacienteResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching patient data", e);
            throw e;
        }
    }

    public ProfissionalResponse getProfissional(Long profissionalId) {
        try {
            log.info("Fetching profissional data for ID: {}", profissionalId);
            return webClient.get()
                    .uri("/pacientes/{id}", profissionalId)
                    .retrieve()
                    .bodyToMono(ProfissionalResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching patient data", e);
            throw e;
        }
    }
}

