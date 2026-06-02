package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST - Refugio de Animales")
                        .version("1.0.0")
                        .description("Documentación de la API REST del sistema de gestión del Refugio de Animales. " +
                                "Permite gestionar usuarios, voluntarios, animales, adopciones, donaciones y más.")
                        .contact(new Contact()
                                .name("Refugio de Animales")
                                .email("admin@refugio.es"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

















