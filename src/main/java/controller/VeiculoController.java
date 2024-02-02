package controller;

import exceptionhandler.NegocioException;
import model.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.VeiculoService;


@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }
    @GetMapping
    public ResponseEntity<Page<Veiculo>> listarTodosOsVeiculos(Pageable pageable) {
        Page<Veiculo> veiculos = veiculoService.listarTodosOsVeiculos(pageable);
        return ResponseEntity.ok().body(veiculos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> obterVeiculoPorId(@PathVariable Long id) {
        return veiculoService.listarVeiculoPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Veiculo> criarVeiculo(@RequestBody Veiculo veiculo) {
        Veiculo veiculoSalvo = veiculoService.cadastrarVeiculo(veiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> modificarVeiculo(@PathVariable Long id, @RequestBody Veiculo veiculoAtualizado) {
        try {
            Veiculo veiculoModificado = veiculoService.modificarVeiculo(id, veiculoAtualizado);
            return ResponseEntity.ok().body(veiculoModificado);
        } catch (NegocioException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable Long id) {
        veiculoService.deletarVeiculo(id);
        return ResponseEntity.noContent().build();
    }



}
