package gal.usc.etse.es.restdemo.filter;

import gal.usc.etse.es.restdemo.model.dto.User;
import gal.usc.etse.es.restdemo.service.AuthenticationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que autentica cada petición a partir del token JWT.
 * 1. Si no hay 'Authorization: Bearer ...', deja pasar sin autenticar.
 * 2. Verifica la firma del token y carga el usuario desde la BD.
 * 3. Publica la autenticación en el SecurityContext con sus roles.
 * 4. Si el token es inválido o ha expirado, limpia el contexto y continúa;
 *    la cadena de seguridad responderá 401 cuando haga falta.
 */
@Component
public class JWTFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;

    @Autowired
    public JWTFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Sin cabecera Bearer no hay nada que autenticar: se deja pasar.
        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // Verifica la firma con la clave pública y carga el usuario de la BD.
            User user = authenticationService.parseJWT(token.replaceFirst("^Bearer ", ""));

            // Autenticación válida para el resto de la petición: usuario + roles.
            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                    user.userID(),
                    user.password(),
                    user.roles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            // Token expirado o manipulado: se limpia el contexto y se sigue.
            // La cadena de seguridad devolverá 401 en las rutas protegidas.
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}