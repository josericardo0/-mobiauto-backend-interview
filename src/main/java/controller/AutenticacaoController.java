package controller;

import dto.LoginDTO;
import dto.UsuarioDTO;
import model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import service.impl.JWTServiceImpl;

@RestController
@RequestMapping("autenticar")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;

    private final JWTServiceImpl jwtService;

    public AutenticacaoController(AuthenticationManager authenticationManager, JWTServiceImpl jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/usuario/login")
    public ResponseEntity<LoginDTO> loginUsuarios(@RequestBody @Valid UsuarioDTO data) {
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(data.getLogin(), data.getSenha());
        Authentication authentication = authenticationManager.authenticate(loginToken);
        Usuario authenticatedUser = (Usuario) authentication.getPrincipal();
        String token = jwtService.criarToken(authenticatedUser);

        LoginDTO response = new LoginDTO("Login realizado com sucesso. Token gerado: ", token);
        return ResponseEntity.ok(response);
    }

}
