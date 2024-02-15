package config;


import enums.Funcoes;
import jakarta.annotation.PostConstruct;
import model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.UsuarioRepository;

public class DataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void iniciarComUsuarioPredefinido() {
        criarUsuarioAdministrador();
    }

    private void criarUsuarioAdministrador() {
        if (!usuarioRepository.existsByEmail("josezinho@gmail.com")) {
            Usuario usuario = new Usuario();
            usuario.setEmail("josezinho@gmail.com");
            usuario.setSenha(passwordEncoder.encode("1234567"));
            usuario.setFuncoes(Funcoes.ADMINISTRADOR);

            usuarioRepository.save(usuario);
        }
    }
}
