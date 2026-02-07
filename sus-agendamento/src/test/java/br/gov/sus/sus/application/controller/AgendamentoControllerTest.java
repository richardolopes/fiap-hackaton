package br.gov.sus.sus.application.controller;

import br.gov.sus.sus.application.dto.request.AgendamentoPorCepRequest;
import br.gov.sus.sus.application.dto.request.CancelamentoRequest;
import br.gov.sus.sus.application.exception.BusinessException;
import br.gov.sus.sus.application.exception.ResourceNotFoundException;
import br.gov.sus.sus.domain.entity.*;
import br.gov.sus.sus.domain.enums.StatusAgendamento;
import br.gov.sus.sus.domain.enums.TipoAtendimento;
import br.gov.sus.sus.domain.usecase.agendamento.BuscarAgendamentoPorIdUseCase;
import br.gov.sus.sus.domain.usecase.agendamento.CancelarAgendamentoUseCase;
import br.gov.sus.sus.domain.usecase.agendamento.CriarAgendamentoPorCepUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes unitários para o AgendamentoController
 * <p>
 * Cobertura completa dos endpoints REST:
 * - POST   /api/v1/agendamentos/por-cep       → Criar agendamento por CEP
 * - GET    /api/v1/agendamentos/{id}           → Buscar agendamento por ID
 * - PATCH  /api/v1/agendamentos/{id}/cancelar  → Cancelar agendamento
 */
@WebMvcTest(AgendamentoController.class)
@DisplayName("AgendamentoController - Testes de Endpoint")
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CriarAgendamentoPorCepUseCase criarAgendamentoPorCepUseCase;

    @MockBean
    private BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase;

    @MockBean
    private CancelarAgendamentoUseCase cancelarAgendamentoUseCase;

    private Agendamento agendamentoCompleto;
    private LocalDateTime dataAgendamento;

    @BeforeEach
    void setUp() {
        dataAgendamento = LocalDateTime.now().plusDays(7);

        Paciente paciente = Paciente.builder()
                .id(1L)
                .nomeCompleto("João Silva")
                .cpf("12345678901")
                .cartaoSus("898000000000001")
                .build();

        Profissional profissional = Profissional.builder()
                .id(1L)
                .nomeCompleto("Dr. Maria Santos")
                .registroConselho("CRM-SP 123456")
                .build();

        UnidadeSaude unidade = UnidadeSaude.builder()
                .nome("UBS Centro")
                .codigoCnes("2300001")
                .endereco("Rua Principal, 100")
                .build();

        Especialidade especialidade = Especialidade.builder()
                .id(1L)
                .nome("Clínica Geral")
                .build();

        agendamentoCompleto = new Agendamento();
        agendamentoCompleto.setId(1L);
        agendamentoCompleto.setPaciente(paciente);
        agendamentoCompleto.setProfissional(profissional);
        agendamentoCompleto.setUnidadeSaude(unidade);
        agendamentoCompleto.setEspecialidade(especialidade);
        agendamentoCompleto.setDataHoraAgendamento(dataAgendamento);
        agendamentoCompleto.setStatus(StatusAgendamento.AGENDADO);
        agendamentoCompleto.setTipoAtendimento(TipoAtendimento.PRESENCIAL);
        agendamentoCompleto.setObservacoes("Primeira consulta");
        agendamentoCompleto.setDataCriacao(LocalDateTime.now());
    }

    // ==================================================================================
    // POST /api/v1/agendamentos/por-cep
    // ==================================================================================

    @Nested
    @DisplayName("POST /api/v1/agendamentos/por-cep")
    class CriarAgendamentoPorCep {

        @Test
        @DisplayName("Deve criar agendamento presencial com sucesso e retornar 201")
        void deveCriarAgendamentoPresencialComSucesso() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .observacoes("Primeira consulta")
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(1L), eq("01310100"), eq(1L),
                    any(LocalDateTime.class), eq(TipoAtendimento.PRESENCIAL),
                    eq("Primeira consulta")
            )).thenReturn(agendamentoCompleto);

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nomePaciente", is("João Silva")))
                    .andExpect(jsonPath("$.cpfPaciente", is("12345678901")))
                    .andExpect(jsonPath("$.cartaoSusPaciente", is("898000000000001")))
                    .andExpect(jsonPath("$.nomeProfissional", is("Dr. Maria Santos")))
                    .andExpect(jsonPath("$.registroConselhoProfissional", is("CRM-SP 123456")))
                    .andExpect(jsonPath("$.nomeUnidadeSaude", is("UBS Centro")))
                    .andExpect(jsonPath("$.enderecoUnidadeSaude", is("Rua Principal, 100")))
                    .andExpect(jsonPath("$.nomeEspecialidade", is("Clínica Geral")))
                    .andExpect(jsonPath("$.dataHoraAgendamento", notNullValue()))
                    .andExpect(jsonPath("$.status", is("AGENDADO")))
                    .andExpect(jsonPath("$.tipoAtendimento", is("PRESENCIAL")))
                    .andExpect(jsonPath("$.observacoes", is("Primeira consulta")))
                    .andExpect(jsonPath("$.dataCriacao", notNullValue()))
                    .andExpect(jsonPath("$.motivoCancelamento", nullValue()));

            verify(criarAgendamentoPorCepUseCase, times(1))
                    .executar(eq(1L), eq("01310100"), eq(1L), any(), eq(TipoAtendimento.PRESENCIAL), eq("Primeira consulta"));
        }

        @Test
        @DisplayName("Deve criar agendamento telemedicina com sucesso e retornar 201")
        void deveCriarAgendamentoTelemedicinaSucesso() throws Exception {
            Agendamento agendamentoTele = new Agendamento();
            agendamentoTele.setId(2L);
            agendamentoTele.setPaciente(agendamentoCompleto.getPaciente());
            agendamentoTele.setProfissional(agendamentoCompleto.getProfissional());
            agendamentoTele.setUnidadeSaude(agendamentoCompleto.getUnidadeSaude());
            agendamentoTele.setEspecialidade(agendamentoCompleto.getEspecialidade());
            agendamentoTele.setDataHoraAgendamento(dataAgendamento);
            agendamentoTele.setStatus(StatusAgendamento.AGENDADO);
            agendamentoTele.setTipoAtendimento(TipoAtendimento.TELEMEDICINA);
            agendamentoTele.setObservacoes("Consulta remota");
            agendamentoTele.setDataCriacao(LocalDateTime.now());

            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("04567890")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.TELEMEDICINA)
                    .observacoes("Consulta remota")
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(1L), eq("04567890"), eq(1L),
                    any(LocalDateTime.class), eq(TipoAtendimento.TELEMEDICINA),
                    eq("Consulta remota")
            )).thenReturn(agendamentoTele);

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(2)))
                    .andExpect(jsonPath("$.tipoAtendimento", is("TELEMEDICINA")))
                    .andExpect(jsonPath("$.observacoes", is("Consulta remota")))
                    .andExpect(jsonPath("$.status", is("AGENDADO")));

            verify(criarAgendamentoPorCepUseCase, times(1))
                    .executar(eq(1L), eq("04567890"), eq(1L), any(), eq(TipoAtendimento.TELEMEDICINA), eq("Consulta remota"));
        }

        @Test
        @DisplayName("Deve criar agendamento sem observações (campo opcional)")
        void deveCriarAgendamentoSemObservacoes() throws Exception {
            agendamentoCompleto.setObservacoes(null);

            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(1L), eq("01310100"), eq(1L),
                    any(LocalDateTime.class), eq(TipoAtendimento.PRESENCIAL),
                    eq(null)
            )).thenReturn(agendamentoCompleto);

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.observacoes", nullValue()));
        }

        @Test
        @DisplayName("Deve retornar 400 quando pacienteId é nulo")
        void deveRetornar400QuandoPacienteIdNulo() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .cep("01310100")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.pacienteId", is("ID do paciente é obrigatório")));

            verify(criarAgendamentoPorCepUseCase, never()).executar(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando CEP é vazio")
        void deveRetornar400QuandoCepVazio() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(criarAgendamentoPorCepUseCase, never()).executar(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando CEP tem formato inválido")
        void deveRetornar400QuandoCepFormatoInvalido() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("ABC12345")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(criarAgendamentoPorCepUseCase, never()).executar(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando especialidadeId é nulo")
        void deveRetornar400QuandoEspecialidadeIdNulo() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.especialidadeId", is("ID da especialidade é obrigatório")));

            verify(criarAgendamentoPorCepUseCase, never()).executar(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando tipoAtendimento é nulo")
        void deveRetornar400QuandoTipoAtendimentoNulo() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .build();

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.tipoAtendimento", is("Tipo de atendimento é obrigatório")));

            verify(criarAgendamentoPorCepUseCase, never()).executar(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando dataHoraAgendamento é nula")
        void deveRetornar400QuandoDataNula() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .especialidadeId(1L)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.dataHoraAgendamento", is("Data e hora do agendamento são obrigatórios")));

            verify(criarAgendamentoPorCepUseCase, never()).executar(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando body está vazio")
        void deveRetornar400QuandoBodyVazio() throws Exception {
            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(criarAgendamentoPorCepUseCase, never()).executar(any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 400 quando paciente não encontrado (BusinessException)")
        void deveRetornar400QuandoPacienteNaoEncontrado() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(999L)
                    .cep("01310100")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(999L), eq("01310100"), eq(1L), any(), eq(TipoAtendimento.PRESENCIAL), eq(null)
            )).thenThrow(new BusinessException("Paciente não encontrado"));

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Paciente não encontrado")));
        }

        @Test
        @DisplayName("Deve retornar 400 quando especialidade não encontrada")
        void deveRetornar400QuandoEspecialidadeNaoEncontrada() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .especialidadeId(999L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(1L), eq("01310100"), eq(999L), any(), eq(TipoAtendimento.PRESENCIAL), eq(null)
            )).thenThrow(new BusinessException("Especialidade não encontrada"));

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Especialidade não encontrada")));
        }

        @Test
        @DisplayName("Deve retornar 400 quando nenhuma UBS encontrada próxima ao CEP")
        void deveRetornar400QuandoNenhumaUbsEncontrada() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("99999999")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(1L), eq("99999999"), eq(1L), any(), eq(TipoAtendimento.PRESENCIAL), eq(null)
            )).thenThrow(new BusinessException("Nenhuma UBS encontrada próxima ao CEP: 99999999"));

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Nenhuma UBS encontrada próxima ao CEP: 99999999")));
        }

        @Test
        @DisplayName("Deve retornar 400 quando nenhum profissional encontrado para a especialidade")
        void deveRetornar400QuandoNenhumProfissionalEncontrado() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(1L), eq("01310100"), eq(1L), any(), eq(TipoAtendimento.PRESENCIAL), eq(null)
            )).thenThrow(new BusinessException("Nenhum profissional encontrado para a especialidade: Clínica Geral"));

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Nenhum profissional encontrado para a especialidade: Clínica Geral")));
        }

        @Test
        @DisplayName("Deve retornar 400 quando horário não disponível")
        void deveRetornar400QuandoHorarioNaoDisponivel() throws Exception {
            AgendamentoPorCepRequest request = AgendamentoPorCepRequest.builder()
                    .pacienteId(1L)
                    .cep("01310100")
                    .especialidadeId(1L)
                    .dataHoraAgendamento(dataAgendamento)
                    .tipoAtendimento(TipoAtendimento.PRESENCIAL)
                    .build();

            when(criarAgendamentoPorCepUseCase.executar(
                    eq(1L), eq("01310100"), eq(1L), any(), eq(TipoAtendimento.PRESENCIAL), eq(null)
            )).thenThrow(new BusinessException("Horário não disponível para agendamento"));

            mockMvc.perform(post("/api/v1/agendamentos/por-cep")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Horário não disponível para agendamento")));
        }
    }

    // ==================================================================================
    // GET /api/v1/agendamentos/{id}
    // ==================================================================================

    @Nested
    @DisplayName("GET /api/v1/agendamentos/{id}")
    class BuscarAgendamentoPorId {

        @Test
        @DisplayName("Deve retornar agendamento completo com 200")
        void deveRetornarAgendamentoCompleto() throws Exception {
            when(buscarAgendamentoPorIdUseCase.executar(1L)).thenReturn(agendamentoCompleto);

            mockMvc.perform(get("/api/v1/agendamentos/1")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nomePaciente", is("João Silva")))
                    .andExpect(jsonPath("$.cpfPaciente", is("12345678901")))
                    .andExpect(jsonPath("$.cartaoSusPaciente", is("898000000000001")))
                    .andExpect(jsonPath("$.nomeProfissional", is("Dr. Maria Santos")))
                    .andExpect(jsonPath("$.registroConselhoProfissional", is("CRM-SP 123456")))
                    .andExpect(jsonPath("$.nomeUnidadeSaude", is("UBS Centro")))
                    .andExpect(jsonPath("$.enderecoUnidadeSaude", is("Rua Principal, 100")))
                    .andExpect(jsonPath("$.nomeEspecialidade", is("Clínica Geral")))
                    .andExpect(jsonPath("$.dataHoraAgendamento", notNullValue()))
                    .andExpect(jsonPath("$.status", is("AGENDADO")))
                    .andExpect(jsonPath("$.tipoAtendimento", is("PRESENCIAL")))
                    .andExpect(jsonPath("$.observacoes", is("Primeira consulta")))
                    .andExpect(jsonPath("$.dataCriacao", notNullValue()));

            verify(buscarAgendamentoPorIdUseCase, times(1)).executar(1L);
        }

        @Test
        @DisplayName("Deve retornar agendamento com campos nulos quando entidades não carregadas")
        void deveRetornarAgendamentoComCamposNulos() throws Exception {
            Agendamento agendamentoMinimo = new Agendamento();
            agendamentoMinimo.setId(5L);
            agendamentoMinimo.setStatus(StatusAgendamento.AGENDADO);
            agendamentoMinimo.setTipoAtendimento(TipoAtendimento.PRESENCIAL);
            agendamentoMinimo.setDataHoraAgendamento(dataAgendamento);
            agendamentoMinimo.setDataCriacao(LocalDateTime.now());

            when(buscarAgendamentoPorIdUseCase.executar(5L)).thenReturn(agendamentoMinimo);

            mockMvc.perform(get("/api/v1/agendamentos/5")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.nomePaciente", nullValue()))
                    .andExpect(jsonPath("$.cpfPaciente", nullValue()))
                    .andExpect(jsonPath("$.cartaoSusPaciente", nullValue()))
                    .andExpect(jsonPath("$.nomeProfissional", nullValue()))
                    .andExpect(jsonPath("$.registroConselhoProfissional", nullValue()))
                    .andExpect(jsonPath("$.nomeUnidadeSaude", nullValue()))
                    .andExpect(jsonPath("$.enderecoUnidadeSaude", nullValue()))
                    .andExpect(jsonPath("$.nomeEspecialidade", nullValue()))
                    .andExpect(jsonPath("$.status", is("AGENDADO")))
                    .andExpect(jsonPath("$.tipoAtendimento", is("PRESENCIAL")))
                    .andExpect(jsonPath("$.observacoes", nullValue()))
                    .andExpect(jsonPath("$.motivoCancelamento", nullValue()));

            verify(buscarAgendamentoPorIdUseCase, times(1)).executar(5L);
        }

        @Test
        @DisplayName("Deve retornar 404 quando agendamento não encontrado (ResourceNotFoundException)")
        void deveRetornar404QuandoAgendamentoNaoEncontrado() throws Exception {
            when(buscarAgendamentoPorIdUseCase.executar(999L))
                    .thenThrow(new ResourceNotFoundException("Agendamento não encontrado"));

            mockMvc.perform(get("/api/v1/agendamentos/999")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Agendamento não encontrado")));

            verify(buscarAgendamentoPorIdUseCase, times(1)).executar(999L);
        }

        @Test
        @DisplayName("Deve retornar agendamento com status CANCELADO_PACIENTE e motivoCancelamento")
        void deveRetornarAgendamentoCanceladoComMotivo() throws Exception {
            agendamentoCompleto.setStatus(StatusAgendamento.CANCELADO_PACIENTE);
            agendamentoCompleto.setMotivoCancelamento("Mudança de cidade");

            when(buscarAgendamentoPorIdUseCase.executar(1L)).thenReturn(agendamentoCompleto);

            mockMvc.perform(get("/api/v1/agendamentos/1")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.status", is("CANCELADO_PACIENTE")))
                    .andExpect(jsonPath("$.motivoCancelamento", is("Mudança de cidade")));
        }

        @Test
        @DisplayName("Deve retornar agendamento com status CONCLUIDO")
        void deveRetornarAgendamentoConcluido() throws Exception {
            agendamentoCompleto.setStatus(StatusAgendamento.CONCLUIDO);

            when(buscarAgendamentoPorIdUseCase.executar(1L)).thenReturn(agendamentoCompleto);

            mockMvc.perform(get("/api/v1/agendamentos/1")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("CONCLUIDO")));
        }
    }

    // ==================================================================================
    // PATCH /api/v1/agendamentos/{id}/cancelar
    // ==================================================================================

    @Nested
    @DisplayName("PATCH /api/v1/agendamentos/{id}/cancelar")
    class CancelarAgendamento {

        @Test
        @DisplayName("Deve cancelar agendamento com sucesso e retornar 200")
        void deveCancelarAgendamentoComSucesso() throws Exception {
            Agendamento agendamentoCancelado = new Agendamento();
            agendamentoCancelado.setId(1L);
            agendamentoCancelado.setPaciente(agendamentoCompleto.getPaciente());
            agendamentoCancelado.setProfissional(agendamentoCompleto.getProfissional());
            agendamentoCancelado.setUnidadeSaude(agendamentoCompleto.getUnidadeSaude());
            agendamentoCancelado.setEspecialidade(agendamentoCompleto.getEspecialidade());
            agendamentoCancelado.setDataHoraAgendamento(dataAgendamento);
            agendamentoCancelado.setStatus(StatusAgendamento.CANCELADO_PACIENTE);
            agendamentoCancelado.setTipoAtendimento(TipoAtendimento.PRESENCIAL);
            agendamentoCancelado.setMotivoCancelamento("Não posso comparecer");
            agendamentoCancelado.setDataCriacao(LocalDateTime.now());

            CancelamentoRequest request = CancelamentoRequest.builder()
                    .motivo("Não posso comparecer")
                    .build();

            when(cancelarAgendamentoUseCase.executar(eq(1L), eq("Não posso comparecer"), eq(true)))
                    .thenReturn(agendamentoCancelado);

            mockMvc.perform(patch("/api/v1/agendamentos/1/cancelar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.status", is("CANCELADO_PACIENTE")))
                    .andExpect(jsonPath("$.motivoCancelamento", is("Não posso comparecer")))
                    .andExpect(jsonPath("$.nomePaciente", is("João Silva")))
                    .andExpect(jsonPath("$.nomeProfissional", is("Dr. Maria Santos")))
                    .andExpect(jsonPath("$.nomeUnidadeSaude", is("UBS Centro")))
                    .andExpect(jsonPath("$.nomeEspecialidade", is("Clínica Geral")));

            verify(cancelarAgendamentoUseCase, times(1)).executar(eq(1L), eq("Não posso comparecer"), eq(true));
        }

        @Test
        @DisplayName("Deve retornar 400 quando motivo está vazio")
        void deveRetornar400QuandoMotivoVazio() throws Exception {
            CancelamentoRequest request = CancelamentoRequest.builder()
                    .motivo("")
                    .build();

            mockMvc.perform(patch("/api/v1/agendamentos/1/cancelar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.motivo", is("Motivo do cancelamento é obrigatório")));

            verify(cancelarAgendamentoUseCase, never()).executar(anyLong(), anyString(), anyBoolean());
        }

        @Test
        @DisplayName("Deve retornar 400 quando motivo é nulo")
        void deveRetornar400QuandoMotivoNulo() throws Exception {
            mockMvc.perform(patch("/api/v1/agendamentos/1/cancelar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.motivo", is("Motivo do cancelamento é obrigatório")));

            verify(cancelarAgendamentoUseCase, never()).executar(anyLong(), anyString(), anyBoolean());
        }

        @Test
        @DisplayName("Deve retornar 400 quando motivo contém apenas espaços em branco")
        void deveRetornar400QuandoMotivoApenasEspacos() throws Exception {
            CancelamentoRequest request = CancelamentoRequest.builder()
                    .motivo("   ")
                    .build();

            mockMvc.perform(patch("/api/v1/agendamentos/1/cancelar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(cancelarAgendamentoUseCase, never()).executar(anyLong(), anyString(), anyBoolean());
        }

        @Test
        @DisplayName("Deve retornar 404 quando agendamento não encontrado para cancelar")
        void deveRetornar404QuandoAgendamentoNaoEncontradoParaCancelar() throws Exception {
            CancelamentoRequest request = CancelamentoRequest.builder()
                    .motivo("Não preciso mais")
                    .build();

            when(cancelarAgendamentoUseCase.executar(eq(999L), eq("Não preciso mais"), eq(true)))
                    .thenThrow(new ResourceNotFoundException("Agendamento não encontrado"));

            mockMvc.perform(patch("/api/v1/agendamentos/999/cancelar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Agendamento não encontrado")));

            verify(cancelarAgendamentoUseCase, times(1)).executar(eq(999L), eq("Não preciso mais"), eq(true));
        }

        @Test
        @DisplayName("Deve retornar 400 quando agendamento já está concluído")
        void deveRetornar400QuandoAgendamentoJaConcluido() throws Exception {
            CancelamentoRequest request = CancelamentoRequest.builder()
                    .motivo("Quero cancelar")
                    .build();

            when(cancelarAgendamentoUseCase.executar(eq(1L), eq("Quero cancelar"), eq(true)))
                    .thenThrow(new IllegalArgumentException("Não é possível cancelar um agendamento já concluído"));

            mockMvc.perform(patch("/api/v1/agendamentos/1/cancelar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Não é possível cancelar um agendamento já concluído")));

            verify(cancelarAgendamentoUseCase, times(1)).executar(eq(1L), eq("Quero cancelar"), eq(true));
        }

        @Test
        @DisplayName("Deve cancelar agendamento com motivo longo")
        void deveCancelarComMotivoLongo() throws Exception {
            String motivoLongo = "Preciso cancelar porque tive uma emergência familiar e não conseguirei comparecer na data agendada.";

            Agendamento agendamentoCancelado = new Agendamento();
            agendamentoCancelado.setId(1L);
            agendamentoCancelado.setStatus(StatusAgendamento.CANCELADO_PACIENTE);
            agendamentoCancelado.setMotivoCancelamento(motivoLongo);
            agendamentoCancelado.setDataCriacao(LocalDateTime.now());

            CancelamentoRequest request = CancelamentoRequest.builder()
                    .motivo(motivoLongo)
                    .build();

            when(cancelarAgendamentoUseCase.executar(eq(1L), eq(motivoLongo), eq(true)))
                    .thenReturn(agendamentoCancelado);

            mockMvc.perform(patch("/api/v1/agendamentos/1/cancelar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("CANCELADO_PACIENTE")))
                    .andExpect(jsonPath("$.motivoCancelamento", is(motivoLongo)));
        }
    }
}
