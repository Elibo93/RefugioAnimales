package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebStorageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenemos la ruta absoluta de la carpeta uploads
        String uploadPath = Paths.get("uploads/animales").toAbsolutePath().toUri().toString();
        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }
        
        // Mapeamos la URL /api/v1/animales/images/** a la carpeta física
        registry.addResourceHandler("/api/v1/animales/images/**")
                .addResourceLocations(uploadPath);
    }
}
