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

import java.security.*;

@Configuration
@EnableMethodSecurity()
public class SecurityConfiguration {
    JWTFilter jwtFilter;
    AuthenticationService authenticationService;

    @Autowired
    public SecurityConfiguration(JWTFilter jwtFilter,  AuthenticationService authenticationService) {
        this.jwtFilter = jwtFilter;
        this.authenticationService = authenticationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/docs/**").permitAll()
                                .requestMatchers("/", "/index.html", "/favicon.ico").permitAll()
                                .requestMatchers("/*.js", "/*.css", "/*.html", "/assets/**").permitAll()
                                .requestMatchers("/img/**").permitAll()
                                .anyRequest().authenticated()

                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(jwtFilter, BasicAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return authenticationService.loadRoleHierarchy();
    }
}
