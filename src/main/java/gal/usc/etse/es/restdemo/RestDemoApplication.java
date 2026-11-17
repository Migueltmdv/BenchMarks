package gal.usc.etse.es.restdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

/**
 * Punto de entrada de la aplicación Spring Boot.
 * - Arranca el contexto, el servidor embebido y la autoconfiguración.
 * - VIA_DTO fija el formato de los Page<T> como {content, page}.
 * - HAL habilita enlaces Hipermedia (aunque hoy no se usan en las respuestas).
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class RestDemoApplication {
    static void main(String[] args) {
        SpringApplication.run(RestDemoApplication.class, args);
    }
}
