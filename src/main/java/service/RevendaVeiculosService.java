package service;

import model.Oportunidades;
import model.RevendaVeiculos;
import model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RevendaVeiculosService {
    Page<RevendaVeiculos> listarRevendas(Pageable pageable);
    RevendaVeiculos cadastroDeRevenda(RevendaVeiculos revendaVeiculos);
    Optional<RevendaVeiculos> listarRevendasPorId(Long id);
    RevendaVeiculos modificarRevenda(Long id, RevendaVeiculos revenda);
    void cnpjValido(String cnpj);
    void deletarRevendasVeiculos(RevendaVeiculos id);
    List<Oportunidades> listarOportunidadesPorLoja(Long idLoja);
    RevendaVeiculos atendimentoOportunidades(Long revendaId, Long oportunidadeId, Usuario usuarioId);

}
