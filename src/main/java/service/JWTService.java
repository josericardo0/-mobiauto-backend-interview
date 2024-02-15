package service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import model.Usuario;

import java.util.Date;

public interface JWTService {

    String criarToken(Usuario usuario);
    Claims receberClaims(String token) throws ExpiredJwtException;
    boolean tokenValido(String token);
    String receberLogin(String token);
    String refreshToken(String token);
    Date getTokenExpiration(String token);
}
