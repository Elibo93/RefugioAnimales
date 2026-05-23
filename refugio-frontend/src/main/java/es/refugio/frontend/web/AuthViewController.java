package es.refugio.frontend.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.util.ErrorMessageExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthViewController {

    private final RestTemplate restTemplate;

    @Value("${auth.api.url}")
    private String authUrl;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("currentUri", "/login");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/login");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("currentUri", "/registro");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/registro");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/web/registro")
    public String procesarRegistro(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response) {

        try {
            // 1. Crear usuario en el servicio de Autenticación
            Map<String, Object> authBody = new HashMap<>();
            authBody.put("email", email);
            authBody.put("username", username);
            authBody.put("contrasena", password);
            authBody.put("rol", "ROLE_PUBLICO"); // El rol por defecto es Público/Simpatizante

            @SuppressWarnings("unchecked")
            Map<String, Object> createdUser = restTemplate.postForObject(authUrl + "/v1/usuarios/publico", authBody, Map.class);

            if (createdUser == null || createdUser.get("id") == null) {
                return "redirect:/registro?error=Error al crear usuario";
            }

            // NOTA: YA NO creamos un PerfilLegal aquí.
            // Perfilado progresivo: El perfil legal se creará únicamente cuando el usuario
            // decida convertirse en voluntario o adoptante.

            // 2. Inicio de sesión automático (Llamar a Auth Login para obtener la Cookie)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // El servicio de autenticación requiere el parámetro 'username' (mapeado a email en CustomUserDetailsService)
            String loginBody = "username=" + email + "&password=" + password;
            HttpEntity<String> entity = new HttpEntity<>(loginBody, headers);
            
            // Quitar /api de authUrl para obtener la base de /login-post
            String authBaseUrl = authUrl.substring(0, authUrl.lastIndexOf("/api"));
            String loginUrl = authBaseUrl + "/login-post";
            
            ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);

            // Extraer la cookie JWT_TOKEN de loginResponse
            List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                for (String cookieStr : cookies) {
                    if (cookieStr.startsWith("JWT_TOKEN=")) {
                        String value = cookieStr.substring(10, cookieStr.indexOf(";"));
                        Cookie cookie = new Cookie("JWT_TOKEN", value);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(86400);
                        response.addCookie(cookie);
                    }
                }
            }

            return "redirect:/web/home?registroExitoso";

        } catch (Exception e) {
            log.error("Error en registro: " + e.getMessage());
            return "redirect:/registro?error=" + ErrorMessageExtractor.extract(e);
        }
    }
}
