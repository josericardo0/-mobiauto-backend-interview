package service.impl;

import enums.Funcoes;
import enums.Status;
import exceptionhandler.NegocioException;
import jakarta.transaction.Transactional;
import messages.MensagensCustomizadas;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import repository.OportunidadeRepository;
import service.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OportunidadesServiceImpl implements OportunidadesService {
    private final OportunidadeRepository oportunidadeRepository;

    private final ClienteService clienteService;

    private final VeiculoService veiculoService;

    private final RevendaVeiculosService revendaVeiculosService;

    private final UsuarioService usuarioService;

    @Autowired // Para o spring injetar automaticamente as depedencias
    public OportunidadesServiceImpl(OportunidadeRepository oportunidadeRepository, ClienteService clienteService, VeiculoService veiculoService, RevendaVeiculosService revendaVeiculosService, UsuarioService usuarioService) {
        this.oportunidadeRepository = oportunidadeRepository;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.revendaVeiculosService = revendaVeiculosService;
        this.usuarioService = usuarioService;
    }

    @Override
    public Page<Oportunidades> listarOportunidades(Pageable pageable) {
        return this.oportunidadeRepository.findAll(pageable);
    }

    @Override
    public Optional<Oportunidades> listarOportunidadesporId(Long id) {
        return this.oportunidadeRepository.findById(id);
    }

    @Override
    @Transactional
    public Oportunidades cadastrarOportunidade(Oportunidades oportunidade) {
        oportunidade.setStatus(Status.NOVO);
        oportunidade.setDataCriacao(LocalDateTime.now());
        return this.oportunidadeRepository.save(oportunidade);
    }

    @Override
    @Transactional
    public void deletarOportunidade(Long id) {
        this.oportunidadeRepository.deleteById(id);
    }

    @Override
    @Transactional // C
    public Oportunidades modificarOportunidade(Long id, Oportunidades oportunidade, Usuario usuarioLogado) {
        return listarOportunidadesporId(id).map(oportunidadeExiste -> {
            validaEdicaoOportunidade(oportunidadeExiste, usuarioLogado);

            oportunidadeExiste.setCliente(oportunidade.getCliente());
            oportunidadeExiste.setRevendaVeiculos(oportunidade.getRevendaVeiculos());
            oportunidadeExiste.setMotivoConclusao(oportunidade.getMotivoConclusao());
            oportunidadeExiste.setStatus(oportunidade.getStatus());
            oportunidadeExiste.setVeiculo(oportunidade.getVeiculo());

            if (oportunidade.getStatus() == Status.EM_ATENDIMENTO) {
                if (oportunidade.getMotivoConclusao() == null || oportunidade.getMotivoConclusao().isEmpty()) {
                    throw new NegocioException(MensagensCustomizadas.STATUS_SEM_MOTIVO);
                }
                oportunidade.setStatus(Status.FINALIZADO);
                oportunidadeExiste.setMotivoConclusao(oportunidade.getMotivoConclusao());
                oportunidade.setDataAtribuicao(LocalDateTime.now());

                /**
                 * Tirei o código else onde vocÊ seta o valor da conclusão como nulo
                 * No bloco if você verifica se o motivo da conclusão é nulo, e lança uma exceção
                 * Então setar nulo no else, forçaria o seu bloco if acontecer em alguns casos
                 */
            }

            return this.cadastrarOportunidade(oportunidadeExiste);
        }).orElseThrow(() -> new NegocioException(MensagensCustomizadas.OPORTUNIDADE_NAO_ENCONTRADA + id));
    }

    private void validaEdicaoOportunidade(Oportunidades oportunidade, Usuario usuarioLogado) {
        if (usuarioLogado.getFuncoes() != Funcoes.PROPRIETARIO_LOJA && usuarioLogado.getFuncoes() != Funcoes.GERENTE) {
            if (!oportunidade.getAtendimento().equals(usuarioLogado)) {
                throw new NegocioException(MensagensCustomizadas.SEM_AUTORIZACAO);
            }
        }
    }


    @Transactional
    public Oportunidades registrarInformacoesCliente(Long idOportunidade, Long idCliente) {
        Oportunidades oportunidades = listarOportunidadesporId(idOportunidade)
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.OPORTUNIDADE_NAO_ENCONTRADA + idOportunidade));

        Cliente cliente = clienteService.listarClientesPorId(idCliente)
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.CLIENTE_NAO_ENCONTRADO + idCliente));

        oportunidades.setCliente(cliente);

        return oportunidadeRepository.save(oportunidades);
    }

    @Transactional
    public Oportunidades registrarInformacoesVeiculo(Long idOportunidade, Long idVeiculo) {
        Oportunidades oportunidades = listarOportunidadesporId(idOportunidade)
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.OPORTUNIDADE_NAO_ENCONTRADA + idOportunidade));

        Veiculo veiculo = this.veiculoService.listarVeiculoPorId(idVeiculo)
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.VEICULO_NAO_ENCONTRADO + idVeiculo));

        oportunidades.setVeiculo(veiculo);

        return oportunidadeRepository.save(oportunidades);
    }

    public List<Oportunidades> listarAssistentesDaRevenda(Long revendaId) {
        RevendaVeiculos revendaVeiculos = this.revendaVeiculosService.listarRevendasPorId(revendaId)
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.REVENDA_NAO_ENCONTRADA));

        return revendaVeiculos.getOportunidades()
                .stream()
                .filter(oportunidades -> oportunidades.getAtendimento() != null &&
                        oportunidades.getAtendimento().getFuncoes().equals(Funcoes.ASSISTENTE))
                .collect(Collectors.toList());
    }

    @Transactional
    public Oportunidades distribuicaoOportunidadesSemProprietario(Long revendaId, Oportunidades oportunidades) {
        Usuario proximoAssistente = proximoAssistente(revendaId);

        if (proximoAssistente != null) {
            oportunidades.setAtendimento(proximoAssistente);
            oportunidades.setStatus(Status.EM_ATENDIMENTO);
            oportunidades.setDataAtribuicao(LocalDateTime.now());
        } else {
            oportunidades.setStatus(Status.NOVO);
        }

        return cadastrarOportunidade(oportunidades);
    }

    public Usuario proximoAssistente(Long revendaId) {
        RevendaVeiculos revendaVeiculos = this.revendaVeiculosService.listarRevendasPorId(revendaId)
                .orElseThrow(() -> new NegocioException("Não foi encontrada uma revenda com o ID especificado"));

        /**
         * A função estava muito verbosa, então decidi quebra-la em partes com pequenas funções
         * Dessa forma facilita a legibilidade e manutenção do código
         */
        Set<Usuario> assistentes = this.getAssistentesPelaRevendaVeiculos(revendaVeiculos);
        Map<Usuario, Long> oportunidadesParaCadaAssistente = this.getNumerosDeOportunidadesParaCadaAssistente(revendaVeiculos);
        List<Usuario> assistentesEnfileirados = this.enfileirarAssistentesPeloTempoDeRecebimento(assistentes, oportunidadesParaCadaAssistente);
        return assistentesEnfileirados.isEmpty() ? null : assistentesEnfileirados.get(0);
    }

    public long calcularTempoRecebimentoOportunidade(Usuario usuario, Map<Usuario, Long> oportunidades) {
        Long ultimaOportunidadeFeita = oportunidades.getOrDefault(usuario, 0L);
        Long agora = System.currentTimeMillis();

        return ultimaOportunidadeFeita == 0 ? Long.MAX_VALUE : agora - ultimaOportunidadeFeita;
    }

    @Transactional
    public Oportunidades transferenciaOportunidades(Long idOportunidade, Long idDestinoAssistente) {
        Oportunidades oportunidades = listarOportunidadesporId(idOportunidade).orElseThrow(
                () -> new NegocioException(MensagensCustomizadas.OPORTUNIDADE_NAO_ENCONTRADA + idOportunidade));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        if (usuarioLogado.getFuncoes() == Funcoes.PROPRIETARIO_LOJA || usuarioLogado.getFuncoes() == Funcoes.GERENTE) {
            Usuario assistenteDestino = this.usuarioService.listarUsuarioPorId(idDestinoAssistente).orElseThrow(
                    () -> new NegocioException(MensagensCustomizadas.ASSISTENTE_NAO_ENCONTRADO + idDestinoAssistente));

            oportunidades.setAtendimento(assistenteDestino);
            oportunidades.setDataAtribuicao(LocalDateTime.now());
            oportunidades.setStatus(Status.EM_ATENDIMENTO);

            return cadastrarOportunidade(oportunidades);
        }
        /**
         * Aqui não é necessário o bloco else, já que seu bloco if aplica o early returne, caso o bloco não aconteça, a exception será lançada
         */
        throw new NegocioException(MensagensCustomizadas.SEM_PERMISSAO_PARA_TRANSFERENCIA);
}

    private Set<Usuario> getAssistentesPelaRevendaVeiculos(RevendaVeiculos revendaVeiculos) {
        return revendaVeiculos.getOportunidades()
                .stream()
                .filter(oportunidades -> oportunidades != null &&
                        oportunidades.getAtendimento() != null &&
                        oportunidades.getAtendimento().getFuncoes().equals(Funcoes.ASSISTENTE))
                .map(Oportunidades::getAtendimento)
                .collect(Collectors.toSet());

    }

    private Map<Usuario, Long> getNumerosDeOportunidadesParaCadaAssistente(RevendaVeiculos revendaVeiculos) {
        return revendaVeiculos.getOportunidades()
                .stream()
                .filter(oportunidades -> oportunidades.getAtendimento() != null &&
                        oportunidades.getAtendimento().getFuncoes().equals(Funcoes.ASSISTENTE))
                .collect(Collectors.groupingBy(Oportunidades::getAtendimento, Collectors.counting()));
    }

    private  List<Usuario> enfileirarAssistentesPeloTempoDeRecebimento(Set<Usuario> assistentes,    Map<Usuario, Long> oportunidadesParaCadaAssistente) {
        return assistentes.stream()
                .sorted(Comparator.<Usuario, Long>comparing(usuario -> oportunidadesParaCadaAssistente.getOrDefault(usuario, 0L))
                        .thenComparing(usuario -> calcularTempoRecebimentoOportunidade(usuario, oportunidadesParaCadaAssistente), Comparator.reverseOrder()))
                .toList();
    }

}
