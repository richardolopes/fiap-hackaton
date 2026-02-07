package br.gov.sus.sus.domain.entity;

import br.gov.sus.sus.domain.enums.StatusAgendamento;
import br.gov.sus.sus.domain.enums.TipoAtendimento;

import java.time.LocalDateTime;

public class Agendamento {

    private Long id;
    private Paciente paciente;
    private Profissional profissional;
    private UnidadeSaude unidadeSaude;
    private Especialidade especialidade;
    private LocalDateTime dataHoraAgendamento;
    private StatusAgendamento status;
    private TipoAtendimento tipoAtendimento;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String motivoCancelamento;

    public Agendamento() {
    }

    public Agendamento(Long id, Paciente paciente, Profissional profissional,
                       UnidadeSaude unidadeSaude, Especialidade especialidade,
                       LocalDateTime dataHoraAgendamento, StatusAgendamento status,
                       TipoAtendimento tipoAtendimento, String observacoes,
                       LocalDateTime dataCriacao, LocalDateTime dataAtualizacao,
                       String motivoCancelamento) {
        this.id = id;
        this.paciente = paciente;
        this.profissional = profissional;
        this.unidadeSaude = unidadeSaude;
        this.especialidade = especialidade;
        this.dataHoraAgendamento = dataHoraAgendamento;
        this.status = status;
        this.tipoAtendimento = tipoAtendimento;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.motivoCancelamento = motivoCancelamento;
    }

    public void confirmar() {
        if (this.status != StatusAgendamento.AGENDADO) {
            throw new IllegalStateException("Apenas agendamentos com status AGENDADO podem ser confirmados");
        }
        this.status = StatusAgendamento.CONFIRMADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void cancelar(String motivo, boolean canceladoPeloPaciente) {
        if (this.status == StatusAgendamento.CONCLUIDO) {
            throw new IllegalStateException("Não é possível cancelar um agendamento já concluído");
        }
        this.status = canceladoPeloPaciente ?
                StatusAgendamento.CANCELADO_PACIENTE : StatusAgendamento.CANCELADO_UNIDADE;
        this.motivoCancelamento = motivo;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void reagendar(LocalDateTime novaDataHora) {
        this.dataHoraAgendamento = novaDataHora;
        this.status = StatusAgendamento.AGENDADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void iniciarAtendimento() {
        this.status = StatusAgendamento.EM_ATENDIMENTO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void concluir() {
        this.status = StatusAgendamento.CONCLUIDO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void marcarNaoCompareceu() {
        this.status = StatusAgendamento.NAO_COMPARECEU;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public LocalDateTime getDataHoraAgendamento() {
        return dataHoraAgendamento;
    }

    public void setDataHoraAgendamento(LocalDateTime dataHoraAgendamento) {
        this.dataHoraAgendamento = dataHoraAgendamento;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public TipoAtendimento getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}
