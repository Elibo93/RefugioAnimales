package es.refugio.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad del Frontend.
 *
 * El frontend NO gestiona la autenticación — eso lo hace el Backend.
 * El login/logout están en el Backend y se accede a ellos a través del Gateway.
 * El frontend solo permite todas las peticiones y delega la autorización
 * en las respuestas HTTP que recibe del Backend (401 → redirige al login).
 */
@Configuration
@EnableWebSecurity
public class FrontendSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Todo se permite — el backend es quien decide si el usuario está autenticado
                        .anyRequest().permitAll())
                // Sin formulario de login propio: el login está en el Backend (ruta /login →
                // gateway → backend)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * UserDetailsService vacío — necesario para que Spring Boot no genere
     * una contraseña aleatoria por consola al arrancar.
     * El frontend no autentica usuarios directamente.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Sin usuarios locales — la autenticación la gestiona el backend
        return new InMemoryUserDetailsManager();
    }
}
