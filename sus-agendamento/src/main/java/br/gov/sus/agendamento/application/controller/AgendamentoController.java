package br.gov.sus.agendamento.application.controller;

import br.gov.sus.agendamento.application.dto.request.AgendamentoPorCepRequest;
import br.gov.sus.agendamento.application.dto.request.CancelamentoRequest;
import br.gov.sus.agendamento.application.dto.response.AgendamentoResponse;
import br.gov.sus.agendamento.domain.entity.Agendamento;
import br.gov.sus.agendamento.domain.usecase.agendamento.BuscarAgendamentoPorIdUseCase;
import br.gov.sus.agendamento.domain.usecase.agendamento.CancelarAgendamentoUseCase;
import br.gov.sus.agendamento.domain.usecase.agendamento.CriarAgendamentoPorCepUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agendamentos")
public class AgendamentoController {

    private final CriarAgendamentoPorCepUseCase criarAgendamentoPorCepUseCase;
    private final BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase;
    private final CancelarAgendamentoUseCase cancelarAgendamentoUseCase;

    public AgendamentoController(CriarAgendamentoPorCepUseCase criarAgendamentoPorCepUseCase,
                                 BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase,
                                 CancelarAgendamentoUseCase cancelarAgendamentoUseCase) {
        this.criarAgendamentoPorCepUseCase = criarAgendamentoPorCepUseCase;
        this.buscarAgendamentoPorIdUseCase = buscarAgendamentoPorIdUseCase;
        this.cancelarAgendamentoUseCase = cancelarAgendamentoUseCase;
    }

    /**
     * Agendar consulta buscando UBS mais próxima do CEP
     * Usa a API real do SUS para localizar a UBS
     */
    @PostMapping("/por-cep")
    public ResponseEntity<AgendamentoResponse> agendarPorCep(@Valid @RequestBody AgendamentoPorCepRequest request) {
        Agendamento agendamento = criarAgendamentoPorCepUseCase.executar(
                request.getPacienteId(),
                request.getCep(),
                request.getEspecialidadeId(),
                request.getDataHoraAgendamento(),
                request.getTipoAtendimento(),
                request.getObservacoes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(agendamento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> buscarPorId(@PathVariable Long id) {
        Agendamento agendamento = buscarAgendamentoPorIdUseCase.executar(id);
        return ResponseEntity.ok(toResponse(agendamento));
    }

    /**
     * Cancelar agendamento
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoResponse> cancelar(@PathVariable Long id,
                                                        @Valid @RequestBody CancelamentoRequest request) {
        Agendamento agendamento = cancelarAgendamentoUseCase.executar(id, request.getMotivo(), true);
        return ResponseEntity.ok(toResponse(agendamento));
    }

    private AgendamentoResponse toResponse(Agendamento agendamento) {
        return AgendamentoResponse.builder()
                .id(agendamento.getId())
                .nomePaciente(agendamento.getPaciente() != null ? agendamento.getPaciente().getNomeCompleto() : null)
                .cpfPaciente(agendamento.getPaciente() != null ? agendamento.getPaciente().getCpf() : null)
                .cartaoSusPaciente(agendamento.getPaciente() != null ? agendamento.getPaciente().getCartaoSus() : null)
                .nomeProfissional(agendamento.getProfissional() != null ? agendamento.getProfissional().getNomeCompleto() : null)
                .registroConselhoProfissional(agendamento.getProfissional() != null ? agendamento.getProfissional().getRegistroConselho() : null)
                .nomeUnidadeSaude(agendamento.getUnidadeSaude() != null ? agendamento.getUnidadeSaude().getNome() : null)
                .enderecoUnidadeSaude(agendamento.getUnidadeSaude() != null ? agendamento.getUnidadeSaude().getEndereco() : null)
                .nomeEspecialidade(agendamento.getEspecialidade() != null ? agendamento.getEspecialidade().getNome() : null)
                .dataHoraAgendamento(agendamento.getDataHoraAgendamento())
                .status(agendamento.getStatus())
                .tipoAtendimento(agendamento.getTipoAtendimento())
                .observacoes(agendamento.getObservacoes())
                .dataCriacao(agendamento.getDataCriacao())
                .motivoCancelamento(agendamento.getMotivoCancelamento())
                .build();
    }
}
