package config;

import filter.JWTFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import enums.Funcoes;
import service.JWTService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtService, userDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /**
         * Alguns metodos do spring security foram depreciados, como o and por exemplo
         * O Spring security agora está usando lambda para a sua configuração, refatorei alguns metodos com as novas versões.
         */
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize -> authorize
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/autenticar").permitAll()
                .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                .requestMatchers(HttpMethod.POST, "/veiculos").permitAll()
                .requestMatchers(HttpMethod.POST, "/oportunidades").permitAll()
                .requestMatchers(HttpMethod.POST, "/revenda").hasAnyRole(Funcoes.ADMINISTRADOR.toString(), Funcoes.PROPRIETARIO_LOJA.toString())
                .requestMatchers(HttpMethod.POST, "/usuarios").hasAnyRole(Funcoes.ADMINISTRADOR.toString(), Funcoes.PROPRIETARIO_LOJA.toString(), Funcoes.GERENTE.toString())
                .anyRequest().authenticated());
        /**
         * Como você já configurou o exception handler para lançar exceções de não autorização, acredito que essa linha não precisa ser utilizada
         */
        // .exceptionHandling().authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso não autorizado"))                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/v3/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }
}
