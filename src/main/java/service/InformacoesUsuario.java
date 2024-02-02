package service;

import messages.MensagensCustomizadas;
import model.Usuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repository.UsuarioRepository;


@Service
public class InformacoesUsuario implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public InformacoesUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuarioEncontrado = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MensagensCustomizadas.EMAIL_NAO_CADASTRADO));

        return User.builder()
                .username(usuarioEncontrado.getEmail())
                .password(usuarioEncontrado.getSenha())
                .build();
    }

}
