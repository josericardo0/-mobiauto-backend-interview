package controller;

import dto.UsuarioDTO;
import exceptionhandler.NegocioException;
import jakarta.validation.Valid;
import model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import service.UsuarioService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<UsuarioDTO> salvar(@RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setFuncoes(dto.getFuncoes());
        Usuario usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(usuarioCadastrado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<UsuarioDTO> listarPorId(@PathVariable("id") Long id) {
        Usuario usuario = usuarioService.listarUsuarioPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<UsuarioDTO> modificarUsuario(@PathVariable Long id, @RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = usuarioService.modificarUsuario(id, dto.toEntity());
        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE') or hasRole('ASSISTENTE')")
    public ResponseEntity<List<UsuarioDTO>> listarTodosUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodosUsuarios().stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROPRIETARIO_LOJA') or hasRole('GERENTE')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Usuario id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (NegocioException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
