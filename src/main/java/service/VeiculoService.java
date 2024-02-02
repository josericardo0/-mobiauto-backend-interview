package service;

import model.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VeiculoService {
    Page<Veiculo> listarTodosOsVeiculos(Pageable pageable);
    Optional<Veiculo> listarVeiculoPorId(Long id);
    Veiculo cadastrarVeiculo(Veiculo veiculo);
    Veiculo modificarVeiculo(Long id, Veiculo veiculoAtualizado);
    void deletarVeiculo(Long id);
}
