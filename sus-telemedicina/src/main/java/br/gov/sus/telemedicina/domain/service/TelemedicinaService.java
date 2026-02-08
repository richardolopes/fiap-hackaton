package br.gov.sus.telemedicina.domain.service;

import br.gov.sus.shared.domain.enums.StatusAgendamento;
import br.gov.sus.shared.domain.enums.TipoAtendimento;
import br.gov.sus.telemedicina.infrastructure.client.MockSusApiClient;
import br.gov.sus.telemedicina.infrastructure.client.WhatsAppClient;
import br.gov.sus.telemedicina.infrastructure.client.ZoomClient;
import br.gov.sus.telemedicina.infrastructure.client.dto.PacienteResponse;
import br.gov.sus.telemedicina.infrastructure.client.dto.ZoomMeetingResponse;
import br.gov.sus.telemedicina.infrastructure.persistence.entity.AgendamentoJpaEntity;
import br.gov.sus.telemedicina.infrastructure.persistence.entity.ConsultaTelemedicinaJpaEntity;
import br.gov.sus.telemedicina.infrastructure.persistence.repository.AgendamentoRepository;
import br.gov.sus.telemedicina.infrastructure.persistence.repository.ConsultaTelemedicinaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemedicinaService {

    private final AgendamentoRepository agendamentoRepository;
    private final ConsultaTelemedicinaRepository consultaRepository;
    private final ZoomClient zoomClient;
    private final WhatsAppClient whatsAppClient;
    private final MockSusApiClient mockSusApiClient;

    @Transactional
    public ConsultaTelemedicinaJpaEntity criarConsultaTelemedicina(Long agendamentoId) {
        log.info("Creating telemedicine consultation for appointment ID: {}", agendamentoId);

        // Check if consultation already exists
        if (consultaRepository.existsByAgendamentoId(agendamentoId)) {
            log.info("Consultation already exists for appointment ID: {}", agendamentoId);
            return consultaRepository.findByAgendamentoId(agendamentoId)
                    .orElseThrow(() -> new RuntimeException("Consultation not found"));
        }

        // Get appointment
        AgendamentoJpaEntity agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + agendamentoId));

        // Validate appointment type
        if (agendamento.getTipoAtendimento() != TipoAtendimento.TELEMEDICINA) {
            throw new RuntimeException("Appointment is not for telemedicine");
        }

        // Create Zoom meeting
        String meetingTopic = String.format("Consulta SUS - Paciente ID: %d", agendamento.getPacienteId());
        int durationMinutes = 60; // Default duration

        ZoomMeetingResponse zoomMeeting = zoomClient.createMeeting(
                meetingTopic,
                agendamento.getDataHoraAgendamento(),
                durationMinutes
        );

        // Save consultation
        ConsultaTelemedicinaJpaEntity consulta = ConsultaTelemedicinaJpaEntity.builder()
                .agendamentoId(agendamentoId)
                .zoomMeetingId(zoomMeeting.getId())
                .zoomJoinUrl(zoomMeeting.getJoinUrl())
                .zoomStartUrl(zoomMeeting.getStartUrl())
                .notificacaoEnviada(false)
                .build();

        consulta = consultaRepository.save(consulta);
        log.info("Telemedicine consultation created successfully. ID: {}", consulta.getId());

        return consulta;
    }

    @Transactional
    public void enviarNotificacaoWhatsApp(Long consultaId) {
        log.info("Sending WhatsApp notification for consultation ID: {}", consultaId);

        ConsultaTelemedicinaJpaEntity consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consultation not found: " + consultaId));

        if (consulta.getNotificacaoEnviada()) {
            log.info("Notification already sent for consultation ID: {}", consultaId);
            return;
        }

        AgendamentoJpaEntity agendamento = agendamentoRepository.findById(consulta.getAgendamentoId())
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + consulta.getAgendamentoId()));

        // Get patient data
        PacienteResponse paciente = mockSusApiClient.getPaciente(agendamento.getPacienteId());

        if (paciente.getTelefone() == null || paciente.getTelefone().isEmpty()) {
            log.warn("Patient has no phone number. Consultation ID: {}", consultaId);
            return;
        }

        // Format appointment time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String appointmentTime = agendamento.getDataHoraAgendamento().format(formatter);

        // Send WhatsApp notification
        whatsAppClient.sendConsultationNotification(
                "+5511965147758", // paciente.getTelefone()
                paciente.getNomeCompleto(),
                "Profissional Dr(a).: " + mockSusApiClient.getProfissional(agendamento.getProfissionalId()),
                appointmentTime,
                consulta.getZoomJoinUrl()
        );

        // Update notification status
        consulta.setNotificacaoEnviada(true);
        consulta.setDataEnvioNotificacao(LocalDateTime.now());
        consultaRepository.save(consulta);

        log.info("WhatsApp notification sent successfully for consultation ID: {}", consultaId);
    }

    public List<AgendamentoJpaEntity> buscarAgendamentosParaNotificar(int antecedenciaMinutos) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataInicio = agora.plusMinutes(antecedenciaMinutos - 5);
        LocalDateTime dataFim = agora.plusMinutes(antecedenciaMinutos + 5);

        List<StatusAgendamento> statusValidos = List.of(
                StatusAgendamento.AGENDADO,
                StatusAgendamento.CONFIRMADO
        );

        return agendamentoRepository.findAgendamentosParaTelemedicina(
                TipoAtendimento.TELEMEDICINA,
                statusValidos,
                dataInicio,
                dataFim
        );
    }

    @Transactional
    public void processarAgendamentosParaNotificacao(int antecedenciaMinutos) {
        log.info("Processing appointments for notification. Advance time: {} minutes", antecedenciaMinutos);

        List<AgendamentoJpaEntity> agendamentos = buscarAgendamentosParaNotificar(antecedenciaMinutos);

        log.info("Found {} appointments to process", agendamentos.size());

        for (AgendamentoJpaEntity agendamento : agendamentos) {
            try {
                // Create consultation if not exists
                ConsultaTelemedicinaJpaEntity consulta;
                if (consultaRepository.existsByAgendamentoId(agendamento.getId())) {
                    consulta = consultaRepository.findByAgendamentoId(agendamento.getId())
                            .orElseThrow();
                } else {
                    consulta = criarConsultaTelemedicina(agendamento.getId());
                }

                // Send notification if not sent
                if (!consulta.getNotificacaoEnviada()) {
                    enviarNotificacaoWhatsApp(consulta.getId());
                }

            } catch (Exception e) {
                log.error("Error processing appointment ID: {}", agendamento.getId(), e);
            }
        }

        log.info("Finished processing appointments");
    }
}

