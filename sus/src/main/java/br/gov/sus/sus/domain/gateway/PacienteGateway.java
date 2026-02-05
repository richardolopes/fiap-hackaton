package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteGateway {
    
    Paciente salvar(Paciente paciente);
    
    Optional<Paciente> buscarPorId(Long id);
    
    Optional<Paciente> buscarPorCpf(String cpf);
    
    Optional<Paciente> buscarPorCartaoSus(String cartaoSus);
    
    List<Paciente> listarTodos();
    
    boolean existePorCpf(String cpf);
    
    boolean existePorCartaoSus(String cartaoSus);
    
    void deletar(Long id);
}
