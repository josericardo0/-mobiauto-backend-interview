package service;

import model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario autenticarUsuario(String email, String senha);
    Usuario cadastrarUsuario(Usuario usuario);
    void emailValido(String email);
    Optional<Usuario> listarUsuarioPorId(Long id);
    List<Usuario> listarTodosUsuarios();
    Usuario modificarUsuario(Long id, Usuario usuarioAtualizado);
    void deletarUsuario(Usuario id);

}
