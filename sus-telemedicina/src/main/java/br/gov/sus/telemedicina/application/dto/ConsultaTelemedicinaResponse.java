package br.gov.sus.telemedicina.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaTelemedicinaResponse {
    private Long id;
    private Long agendamentoId;
    private String zoomMeetingId;
    private String zoomJoinUrl;
    private Boolean notificacaoEnviada;
    private String dataEnvioNotificacao;
    private String dataCriacao;
}

