package service.impl;

import enums.Status;
import exceptionhandler.NegocioException;
import javax.transaction.Transactional;
import messages.MensagensCustomizadas;
import model.Oportunidades;
import model.RevendaVeiculos;
import model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.RevendaVeiculosRepository;
import service.RevendaVeiculosService;
import service.UsuarioService;

import java.util.List;
import java.util.Optional;

@Service
public class RevendaVeiculosServiceImpl implements RevendaVeiculosService {

    private final RevendaVeiculosRepository revendaVeiculosRepository;
    private final UsuarioService usuarioService;

    public RevendaVeiculosServiceImpl(RevendaVeiculosRepository revendaVeiculosRepository, UsuarioService usuarioService) {
        this.revendaVeiculosRepository = revendaVeiculosRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public Page<RevendaVeiculos> listarRevendas(Pageable pageable) {
        return this.revendaVeiculosRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public RevendaVeiculos cadastroDeRevenda(RevendaVeiculos revendaVeiculos) {
        revendaVeiculos.setNomeSocial(revendaVeiculos.getNomeSocial());
        this.validarCnpj(revendaVeiculos.getCnpj());
        return this.revendaVeiculosRepository.save(revendaVeiculos);
    }

    @Override
    public Optional<RevendaVeiculos> listarRevendasPorId(Long id) {
        return Optional.empty();
    }

    public Optional<RevendaVeiculos> buscarRevendaPorId(Long id) {
        return this.revendaVeiculosRepository.findById(id);
    }

    @Override
    public RevendaVeiculos modificarRevenda(Long id, RevendaVeiculos revendaAtualizada) {
        return buscarRevendaPorId(id)
                .map(revendaVeiculoExiste -> {
                    revendaVeiculoExiste.setNomeSocial(revendaAtualizada.getNomeSocial());
                    revendaVeiculoExiste.setCnpj(revendaAtualizada.getCnpj());

                    return cadastroDeRevenda(revendaVeiculoExiste);
                })
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.REVENDA_NAO_ENCONTRADA + id));
    }

    @Override
    public void cnpjValido(String cnpj) {
    }


    public void validarCnpj(String cnpj) {
        boolean existe = this.revendaVeiculosRepository.existsByCnpj(cnpj);
        if (existe) {
            throw new NegocioException(MensagensCustomizadas.CNPJ_CADASTRADO);
        }
    }

    @Override
    @Transactional
    public void deletarRevendasVeiculos(RevendaVeiculos id) {
        this.revendaVeiculosRepository.delete(id);
    }

    public List<Oportunidades> listarOportunidadesPorLoja(Long lojaId) {
        RevendaVeiculos revendaVeiculos = this.buscarRevendaPorId(lojaId)
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.LOJA_NAO_ENCONTRADO + lojaId));

        return revendaVeiculos.getOportunidades();
    }

    @Override
    @Transactional
    public RevendaVeiculos atendimentoOportunidades(Long revendaId, Long oportunidadeId, Usuario atendimentoUsuario) {
        RevendaVeiculos revendaVeiculos = buscarRevendaPorId(revendaId)
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.REVENDA_NAO_ENCONTRADA + revendaId));

        Oportunidades oportunidades = revendaVeiculos.getOportunidades().stream()
                .filter(o -> o.getId().equals(oportunidadeId))
                .findFirst()
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.OPORTUNIDADE_NAO_ENCONTRADA + oportunidadeId));

        this.usuarioService.listarUsuarioPorId(atendimentoUsuario.getId())
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.USUARIO_NAO_ENCONTRADO + atendimentoUsuario.getId()));

        if (!atendimentoUsuario.getRevendaVeiculos().contains(revendaVeiculos)) {
            throw new NegocioException("A revenda não possui usuário associado");
        }
        oportunidades.setStatus(Status.EM_ATENDIMENTO);
        oportunidades.setAtendimento(atendimentoUsuario);

        return this.revendaVeiculosRepository.save(revendaVeiculos);
    }
}
