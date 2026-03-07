package es.refugio.animales.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de un Centro Educativo")
                        .version("1.0.0")
                        .description("Documentación detallada de la API para la gestión de un Centro Educativo")
                        .contact(new Contact().name("Alumnado").email("daw@etg.es")));
    }
}
















