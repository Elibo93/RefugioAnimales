package es.refugio.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Recursos públicos y swagger
                        .requestMatchers("/", "/error", "/login", "/login-post", "/registro",
                                "/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**", "/favicon.ico",
                                "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**",
                                "/v3/api-docs", "/v3/api-docs/**")
                        .permitAll()
                        // API pública (lectura de animales y donaciones sin login)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/animales/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/donaciones").permitAll()
                        // API de tareas y voluntarios requiere roles específicos
                        .requestMatchers("/api/v1/tareas/**").hasAnyRole("ADMIN", "VOLUNTARIO")
                        .requestMatchers("/api/v1/historiales/**").hasAnyRole("ADMIN", "VOLUNTARIO")
                        // Resto de API requiere autenticación
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-post")
                        // Tras login exitoso redirigir a raíz → Gateway lo enviará al frontend
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .httpBasic(basic -> {
                })
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
                .exceptionHandling(ex -> ex
                        // Para /api/** devolver 401 en lugar de redirect al login
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")));

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
