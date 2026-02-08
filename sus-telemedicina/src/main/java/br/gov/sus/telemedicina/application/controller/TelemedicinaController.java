package br.gov.sus.telemedicina.application.controller;

import br.gov.sus.telemedicina.application.dto.ConsultaTelemedicinaResponse;
import br.gov.sus.telemedicina.application.dto.CriarConsultaRequest;
import br.gov.sus.telemedicina.domain.service.TelemedicinaService;
import br.gov.sus.telemedicina.infrastructure.persistence.entity.ConsultaTelemedicinaJpaEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/telemedicina")
@RequiredArgsConstructor
public class TelemedicinaController {

    private final TelemedicinaService telemedicinaService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @PostMapping("/consultas")
    public ResponseEntity<ConsultaTelemedicinaResponse> criarConsulta(
            @Valid @RequestBody CriarConsultaRequest request) {

        log.info("Received request to create telemedicine consultation for appointment ID: {}",
                request.getAgendamentoId());

        try {
            ConsultaTelemedicinaJpaEntity consulta = telemedicinaService
                    .criarConsultaTelemedicina(request.getAgendamentoId());

            ConsultaTelemedicinaResponse response = mapToResponse(consulta);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating telemedicine consultation", e);
            throw new RuntimeException("Erro ao criar consulta de telemedicina: " + e.getMessage());
        }
    }

    @PostMapping("/consultas/{id}/notificar")
    public ResponseEntity<Void> enviarNotificacao(@PathVariable Long id) {
        log.info("Received request to send notification for consultation ID: {}", id);

        try {
            telemedicinaService.enviarNotificacaoWhatsApp(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error sending notification", e);
            throw new RuntimeException("Erro ao enviar notificação: " + e.getMessage());
        }
    }

    @PostMapping("/processar")
    public ResponseEntity<Void> processarAgendamentos(
            @RequestParam(defaultValue = "15") int antecedenciaMinutos) {

        log.info("Received request to process appointments with {} minutes advance", antecedenciaMinutos);

        try {
            telemedicinaService.processarAgendamentosParaNotificacao(antecedenciaMinutos);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing appointments", e);
            throw new RuntimeException("Erro ao processar agendamentos: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("SUS Telemedicina Service is running");
    }

    private ConsultaTelemedicinaResponse mapToResponse(ConsultaTelemedicinaJpaEntity consulta) {
        return ConsultaTelemedicinaResponse.builder()
                .id(consulta.getId())
                .agendamentoId(consulta.getAgendamentoId())
                .zoomMeetingId(consulta.getZoomMeetingId())
                .zoomJoinUrl(consulta.getZoomJoinUrl())
                .notificacaoEnviada(consulta.getNotificacaoEnviada())
                .dataEnvioNotificacao(consulta.getDataEnvioNotificacao() != null
                        ? consulta.getDataEnvioNotificacao().format(DATE_FORMATTER) : null)
                .dataCriacao(consulta.getDataCriacao() != null
                        ? consulta.getDataCriacao().format(DATE_FORMATTER) : null)
                .build();
    }
}

