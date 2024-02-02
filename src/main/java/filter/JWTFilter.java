package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import service.JWTService;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    public JWTFilter(JWTService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");


        if(authorization != null && authorization.startsWith("Bearer")) {

            String token = authorization.split(" ")[1];
            boolean isTokenValid = jwtService.tokenValido(token);

            if(isTokenValid) {
                String login = jwtService.receberLogin(token);
                UserDetails authenticatedUser = userDetailsService.loadUserByUsername(login);

                UsernamePasswordAuthenticationToken user =
                        new UsernamePasswordAuthenticationToken(
                                authenticatedUser, null, authenticatedUser.getAuthorities());

                user.setDetails( new WebAuthenticationDetailsSource().buildDetails(request) );

                SecurityContextHolder.getContext().setAuthentication(user);

            }
        }

        filterChain.doFilter(request, response);
    }

}
