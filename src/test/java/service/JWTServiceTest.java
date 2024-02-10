package service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import service.impl.JWTServiceImpl;
import java.security.Key;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    @InjectMocks
    private JWTServiceImpl jwtService;

    private Key tokenAssinatura;
    private final String tokenExpiracao = "60";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenAssinatura = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String encodedKey = Base64.getEncoder().encodeToString(tokenAssinatura.getEncoded());
        ReflectionTestUtils.setField(jwtService, "tokenExpiracao", tokenExpiracao);
        ReflectionTestUtils.setField(jwtService, "tokenAssinatura", encodedKey); // Use encodedKey here
    }

    @Test
    void testCriarToken() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setId(1L);
        usuario.setNome("Test User");

        String token = jwtService.criarToken(usuario);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(tokenAssinatura)
                .build()
                .parseClaimsJws(token);

        assertEquals(usuario.getEmail(), claims.getBody().getSubject());
        assertEquals(usuario.getId().toString(), claims.getBody().get("id_usuario", String.class));
        assertEquals(usuario.getNome(), claims.getBody().get("nome_usuario", String.class));
    }

    @Test
    void testTokenValido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        String token = jwtService.criarToken(usuario);

        assertTrue(jwtService.tokenValido(token));
    }

    @Test
    void testTokenExpirado() {
        ReflectionTestUtils.setField(jwtService, "tokenExpiracao", "-1");
        Usuario usuario = new Usuario();
        usuario.setEmail("expired@example.com");
        usuario.setId(2L);

        String expiredToken = jwtService.criarToken(usuario);

        ReflectionTestUtils.setField(jwtService, "tokenExpiracao", tokenExpiracao);

        assertFalse(jwtService.tokenValido(expiredToken));
    }

    @Test
    void testReceberLogin() {
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        String token = jwtService.criarToken(usuario);

        String login = jwtService.receberLogin(token);
        assertEquals(usuario.getEmail(), login);
    }
}
