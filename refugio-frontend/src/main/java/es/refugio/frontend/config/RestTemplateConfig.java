package es.refugio.frontend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate con:
     * 1. Interceptor de reenvío de cookie de sesión al backend.
     * 2. Soporte para LocalDateTime con JavaTimeModule.
     * 3. Ignora propiedades JSON desconocidas (tolerante a cambios del backend).
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(SessionCookieRelayInterceptor sessionCookieRelayInterceptor) {
        RestTemplate restTemplate = new RestTemplate();

        // Configurar ObjectMapper con soporte de fechas Java 8
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);
        StringHttpMessageConverter stringConverter =
                new StringHttpMessageConverter(StandardCharsets.UTF_8);
        AllEncompassingFormHttpMessageConverter formConverter = new AllEncompassingFormHttpMessageConverter();

        restTemplate.setMessageConverters(List.of(jsonConverter, stringConverter, formConverter));

        // Añadir el interceptor de sesión
        restTemplate.setInterceptors(List.of(sessionCookieRelayInterceptor));

        return restTemplate;
    }
}
