package service.impl;

import exceptionhandler.NegocioException;
import javax.transaction.Transactional;
import model.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.VeiculoRepository;
import service.VeiculoService;

import java.util.Optional;

@Service
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public VeiculoServiceImpl(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public Page<Veiculo> listarTodosOsVeiculos(Pageable pageable) {
        return veiculoRepository.findAll(pageable);
    }

    public Optional<Veiculo> listarVeiculoPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    @Transactional
    public Veiculo cadastrarVeiculo(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    @Transactional
    public Veiculo modificarVeiculo(Long id, Veiculo veiculoAtualizado) {
        return listarVeiculoPorId(id)
                .map(veiculoExiste -> {
                    veiculoExiste.setMarca(veiculoAtualizado.getMarca());
                    veiculoExiste.setModeloVeiculo(veiculoAtualizado.getModeloVeiculo());
                    veiculoExiste.setVersaoVeiculo(veiculoAtualizado.getVersaoVeiculo());
                    veiculoExiste.setAnoModelo(veiculoAtualizado.getAnoModelo());

                    return cadastrarVeiculo(veiculoExiste);
                })
                .orElseThrow(() -> new NegocioException("Veículo não encontrado com o id: " + id));
    }

    @Transactional
    public void deletarVeiculo(Long id) {
        veiculoRepository.deleteById(id);
    }
}
