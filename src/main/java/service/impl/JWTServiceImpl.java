package service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.JWTService;
import io.jsonwebtoken.MalformedJwtException;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.token-assinatura}")
    private String secret;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String criarToken(Usuario usuario) {
        long expirationTime = 7200000;
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        String funcoesAsString = usuario.getFuncoes().name();

        return Jwts.builder()
                .setIssuer("mobiauto")
                .setSubject(funcoesAsString)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public Claims receberClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    @Override
    public boolean tokenValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            return false;
        }
    }



    @Override
    public String receberLogin(String token) {
        Claims claims = receberClaims(token);
        return claims.getSubject();
    }

    @Override
    public String refreshToken(String token) {
        Claims claims = receberClaims(token);
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + 7200000);

        return Jwts.builder()
                .setIssuer("mobiauto")
                .setSubject(claims.getSubject())
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    @Override
    public Date getTokenExpiration(String token) {
        Claims claims = receberClaims(token);
        return claims.getExpiration();
    }
}
