package br.gov.sus.agendamento.domain.usecase.agendamento;

import br.gov.sus.agendamento.application.exception.ResourceNotFoundException;
import br.gov.sus.shared.domain.entity.*;
import br.gov.sus.shared.domain.enums.StatusAgendamento;
import br.gov.sus.shared.domain.enums.TipoAtendimento;
import br.gov.sus.agendamento.domain.gateway.AgendamentoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para BuscarAgendamentoPorIdUseCase
 * <p>
 * Testa a lógica de busca de agendamentos por ID:
 * - Buscar agendamento existente
 * - Tratamento de agendamento não encontrado
 * - Retorno de dados completos do agendamento
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do BuscarAgendamentoPorIdUseCase")
class BuscarAgendamentoPorIdUseCaseSimpleTest {

    @Mock
    private AgendamentoGateway agendamentoGateway;

    private BuscarAgendamentoPorIdUseCase useCase;

    private Agendamento agendamentoMock;

    @BeforeEach
    void setUp() {
        useCase = new BuscarAgendamentoPorIdUseCase(agendamentoGateway);

        Paciente paciente = Paciente.builder()
                .nomeCompleto("Ana Costa")
                .build();

        Profissional profissional = Profissional.builder()
                .nomeCompleto("Dr. Paulo Oliveira")
                .build();

        UnidadeSaude unidade = UnidadeSaude.builder()
                .nome("UBS Vila Nova")
                .codigoCnes("2300002")
                .endereco("Avenida Secundária, 200")
                .build();

        Especialidade especialidade = Especialidade.builder()
                .nome("Pediatria")
                .build();

        agendamentoMock = new Agendamento();
        agendamentoMock.setId(1L);
        agendamentoMock.setPaciente(paciente);
        agendamentoMock.setProfissional(profissional);
        agendamentoMock.setUnidadeSaude(unidade);
        agendamentoMock.setEspecialidade(especialidade);
        agendamentoMock.setDataHoraAgendamento(LocalDateTime.now().plusDays(5));
        agendamentoMock.setStatus(StatusAgendamento.AGENDADO);
        agendamentoMock.setTipoAtendimento(TipoAtendimento.PRESENCIAL);
        agendamentoMock.setObservacoes("Consulta de rotina");
        agendamentoMock.setDataCriacao(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve buscar agendamento existente com sucesso")
    void deveBuscarAgendamentoComSucesso() {
        // Arrange
        when(agendamentoGateway.buscarPorId(1L)).thenReturn(Optional.of(agendamentoMock));

        // Act
        Agendamento resultado = useCase.executar(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Ana Costa", resultado.getPaciente().getNomeCompleto());
        assertEquals("Dr. Paulo Oliveira", resultado.getProfissional().getNomeCompleto());
        assertEquals("UBS Vila Nova", resultado.getUnidadeSaude().getNome());
        assertEquals("Pediatria", resultado.getEspecialidade().getNome());
        assertEquals(StatusAgendamento.AGENDADO, resultado.getStatus());

        verify(agendamentoGateway, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não encontrado")
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
        // Arrange
        when(agendamentoGateway.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> useCase.executar(999L));

        verify(agendamentoGateway, times(1)).buscarPorId(999L);
    }

    @Test
    @DisplayName("Deve retornar agendamento com todos os dados preenchidos")
    void deveRetornarAgendamentoComTodosDados() {
        // Arrange
        when(agendamentoGateway.buscarPorId(1L)).thenReturn(Optional.of(agendamentoMock));

        // Act
        Agendamento resultado = useCase.executar(1L);

        // Assert
        assertNotNull(resultado.getId());
        assertNotNull(resultado.getPaciente());
        assertNotNull(resultado.getProfissional());
        assertNotNull(resultado.getUnidadeSaude());
        assertNotNull(resultado.getEspecialidade());
        assertNotNull(resultado.getDataHoraAgendamento());
        assertNotNull(resultado.getStatus());
        assertNotNull(resultado.getTipoAtendimento());
        assertNotNull(resultado.getDataCriacao());
    }

    @Test
    @DisplayName("Deve buscar agendamento com status diferente")
    void deveBuscarAgendamentoComStatusDiferente() {
        // Arrange
        agendamentoMock.setStatus(StatusAgendamento.CANCELADO_PACIENTE);
        when(agendamentoGateway.buscarPorId(1L)).thenReturn(Optional.of(agendamentoMock));

        // Act
        Agendamento resultado = useCase.executar(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(StatusAgendamento.CANCELADO_PACIENTE, resultado.getStatus());
    }

    @Test
    @DisplayName("Deve buscar múltiplos agendamentos com IDs diferentes")
    void deveBuscarMultiplosAgendamentosComIdsDiferentes() {
        // Arrange
        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(2L);
        agendamento2.setStatus(StatusAgendamento.AGENDADO);

        when(agendamentoGateway.buscarPorId(1L)).thenReturn(Optional.of(agendamentoMock));
        when(agendamentoGateway.buscarPorId(2L)).thenReturn(Optional.of(agendamento2));

        // Act
        Agendamento resultado1 = useCase.executar(1L);
        Agendamento resultado2 = useCase.executar(2L);

        // Assert
        assertNotNull(resultado1);
        assertNotNull(resultado2);
        assertEquals(1L, resultado1.getId());
        assertEquals(2L, resultado2.getId());

        verify(agendamentoGateway, times(1)).buscarPorId(1L);
        verify(agendamentoGateway, times(1)).buscarPorId(2L);
    }
}
