package service;

import model.Oportunidades;
import model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;
public interface OportunidadesService {

    Page<Oportunidades> listarOportunidades(Pageable pageable);
    Optional<Oportunidades> listarOportunidadesporId(Long id);
    Oportunidades cadastrarOportunidade(Oportunidades oportunidade);
    void deletarOportunidade(Long id);
    Oportunidades modificarOportunidade(Long id, Oportunidades oportunidade, Usuario usuarioLogado);
    Oportunidades registrarInformacoesCliente(Long oportunidadeId, Long clienteId);
    Oportunidades registrarInformacoesVeiculo(Long oportunidadeId, Long veiculoId);
    List<Oportunidades> listarAssistentesDaRevenda(Long idRevenda);
    Usuario proximoAssistente(Long idRevenda);
    Oportunidades distribuicaoOportunidadesSemProprietario(Long idRevenda, Oportunidades oportunidades);
    Oportunidades transferenciaOportunidades(Long oportunidadeId, Long idDestinoAssistente);
}
