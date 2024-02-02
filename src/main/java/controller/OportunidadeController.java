package controller;

import exceptionhandler.NegocioException;
import model.Oportunidades;

import model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import service.OportunidadesService;
import java.util.List;
@RestController
@RequestMapping("/oportunidades")
public class OportunidadeController {

    private final OportunidadesService oportunidadesService;

    public OportunidadeController(OportunidadesService oportunidadesService) {
        this.oportunidadesService = oportunidadesService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<Page<Oportunidades>> listarOportunidades(Pageable pageable) {
        Page<Oportunidades> oportunidades = oportunidadesService.listarOportunidades(pageable);
        return ResponseEntity.ok().body(oportunidades);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<Oportunidades> listarOportunidadePorId(@PathVariable Long id) {
        return oportunidadesService.listarOportunidadesporId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<Oportunidades> cadastrarOportunidade(@RequestBody Oportunidades oportunidade) {
        Oportunidades oportunidadeSalva = oportunidadesService.cadastrarOportunidade(oportunidade);
        return ResponseEntity.ok().body(oportunidadeSalva);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<Void> deletarOportunidade(@PathVariable Long id) {
        oportunidadesService.deletarOportunidade(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cadastrar-info-cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<Oportunidades> cadastrarInfoCliente(
            @PathVariable Long oportunidadeId,
            @PathVariable Long clienteId) {

        try {
            Oportunidades oportunidadeAtualizada = oportunidadesService.registrarInformacoesCliente(oportunidadeId, clienteId);
            return ResponseEntity.ok().body(oportunidadeAtualizada);
        } catch (NegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cadastrar-info-veiculo/{veiculoId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<Oportunidades> registrarDadosVeiculo(
            @PathVariable Long oportunidadeId,
            @PathVariable Long veiculoId) {

        try {
            Oportunidades oportunidadeAtualizada = oportunidadesService.registrarInformacoesVeiculo(oportunidadeId, veiculoId);
            return ResponseEntity.ok().body(oportunidadeAtualizada);
        } catch (NegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/assistentes")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<List<Oportunidades>> buscarAssistentesDaRevenda(@PathVariable Long idRevenda) {
        List<Oportunidades> assistentes = oportunidadesService.listarAssistentesDaRevenda(idRevenda);
        return ResponseEntity.ok(assistentes);
    }

    @PostMapping("/distribuicao-oportunidade-sem-proprietario")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<Oportunidades> distribuicaoOportunidadesSemProprietario(@PathVariable Long idRevenda,
                                                                             @RequestBody Oportunidades oportunidade) {
        try {
            Oportunidades oportunidadeDistribuida = oportunidadesService.distribuicaoOportunidadesSemProprietario(idRevenda,
                    oportunidade);
            return ResponseEntity.ok(oportunidadeDistribuida);
        } catch (NegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{oportunidadeId}/transferir/{assistenteDestinoId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<Oportunidades> transferirOportunidade(@PathVariable Long oportunidadeId,
                                                               @PathVariable Long assistenteDestinoId) {
        try {
            Oportunidades oportunidadeTransferida = oportunidadesService.transferenciaOportunidades(oportunidadeId,
                    assistenteDestinoId);
            return ResponseEntity.ok(oportunidadeTransferida);
        } catch (NegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<Oportunidades> modificarOportunidade(
            @PathVariable Long id,
            @RequestBody Oportunidades oportunidades,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        try {
            Oportunidades oportunidadeAtualizada = oportunidadesService.modificarOportunidade(id, oportunidades, usuarioLogado);
            return ResponseEntity.ok(oportunidadeAtualizada);
        } catch (NegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
