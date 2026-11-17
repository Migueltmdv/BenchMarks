package gal.usc.etse.es.restdemo.configuration;

import gal.usc.etse.es.restdemo.filter.JWTFilter;
import gal.usc.etse.es.restdemo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Cadena de seguridad HTTP de la aplicación.
 * - Rutas públicas: auth, errores, documentación, estáticos y la imagen de fotos.
 * - El resto requiere autenticación mediante JWT.
 * - Sin CSRF y sin sesiones: cada petición se autentica con su token.
 * - Registra el JWTFilter y construye la jerarquía de roles desde la BD.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JWTFilter jwtFilter;
    private final AuthenticationService authenticationService;

    @Autowired
    public SecurityConfiguration(JWTFilter jwtFilter, AuthenticationService authenticationService) {
        this.jwtFilter = jwtFilter;
        this.authenticationService = authenticationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http.authorizeHttpRequests(authorize ->
                        // --- Rutas públicas (no requieren token) ---
                        authorize.requestMatchers("/auth/**").permitAll()          // login, registro, refresh, logout
                                .requestMatchers("/error").permitAll()               // errores internos de Spring
                                .requestMatchers("/docs/**").permitAll()             // Swagger/OpenAPI/Scalar
                                .requestMatchers("/", "/index.html", "/favicon.*").permitAll()
                                .requestMatchers("/*.js", "/*.css", "/*.html", "/*.png", "/*.ico", "/assets/**").permitAll()
                                .requestMatchers("/img/**").permitAll()              // imágenes de demostración
                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/photos/*/image").permitAll()
                                // --- Todo lo demás requiere usuario autenticado ---
                                .anyRequest().authenticated()
                )
                // API sin formularios ni cookies de sesión: no hace falta CSRF.
                .csrf(AbstractHttpConfigurer::disable)
                // El filtro JWT se ejecuta en cada petición para autenticarla.
                .addFilterAfter(jwtFilter, BasicAuthenticationFilter.class)
                // Sin HttpSession: el estado de autenticación viaja en el token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    // Jerarquía leída de la BD: permite que ADMIN herede los permisos de USER
    // y que cada rol herede sus permisos (recurso:acción).
    @Bean
    public RoleHierarchy roleHierarchy() {
        return authenticationService.loadRoleHierarchy();
    }
}