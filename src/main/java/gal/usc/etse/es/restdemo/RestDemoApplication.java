package gal.usc.etse.es.restdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class RestDemoApplication {
    static void main(String[] args) {
        SpringApplication.run(RestDemoApplication.class, args);
    }
}
