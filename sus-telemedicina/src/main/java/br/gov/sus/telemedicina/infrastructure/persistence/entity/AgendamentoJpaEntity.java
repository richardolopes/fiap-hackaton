package br.gov.sus.telemedicina.infrastructure.persistence.entity;

import br.gov.sus.telemedicina.domain.enums.StatusAgendamento;
import br.gov.sus.telemedicina.domain.enums.TipoAtendimento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long profissionalId;

    @Column(nullable = false, length = 20)
    private String codigoCnesUnidade;

    @Column(nullable = false)
    private Long especialidadeId;

    @Column(nullable = false)
    private LocalDateTime dataHoraAgendamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAtendimento tipoAtendimento;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @Column(length = 500)
    private String motivoCancelamento;

    @PrePersist
    public void prePersist() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusAgendamento.AGENDADO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}

