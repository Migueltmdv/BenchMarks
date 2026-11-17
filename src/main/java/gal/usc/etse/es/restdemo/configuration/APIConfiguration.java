package gal.usc.etse.es.restdemo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración del versionado de la API.
 * Permite indicar la versión por cabecera (API-Version), por query (?version=)
 * o por media type (Accept: application/json;v=1). La versión por defecto es
 * 1.0 y no es obligatoria, así que las rutas que no la indican siguen funcionando.
 */
@Configuration
public class APIConfiguration implements WebMvcConfigurer {
    @Override
	public void configureApiVersioning(ApiVersionConfigurer configurer) {
		configurer.useRequestHeader("API-Version");
        configurer.useQueryParam("version");
        configurer.useMediaTypeParameter(MediaType.ALL, "v");
        configurer.setDefaultVersion("1.0");
        configurer.setVersionRequired(false);
	}
}
