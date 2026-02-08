package br.gov.sus.telemedicina.infrastructure.client;

import br.gov.sus.telemedicina.infrastructure.client.dto.PacienteResponse;
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
            // Return mock data in case of error
            return PacienteResponse.builder()
                    .id(pacienteId)
                    .nome("Paciente " + pacienteId)
                    .telefone("+5511999999999")
                    .build();
        }
    }
}

