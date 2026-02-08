package br.gov.sus.agendamento.domain.usecase.agendamento;

import br.gov.sus.agendamento.application.exception.BusinessException;
import br.gov.sus.agendamento.domain.entity.*;
import br.gov.sus.agendamento.domain.enums.StatusAgendamento;
import br.gov.sus.agendamento.domain.enums.TipoAtendimento;
import br.gov.sus.agendamento.domain.gateway.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CriarAgendamentoPorCepUseCase
 *
 * Cobre os cenários:
 * - Criação de agendamento com sucesso
 * - Seleção de profissional disponível quando o primeiro está ocupado (bug fix)
 * - Validações de paciente, especialidade, UBS e profissional não encontrados
 * - Todos os profissionais ocupados no horário
 * - Verificação dos dados salvos no agendamento
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CriarAgendamentoPorCepUseCase")
class CriarAgendamentoPorCepUseCaseTest {

    @Mock
    private AgendamentoGateway agendamentoGateway;
    @Mock
    private PacienteGateway pacienteGateway;
    @Mock
    private ProfissionalGateway profissionalGateway;
    @Mock
    private UnidadeSaudeGateway unidadeSaudeGateway;
    @Mock
    private EspecialidadeGateway especialidadeGateway;

    private CriarAgendamentoPorCepUseCase useCase;

    // Dados de teste
    private static final Long PACIENTE_ID = 1L;
    private static final String CEP = "01001000";
    private static final Long ESPECIALIDADE_ID = 1L;
    private static final LocalDateTime DATA_HORA = LocalDateTime.of(2026, 3, 10, 10, 0);
    private static final TipoAtendimento TIPO = TipoAtendimento.PRESENCIAL;
    private static final String OBSERVACOES = "Consulta de rotina";

    private Paciente paciente;
    private Especialidade especialidade;
    private UnidadeSaude unidadeSaude;
    private Profissional profissional1;
    private Profissional profissional2;
    private Profissional profissional3;

    @BeforeEach
    void setUp() {
        useCase = new CriarAgendamentoPorCepUseCase(
                agendamentoGateway, pacienteGateway, profissionalGateway,
                unidadeSaudeGateway, especialidadeGateway);

        paciente = Paciente.builder()
                .id(PACIENTE_ID)
                .nomeCompleto("Maria Silva")
                .cpf("12345678900")
                .cartaoSus("700000000000001")
                .build();

        especialidade = Especialidade.builder()
                .id(ESPECIALIDADE_ID)
                .nome("Clínica Geral")
                .descricao("Atendimento geral")
                .tempoConsultaMinutos(30)
                .ativo(true)
                .build();

        unidadeSaude = UnidadeSaude.builder()
                .codigoCnes("2300001")
                .nome("UBS Centro")
                .endereco("Rua Principal, 100")
                .bairro("Centro")
                .cep(CEP)
                .build();

        profissional1 = Profissional.builder()
                .id(1L)
                .nomeCompleto("Dr. João Médico")
                .registroConselho("CRM-SP 12345")
                .especialidadeId(ESPECIALIDADE_ID)
                .codigoCnesUnidade("2300001")
                .ativo(true)
                .build();

        profissional2 = Profissional.builder()
                .id(2L)
                .nomeCompleto("Dra. Ana Doutora")
                .registroConselho("CRM-SP 67890")
                .especialidadeId(ESPECIALIDADE_ID)
                .codigoCnesUnidade("2300001")
                .ativo(true)
                .build();

        profissional3 = Profissional.builder()
                .id(3L)
                .nomeCompleto("Dr. Carlos Especialista")
                .registroConselho("CRM-SP 11111")
                .especialidadeId(ESPECIALIDADE_ID)
                .codigoCnesUnidade("2300001")
                .ativo(true)
                .build();
    }

    private void configurarMocksBasicos() {
        when(pacienteGateway.buscarPorId(PACIENTE_ID)).thenReturn(Optional.of(paciente));
        when(especialidadeGateway.buscarPorId(ESPECIALIDADE_ID)).thenReturn(Optional.of(especialidade));
        when(unidadeSaudeGateway.buscarUbsMaisProximaPorCep(CEP)).thenReturn(Optional.of(unidadeSaude));
    }

    private Agendamento executar() {
        return useCase.executar(PACIENTE_ID, CEP, ESPECIALIDADE_ID, DATA_HORA, TIPO, OBSERVACOES);
    }

    // ========================================================================================
    // Cenários de sucesso
    // ========================================================================================

    @Nested
    @DisplayName("Cenários de sucesso")
    class CenariosSucesso {

        @Test
        @DisplayName("Deve criar agendamento com sucesso quando profissional disponível")
        void deveCriarAgendamentoComSucesso() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1));
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(1L), eq(DATA_HORA), anyList())).thenReturn(false);

            Agendamento agendamentoSalvo = new Agendamento();
            agendamentoSalvo.setId(100L);
            agendamentoSalvo.setPaciente(paciente);
            agendamentoSalvo.setProfissional(profissional1);
            agendamentoSalvo.setUnidadeSaude(unidadeSaude);
            agendamentoSalvo.setEspecialidade(especialidade);
            agendamentoSalvo.setDataHoraAgendamento(DATA_HORA);
            agendamentoSalvo.setStatus(StatusAgendamento.AGENDADO);
            agendamentoSalvo.setTipoAtendimento(TIPO);

            when(agendamentoGateway.salvar(any(Agendamento.class))).thenReturn(agendamentoSalvo);

            // Act
            Agendamento resultado = executar();

            // Assert
            assertNotNull(resultado);
            assertEquals(100L, resultado.getId());
            assertEquals(paciente, resultado.getPaciente());
            assertEquals(profissional1, resultado.getProfissional());
            assertEquals(unidadeSaude, resultado.getUnidadeSaude());
            assertEquals(especialidade, resultado.getEspecialidade());
            assertEquals(StatusAgendamento.AGENDADO, resultado.getStatus());

            verify(agendamentoGateway).salvar(any(Agendamento.class));
        }

        @Test
        @DisplayName("Deve salvar agendamento com todos os campos corretos")
        void deveSalvarAgendamentoComCamposCorretos() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1));
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(1L), eq(DATA_HORA), anyList())).thenReturn(false);
            when(agendamentoGateway.salvar(any(Agendamento.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            executar();

            // Assert - capturar o agendamento que foi passado para salvar
            ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
            verify(agendamentoGateway).salvar(captor.capture());

            Agendamento salvo = captor.getValue();
            assertEquals(paciente, salvo.getPaciente());
            assertEquals(profissional1, salvo.getProfissional());
            assertEquals(unidadeSaude, salvo.getUnidadeSaude());
            assertEquals(especialidade, salvo.getEspecialidade());
            assertEquals(DATA_HORA, salvo.getDataHoraAgendamento());
            assertEquals(StatusAgendamento.AGENDADO, salvo.getStatus());
            assertEquals(TIPO, salvo.getTipoAtendimento());
            assertEquals(OBSERVACOES, salvo.getObservacoes());
            assertNotNull(salvo.getDataCriacao());
        }
    }

    // ========================================================================================
    // Bug fix: seleção de profissional disponível
    // ========================================================================================

    @Nested
    @DisplayName("Seleção de profissional disponível (bug fix)")
    class SelecaoProfissionalDisponivel {

        @Test
        @DisplayName("Deve selecionar segundo profissional quando primeiro está ocupado")
        void deveUsarSegundoProfissionalQuandoPrimeiroOcupado() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1, profissional2));

            // Profissional 1 ocupado, profissional 2 livre
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(1L), eq(DATA_HORA), anyList())).thenReturn(true);
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(2L), eq(DATA_HORA), anyList())).thenReturn(false);

            when(agendamentoGateway.salvar(any(Agendamento.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            Agendamento resultado = executar();

            // Assert - deve ter selecionado o profissional 2
            ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
            verify(agendamentoGateway).salvar(captor.capture());
            assertEquals(profissional2, captor.getValue().getProfissional());
            assertEquals("Dra. Ana Doutora", captor.getValue().getProfissional().getNomeCompleto());
        }

        @Test
        @DisplayName("Deve selecionar terceiro profissional quando dois primeiros estão ocupados")
        void deveUsarTerceiroProfissionalQuandoDoisPrimeirosOcupados() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1, profissional2, profissional3));

            // Profissional 1 e 2 ocupados, profissional 3 livre
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(1L), eq(DATA_HORA), anyList())).thenReturn(true);
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(2L), eq(DATA_HORA), anyList())).thenReturn(true);
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(3L), eq(DATA_HORA), anyList())).thenReturn(false);

            when(agendamentoGateway.salvar(any(Agendamento.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            Agendamento resultado = executar();

            // Assert - deve ter selecionado o profissional 3
            ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
            verify(agendamentoGateway).salvar(captor.capture());
            assertEquals(profissional3, captor.getValue().getProfissional());
            assertEquals("Dr. Carlos Especialista", captor.getValue().getProfissional().getNomeCompleto());
        }

        @Test
        @DisplayName("Deve selecionar primeiro profissional quando todos estão livres")
        void deveUsarPrimeiroProfissionalQuandoTodosLivres() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1, profissional2, profissional3));

            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(1L), eq(DATA_HORA), anyList())).thenReturn(false);

            when(agendamentoGateway.salvar(any(Agendamento.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            Agendamento resultado = executar();

            // Assert - deve ter selecionado o profissional 1 (primeiro disponível)
            ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
            verify(agendamentoGateway).salvar(captor.capture());
            assertEquals(profissional1, captor.getValue().getProfissional());

            // Não deve ter verificado os outros (findFirst para no primeiro match)
            verify(agendamentoGateway, never()).existeAgendamentoProfissionalNoHorario(
                    eq(2L), eq(DATA_HORA), anyList());
        }

        @Test
        @DisplayName("Deve lançar exceção quando todos os profissionais estão ocupados no horário")
        void deveLancarExcecaoQuandoTodosProfissionaisOcupados() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1, profissional2));

            // Todos ocupados
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    anyLong(), eq(DATA_HORA), anyList())).thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> executar());
            assertTrue(exception.getMessage().contains("Nenhum profissional disponível"));
            assertTrue(exception.getMessage().contains(especialidade.getNome()));

            // Não deve ter salvo nada
            verify(agendamentoGateway, never()).salvar(any());
        }
    }

    // ========================================================================================
    // Cenários de validação / erro
    // ========================================================================================

    @Nested
    @DisplayName("Cenários de validação e erro")
    class CenariosValidacao {

        @Test
        @DisplayName("Deve lançar exceção quando paciente não encontrado")
        void deveLancarExcecaoQuandoPacienteNaoEncontrado() {
            // Arrange
            when(pacienteGateway.buscarPorId(PACIENTE_ID)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> executar());
            assertEquals("Paciente não encontrado", exception.getMessage());

            verify(agendamentoGateway, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando especialidade não encontrada")
        void deveLancarExcecaoQuandoEspecialidadeNaoEncontrada() {
            // Arrange
            when(pacienteGateway.buscarPorId(PACIENTE_ID)).thenReturn(Optional.of(paciente));
            when(especialidadeGateway.buscarPorId(ESPECIALIDADE_ID)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> executar());
            assertEquals("Especialidade não encontrada", exception.getMessage());

            verify(agendamentoGateway, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nenhuma UBS encontrada para o CEP")
        void deveLancarExcecaoQuandoUbsNaoEncontrada() {
            // Arrange
            when(pacienteGateway.buscarPorId(PACIENTE_ID)).thenReturn(Optional.of(paciente));
            when(especialidadeGateway.buscarPorId(ESPECIALIDADE_ID)).thenReturn(Optional.of(especialidade));
            when(unidadeSaudeGateway.buscarUbsMaisProximaPorCep(CEP)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> executar());
            assertTrue(exception.getMessage().contains("Nenhuma UBS encontrada"));
            assertTrue(exception.getMessage().contains(CEP));

            verify(agendamentoGateway, never()).salvar(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nenhum profissional para a especialidade")
        void deveLancarExcecaoQuandoNenhumProfissionalParaEspecialidade() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(Collections.emptyList());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> executar());
            assertTrue(exception.getMessage().contains("Nenhum profissional encontrado"));
            assertTrue(exception.getMessage().contains(especialidade.getNome()));

            verify(agendamentoGateway, never()).salvar(any());
        }
    }

    // ========================================================================================
    // Verificação dos status excluídos na checagem de conflito
    // ========================================================================================

    @Nested
    @DisplayName("Verificação dos status excluídos")
    class VerificacaoStatusExcluidos {

        @Test
        @DisplayName("Deve passar status CANCELADO_PACIENTE e CANCELADO_UNIDADE como excluídos")
        void devePassarStatusCanceladosComoExcluidos() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1));
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    anyLong(), any(), anyList())).thenReturn(false);
            when(agendamentoGateway.salvar(any(Agendamento.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            executar();

            // Assert - verificar que os status excluídos foram passados corretamente
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<StatusAgendamento>> statusCaptor = ArgumentCaptor.forClass(List.class);
            verify(agendamentoGateway).existeAgendamentoProfissionalNoHorario(
                    eq(1L), eq(DATA_HORA), statusCaptor.capture());

            List<StatusAgendamento> statusExcluidos = statusCaptor.getValue();
            assertEquals(2, statusExcluidos.size());
            assertTrue(statusExcluidos.contains(StatusAgendamento.CANCELADO_PACIENTE));
            assertTrue(statusExcluidos.contains(StatusAgendamento.CANCELADO_UNIDADE));
        }
    }

    // ========================================================================================
    // Cenário de telemedicina
    // ========================================================================================

    @Nested
    @DisplayName("Tipos de atendimento")
    class TiposAtendimento {

        @Test
        @DisplayName("Deve criar agendamento com tipo TELEMEDICINA")
        void deveCriarAgendamentoTelemedicina() {
            // Arrange
            configurarMocksBasicos();
            when(profissionalGateway.buscarPorEspecialidadeId(ESPECIALIDADE_ID))
                    .thenReturn(List.of(profissional1));
            when(agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                    eq(1L), eq(DATA_HORA), anyList())).thenReturn(false);
            when(agendamentoGateway.salvar(any(Agendamento.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            Agendamento resultado = useCase.executar(
                    PACIENTE_ID, CEP, ESPECIALIDADE_ID, DATA_HORA,
                    TipoAtendimento.TELEMEDICINA, "Teleconsulta");

            // Assert
            ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
            verify(agendamentoGateway).salvar(captor.capture());
            assertEquals(TipoAtendimento.TELEMEDICINA, captor.getValue().getTipoAtendimento());
            assertEquals("Teleconsulta", captor.getValue().getObservacoes());
        }
    }
}
