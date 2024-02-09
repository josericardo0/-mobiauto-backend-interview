import dto.AcessoDTO;
import dto.JwtDTO;
import dto.UsuarioDTO;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import service.JWTService;
import service.UsuarioService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JWTService jwtService;

    private AcessoDTO dto;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private String token;

    @BeforeEach
    public void setup() {
        dto = new AcessoDTO();
        dto.setEmail("user@example.com");
        dto.setSenha("password");

        usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setNome("Test User");

        usuarioDTO = new UsuarioDTO(usuario);
        usuarioDTO.setNome("Test User");
        usuarioDTO.setEmail("user@example.com");
        usuarioDTO.setSenha("password");
        token = "mockToken";
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    public void testAutenticarSuccess() throws Exception {
        JwtDTO jwtDTO = new JwtDTO(usuario.getNome(), token);

        Mockito.when(usuarioService.autenticarUsuario(dto.getEmail(), dto.getSenha())).thenReturn(usuario);
        Mockito.when(jwtService.criarToken(usuario)).thenReturn(token);

        mockMvc.perform(post("/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"" + dto.getEmail() + "\", \"senha\": \"" + dto.getSenha() + "\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    public void testSalvarUsuarioSuccess() throws Exception {
        Mockito.when(usuarioService.cadastrarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"" + usuarioDTO.getNome() + "\", \"email\": \"" + usuarioDTO.getEmail() + "\", \"senha\": \"" + usuarioDTO.getSenha() + "\" }"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void listarTodosUsuariosComSucesso() throws Exception {
        Usuario usuario1 = new Usuario();
        usuario1.setNome("Usuario 1");
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuario 2");

        List<Usuario> usuarios = List.of(usuario1, usuario2);
        Mockito.when(usuarioService.listarTodosUsuarios()).thenReturn(usuarios);
        mockMvc.perform(get("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(usuarios.size()))
                .andExpect(jsonPath("$[0].nome").value(usuarios.get(0).getNome()))
                .andExpect(jsonPath("$[1].nome").value(usuarios.get(1).getNome()));
    }





    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    public void testListarPorIDComSucesso() throws Exception {
        Long userId = 1L;
        usuario.setId(userId);

        Mockito.when(usuarioService.listarUsuarioPorId(userId)).thenReturn(java.util.Optional.of(usuario));

        mockMvc.perform(get("/usuarios/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    public void testModificarUsuarioSuccess() throws Exception {
        Long userId = 1L;
        UsuarioDTO dto = new UsuarioDTO(usuario);
        dto.setNome("Nome Atualizado");
        dto.setEmail("atualizado@exemplo.com");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setId(userId);
        updatedUsuario.setNome(dto.getNome());
        updatedUsuario.setEmail(dto.getEmail());

        Mockito.when(usuarioService.modificarUsuario(Mockito.eq(userId), Mockito.any(Usuario.class))).thenReturn(updatedUsuario);

        mockMvc.perform(put("/usuarios/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"" + dto.getNome() + "\", \"email\": \"" + dto.getEmail() + "\" }"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    public void testDeletarUsuarioSuccess() throws Exception {
        Long userId = 1L;
        Usuario usuarioToDelete = new Usuario();
        usuarioToDelete.setId(userId);
        Mockito.doNothing().when(usuarioService).deletarUsuario(Mockito.any(Usuario.class));

        mockMvc.perform(delete("/usuarios/{id}", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(usuarioService).deletarUsuario(Mockito.argThat(usuario -> usuario.getId().equals(userId)));
    }

}
