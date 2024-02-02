package service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.JWTService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Service
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.token-expiracao}")
    private String tokenExpiracao;

    @Value("${jwt.token-assinatura}")
    private String tokenAssinatura;

    @Override
    public String criarToken(Usuario usuario) {
        long exp = Long.valueOf(tokenExpiracao);
        LocalDateTime horaEDataExpiracaoToken = LocalDateTime.now().plusMinutes(exp);
        Instant instant = horaEDataExpiracaoToken.atZone( ZoneId.systemDefault() ).toInstant();
        java.util.Date data = Date.from(instant);

        String horaExpiracaoToken = horaEDataExpiracaoToken.toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        String token = Jwts
                .builder()
                .setExpiration(data)
                .setSubject(usuario.getEmail())
                .claim("id_usuario", usuario.getId())
                .claim("nome_usuario", usuario.getNome())
                .claim("hora_expiracao", horaExpiracaoToken)
                .signWith( SignatureAlgorithm.HS512 , tokenAssinatura )
                .compact();

        return token;
    }

    @Override
    public Claims receberClaims(String token) throws ExpiredJwtException {
        return null;
    }


    public Claims obterClaims(String token) throws ExpiredJwtException {
        return Jwts
                .parser()
                .setSigningKey(tokenAssinatura)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    @Override
    public boolean tokenValido(String token) {
        try {
            Claims claims = obterClaims(token);
            java.util.Date dataExemplo = claims.getExpiration();
            LocalDateTime tokenDataExpiracao = dataExemplo.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            boolean dataHoraAtualDepoisDaDataDeExpiracao = LocalDateTime.now().isAfter(tokenDataExpiracao);
            return !dataHoraAtualDepoisDaDataDeExpiracao;
        }catch(ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String receberLogin(String token) {
        Claims claims = receberClaims(token);
        return claims.getSubject();
    }
}
