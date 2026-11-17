package gal.usc.etse.es.restdemo.service;

import gal.usc.etse.es.restdemo.exception.InvalidRefreshTokenException;
import gal.usc.etse.es.restdemo.model.entity.RefreshToken;
import gal.usc.etse.es.restdemo.model.dto.User;
import gal.usc.etse.es.restdemo.repository.RefreshTokenRepository;
import gal.usc.etse.es.restdemo.repository.RoleRepository;
import gal.usc.etse.es.restdemo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Servicio central de autenticación.
 * - login: valida credenciales y emite un JWT firmado (RS256).
 * - login(refreshToken): renueva el JWT a partir del token guardado en Redis.
 * - regenerate/invalidate: gestionan los refresh tokens (TTL de 7 días).
 * - parseJWT: verifica el token y devuelve el usuario.
 * - loadRoleHierarchy: traduce roles y permisos de la BD a Spring Security.
 */
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final KeyPair keyPair;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.jwt.ttl:PT15M}")
    private Duration tokenTTL;

    @Value("${auth.refresh.ttl:PT72H}")
    private Duration refreshTTL;

    @Autowired
    public AuthenticationService(
            AuthenticationManager authenticationManager,
            KeyPair keyPair,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.keyPair = keyPair;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Retryable(
            maxRetries = 3,
            delay = 300,
            jitter = 50,
            timeUnit = TimeUnit.MILLISECONDS,
            multiplier = 2
    )
    @ConcurrencyLimit(10)
    public User login(User user) throws AuthenticationException {
        // 1. Spring Security valida el usuario y la contraseña (DaoAuthenticationProvider).
        Authentication auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(user.userID(), user.password()));

        // 2. Se recogen las autoridades (ROLE_USER, ROLE_ADMIN...) para el claim.
        List<String> roles = auth.getAuthorities()
                .stream()
                .filter(authority -> authority instanceof SimpleGrantedAuthority)
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 3. Se construye y firma el JWT (15 minutos por defecto).
        //    El subject es el userID y el claim "roles" viaja dentro del token.
        String token = Jwts.builder()
                .subject(auth.getName())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(tokenTTL)))
                .notBefore(Date.from(Instant.now()))
                .claim("roles", roles)
                .signWith(keyPair.getPrivate())
                .compact();

        // 4. El DTO transporta el token en el campo password; el controller
        //    lo copia a la cabecera Authorization y no lo serializa.
        return new User(user.userID(), null, null, null, null, null, token, new HashSet<>(roles));
    }

    public User login(String refreshToken) throws InvalidRefreshTokenException {
        // El refresh token es un UUID guardado en Redis -> usuario.
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);

        if (token.isPresent()) {
            var user = userRepository.findByUserID(token.get().getUser()).orElseThrow(() -> new UsernameNotFoundException(token.get().getUser()));

            // Reutiliza el login normal; el DTO del usuario ya trae el hash
            // de la contraseña almacenado, que sirve para reautenticar.
            return login(User.from(user));
        }

        throw new InvalidRefreshTokenException(refreshToken);
    }

    // Rotación: se eliminan los refresh tokens previos del usuario y se crea uno nuevo.
    public String regenerateRefreshToken(User user) {
        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken(uuid.toString(), user.userID(), refreshTTL.toSeconds());
        refreshTokenRepository.deleteAllByUser(user.userID());
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    // Logout: invalida todas las sesiones de refresh del usuario.
    public void invalidateTokens(String username) {
        refreshTokenRepository.deleteAllByUser(username);
    }

    public User parseJWT(String token) throws JwtException {
        // Verifica la firma con la clave pública y extrae el payload.
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        var user = userRepository.findByUserID(username);

        if (user.isPresent()) {
            return User.from(user.get());
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }

    // Traduce las tablas role_hierarchy y role_permissions a la jerarquía
    // que entiende Spring Security (ADMIN incluye USER, etc.).
    public RoleHierarchy loadRoleHierarchy() {
        RoleHierarchyImpl.Builder builder = RoleHierarchyImpl.withRolePrefix("");

        roleRepository.findAll().forEach(role -> {
            if (!role.getIncludes().isEmpty()) {
                builder.role("ROLE_"+role.getRolename()).implies(
                        role.getIncludes().stream().map(i -> "ROLE_"+i.getRolename()).toArray(String[]::new)
                );
            }
            if (!role.getPermissions().isEmpty()) {
                builder.role("ROLE_"+role.getRolename()).implies(
                        role.getPermissions().stream().map(p -> p.getResource()+":"+p.getAction()).toArray(String[]::new)
                );
            }
        });

        return builder.build();
    }
}
