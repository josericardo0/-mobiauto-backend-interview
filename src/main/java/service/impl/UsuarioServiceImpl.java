package service.impl;

import exceptionhandler.AcessoException;
import exceptionhandler.NegocioException;
import jakarta.transaction.Transactional;
import messages.MensagensCustomizadas;
import model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UsuarioRepository;
import service.UsuarioService;

import java.util.List;
import java.util.Optional;


@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public Usuario autenticarUsuario(String email, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if (!usuario.isPresent()) {
            throw new AcessoException(MensagensCustomizadas.USUARIO_NAO_ENCONTRADO);
        }

        boolean senhasIguais = encoder.matches(senha, usuario.get().getSenha());

        if (!senhasIguais) {
            throw new AcessoException(MensagensCustomizadas.SENHA_INVALIDA);
        }

        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        emailValido(usuario.getEmail());
        usuario.setNome(usuario.getNome());
        usuario.setFuncoes(usuario.getFuncoes());
        usuario.setRevendaVeiculos(usuario.getRevendaVeiculos());
        senhaCriptografada(usuario);
        return usuarioRepository.save(usuario);
    }

    private void senhaCriptografada(Usuario usuario) {
        String senha = usuario.getSenha();
        String senhaCripto = encoder.encode(senha);
        usuario.setSenha(senhaCripto);
    }

    @Override
    public void emailValido(String email) {
        boolean emailExiste = usuarioRepository.existsByEmail(email);
        if (!emailExiste) {
            throw new NegocioException(MensagensCustomizadas.EMAIL_CADASTRADO);
        }
    }

    @Override
    public Optional<Usuario> listarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> listarTodosUsuarios() {
        return this.usuarioRepository.findAll();
    }

    public Usuario modificarUsuario(Long id, Usuario usuarioAtualizado) {
        return listarUsuarioPorId(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNome(usuarioAtualizado.getNome());
                    usuarioExistente.setEmail(usuarioAtualizado.getEmail());
                    usuarioExistente.setFuncoes(usuarioAtualizado.getFuncoes());
                    usuarioExistente.setSenha(usuarioAtualizado.getSenha());

                    return cadastrarUsuario(usuarioExistente);
                })
                .orElseThrow(() -> new NegocioException(MensagensCustomizadas.USUARIO_NAO_ENCONTRADO + id));
    }

    @Override
    @Transactional
    public void deletarUsuario(Usuario id) {
        this.usuarioRepository.delete(id);
    }
}
