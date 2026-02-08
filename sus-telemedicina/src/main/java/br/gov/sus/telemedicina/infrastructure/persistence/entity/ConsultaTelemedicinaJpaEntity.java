package br.gov.sus.telemedicina.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultas_telemedicina")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaTelemedicinaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long agendamentoId;

    @Column(nullable = false)
    private String zoomMeetingId;

    @Column(nullable = false, length = 500)
    private String zoomJoinUrl;

    @Column(length = 500)
    private String zoomStartUrl;

    @Column(nullable = false)
    private Boolean notificacaoEnviada;

    private LocalDateTime dataEnvioNotificacao;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @PrePersist
    public void prePersist() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
        if (this.notificacaoEnviada == null) {
            this.notificacaoEnviada = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}

