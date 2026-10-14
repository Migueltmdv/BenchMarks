package gal.usc.etse.es.restdemo.filter;

import gal.usc.etse.es.restdemo.model.dto.User;
import gal.usc.etse.es.restdemo.repository.RoleRepository;
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
    ) throws ServletException, IOException, JwtException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(token == null || !token.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }

        User user = authenticationService.parseJWT(token.replaceFirst("^Bearer ", ""));

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                user.userID(),
                user.password(),
                user.roles().stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role)).toList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
