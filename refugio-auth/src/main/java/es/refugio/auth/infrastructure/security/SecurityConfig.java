package es.refugio.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.refugio.auth.social_login.infrastructure.CustomOAuth2UserService;
import es.refugio.auth.social_login.infrastructure.OAuth2SuccessHandler;
import java.io.IOException;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

        private final JwtTokenProvider tokenProvider;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2SuccessHandler oauth2SuccessHandler;

        public SecurityConfig(JwtTokenProvider tokenProvider, JwtAuthenticationFilter jwtAuthenticationFilter,
                        CustomOAuth2UserService customOAuth2UserService, OAuth2SuccessHandler oauth2SuccessHandler) {
                this.tokenProvider = tokenProvider;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.customOAuth2UserService = customOAuth2UserService;
                this.oauth2SuccessHandler = oauth2SuccessHandler;
        }

        /**
         * Custom success handler que siempre redirige al usuario a través del
         * API Gateway, usando los headers X-Forwarded-Host y X-Forwarded-Proto
         * que el Gateway añade a cada petición.
         *
         * Sin esto, Spring Security redirige al host/IP del propio backend
         * (ej: http://192.168.0.202:8081/web/home) en lugar de al Gateway
         * (ej: https://localhost:8443/web/home), rompiendo la sesión.
         */
        @Bean
        public SimpleUrlAuthenticationSuccessHandler gatewayAwareSuccessHandler() {
                return new SimpleUrlAuthenticationSuccessHandler() {
                        @Override
                        protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response) {
                                // 1. Verificar si hay un parámetro de 'redirect' en la petición original
                                // Nota: En un flujo de login normal, el parámetro está en la URL del GET /login
                                // pero Spring Security lo descarta en el POST /login-post a menos que lo
                                // guardemos.
                                // Sin embargo, si el usuario vino de un link th:href="@{/login(redirect=...)}"
                                // el parámetro debería estar presente si el formulario de login lo incluye como
                                // hidden.

                                String redirect = request.getParameter("redirect");
                                if (redirect != null && !redirect.isBlank()) {
                                        return redirect;
                                }

                                // 2. Fallback al comportamiento estándar vía Gateway
                                String proto = request.getHeader("X-Forwarded-Proto");
                                String host = request.getHeader("X-Forwarded-Host");

                                if (proto != null && !proto.isBlank()
                                                && host != null && !host.isBlank()) {
                                        return proto + "://" + host + "/web/home";
                                }
                                return "/web/home";
                        }

                        @Override
                                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
                                        throws IOException, ServletException {

                                String token = tokenProvider.generateToken(authentication);
                                Cookie authCookie = new Cookie("JWT_TOKEN", token);
                                authCookie.setHttpOnly(true);
                                authCookie.setSecure(false); // establecer a true en producción con HTTPS
                                authCookie.setPath("/");
                                authCookie.setMaxAge(86400); // 1 día
                                response.addCookie(authCookie);

                                super.onAuthenticationSuccess(request, response, authentication);
                        }
                };
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // Recursos públicos y swagger
                                                .requestMatchers("/", "/error", "/login", "/login-post", "/registro",
                                                                "/css/**", "/js/**", "/img/**", "/images/**",
                                                                "/webjars/**", "/favicon.ico",
                                                                "/swagger-ui.html", "/swagger-ui/**", "/api-docs",
                                                                "/api-docs/**",
                                                                "/v3/api-docs", "/v3/api-docs/**",
                                                                "/oauth2/**", "/login/oauth2/**")
                                                .permitAll()
                                                // API pública (lectura de animales, donaciones y me-info sin login)
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/v1/animales/**", "/api/v1/me",
                                                                "/api/v1/donaciones/total")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/donaciones", "/api/v1/usuarios/publico",
                                                                "/api/v1/solicitudes-adopcion/publico/registro-y-adopcion")
                                                .permitAll()
                                                // API de tareas y voluntarios requiere roles específicos
                                                .requestMatchers("/api/v1/tareas/**").hasAnyRole("ADMIN", "VOLUNTARIO")
                                                .requestMatchers("/api/v1/historiales/**")
                                                .hasAnyRole("ADMIN", "VOLUNTARIO")
                                                // Resto de API requiere autenticación
                                                .requestMatchers("/api/**").authenticated()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oauth2SuccessHandler))
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login-post")
                                                // Custom handler que redirige al Gateway, no al backend directamente
                                                .successHandler(gatewayAwareSuccessHandler())
                                                .permitAll())
                                .httpBasic(basic -> {
                                })
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .deleteCookies("JWT_TOKEN")
                                                // Usamos un handler para redirigir respetando el header
                                                // X-Forwarded-Host del Gateway
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        String proto = request.getHeader("X-Forwarded-Proto");
                                                        String host = request.getHeader("X-Forwarded-Host");
                                                        String targetUrl = (proto != null && host != null)
                                                                        ? proto + "://" + host + "/login?logout"
                                                                        : "/login?logout";
                                                        response.sendRedirect(targetUrl);
                                                })
                                                .permitAll())
                                .exceptionHandling(ex -> ex
                                                // Para /api/** devolver 401 en lugar de redirect al login
                                                .defaultAuthenticationEntryPointFor(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                                                new AntPathRequestMatcher("/api/**")));

                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();

        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }
}
