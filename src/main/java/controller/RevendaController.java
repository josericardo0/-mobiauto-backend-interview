package controller;

import exceptionhandler.NegocioException;
import model.Oportunidades;
import model.RevendaVeiculos;
import model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.RevendaVeiculosService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/revendas")
public class RevendaController {

    private final RevendaVeiculosService revendaVeiculosService;

    public RevendaController(RevendaVeiculosService revendaVeiculosService) {
        this.revendaVeiculosService = revendaVeiculosService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<RevendaVeiculos> buscarRevendaPorId(@PathVariable Long id) {
        Optional<RevendaVeiculos> revendaVeiculos = this.revendaVeiculosService.listarRevendasPorId(id);
        return revendaVeiculos.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO') or hasRole('GERENTE')")
    public ResponseEntity<RevendaVeiculos> cadastrarRevenda(@RequestBody @Validated RevendaVeiculos revendaVeiculos) {
        RevendaVeiculos revendaCadastrada = this.revendaVeiculosService.cadastroDeRevenda(revendaVeiculos);
        return ResponseEntity.status(HttpStatus.CREATED).body(revendaCadastrada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO') or hasRole('GERENTE')")
    public ResponseEntity<RevendaVeiculos> alterarRevenda(@PathVariable Long id, @RequestBody RevendaVeiculos revendaAtualizada) {
        try {
            RevendaVeiculos revendaModificada = this.revendaVeiculosService.modificarRevenda(id, revendaAtualizada);
            return ResponseEntity.ok().body(revendaModificada);
        } catch (NegocioException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO') or hasRole('GERENTE')")
    public ResponseEntity<Void> excluirRevenda(@PathVariable RevendaVeiculos id) {
        try {
            this.revendaVeiculosService.deletarRevendasVeiculos(id);
            return ResponseEntity.noContent().build();
        } catch (NegocioException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<Page<RevendaVeiculos>> buscarTodasRevendas(Pageable pageable) {
        Page<RevendaVeiculos> revendas = this.revendaVeiculosService.listarRevendas(pageable);
        return ResponseEntity.ok().body(revendas);
    }

    @GetMapping("/{id}/oportunidades")
    public ResponseEntity<List<Oportunidades>> listarOportunidadesPorLoja(@PathVariable Long id) {
        try {
            List<Oportunidades> oportunidades = this.revendaVeiculosService.listarOportunidadesPorLoja(id);
            return ResponseEntity.ok().body(oportunidades);
        } catch (NegocioException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/atenderOportunidade/{idOportunidade}/{idUsuario}")
    public ResponseEntity<?> atenderOportunidade(
            @PathVariable Long revendaId,
            @PathVariable Long oportunidadeId,
            @PathVariable Usuario usuario) {
        try {
            RevendaVeiculos revendaVeiculos = this.revendaVeiculosService.atendimentoOportunidades(revendaId, oportunidadeId, usuario);
            Oportunidades oportunidadeAtendida = revendaVeiculos.getOportunidades().stream()
                    .filter(o -> o.getId().equals(oportunidadeId))
                    .findFirst()
                    .orElseThrow(() -> new NegocioException("Oportunidade não encontrada."));
            return ResponseEntity.ok(oportunidadeAtendida);
        } catch (NegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
