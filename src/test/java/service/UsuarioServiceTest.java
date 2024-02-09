package service.impl;

import exceptionhandler.AcessoException;
import exceptionhandler.NegocioException;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void autenticarUsuario_WithValidCredentials_ShouldReturnUsuario() {
        String email = "user@example.com";
        String senha = "password";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode(senha));

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        Usuario result = usuarioService.autenticarUsuario(email, senha);

        assertNotNull(result);
    }

    @Test
    void autenticarUsuario_WithInvalidPassword_ShouldThrowAcessoException() {
        String email = "user@example.com";
        String senha = "wrongPassword";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode("correctPassword"));

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(encoder.matches(senha, usuario.getSenha())).thenReturn(false);

        assertThrows(AcessoException.class, () -> usuarioService.autenticarUsuario(email, senha));
    }

    @Test
    void cadastrarUsuario_WithEmailAlreadyExists_ShouldThrowNegocioException() {
        Usuario usuario = new Usuario();
        usuario.setEmail("existing@example.com");

        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(true);

        assertThrows(NegocioException.class, () -> usuarioService.cadastrarUsuario(usuario));
    }

    @Test
    void cadastrarUsuario_WithNewEmail_ShouldReturnUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("new@example.com");
        usuario.setSenha("password");

        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(encoder.encode(usuario.getSenha())).thenReturn("encryptedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario savedUsuario = usuarioService.cadastrarUsuario(usuario);

        assertNotNull(savedUsuario);
        assertEquals("encryptedPassword", savedUsuario.getSenha());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deletarUsuario_ShouldCallDelete() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        assertDoesNotThrow(() -> usuarioService.deletarUsuario(usuario));
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void modificarUsuario_ExistingUser_ShouldUpdateUserDetails() {
        Long userId = 1L;
        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(userId);
        existingUsuario.setEmail("original@example.com");
        existingUsuario.setSenha("password");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setNome("Updated Name");
        updatedUsuario.setEmail("updated@example.com");
        updatedUsuario.setSenha("newPassword");

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);
        when(encoder.encode(anyString())).thenReturn("encryptedNewPassword");

        Usuario result = usuarioService.modificarUsuario(userId, updatedUsuario);

        assertNotNull(result);
        assertEquals("Updated Name", result.getNome());
        assertEquals("updated@example.com", result.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void modificarUsuario_NonExistingUser_ShouldThrowNegocioException() {
        Long userId = 99L;
        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setNome("Non-existent User");

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NegocioException.class, () -> usuarioService.modificarUsuario(userId, updatedUsuario));
    }

    @Test
    void listarUsuarioPorId_ExistingUser_ShouldReturnUser() {
        Long userId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(userId);
        usuario.setEmail("user@example.com");

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.listarUsuarioPorId(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
    }

    @Test
    void listarUsuarioPorId_NonExistingUser_ShouldReturnEmptyOptional() {
        Long userId = 99L;

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.listarUsuarioPorId(userId);

        assertFalse(result.isPresent());
    }


}
