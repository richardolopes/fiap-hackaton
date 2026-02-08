package br.gov.sus.telemedicina.infrastructure.scheduler;

import br.gov.sus.telemedicina.domain.service.TelemedicinaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class TelemedicinaScheduler {

    private final TelemedicinaService telemedicinaService;

    @Value("${scheduler.notification-advance-minutes:15}")
    private int notificationAdvanceMinutes;

    /**
     * Runs every 5 minutes to check for appointments that need notifications
     */
//    @Scheduled(fixedDelayString = "${scheduler.check-interval-minutes:5}000", initialDelay = 10000)
    public void verificarAgendamentos() {
        log.info("Starting scheduled check for telemedicine appointments");

        try {
            telemedicinaService.processarAgendamentosParaNotificacao(notificationAdvanceMinutes);
        } catch (Exception e) {
            log.error("Error in scheduled appointment check", e);
        }

        log.info("Finished scheduled check for telemedicine appointments");
    }
}

