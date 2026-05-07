package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributesAdvice {

    private final RestTemplate restTemplate;

    @Value("${auth.api.url}")
    private String authUrl;

    @Value("${backend.api.url}")
    private String apiUrl;

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("showBack")
    public boolean showBack(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !(uri.equals("/") || uri.equals("/web/home") || uri.equals("/web/home/"));
    }

    /**
     * Llama al backend para obtener los datos del usuario autenticado y los
     * inyecta en el modelo de todas las vistas.
     * Los templates usan th:if="${isAdmin}" en lugar de sec:authorize.
     */
    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {
        // 1. Atributos de navegación
        String uri = request.getRequestURI();
        model.addAttribute("currentUri", uri);
        model.addAttribute("showBack", !(uri.equals("/") || uri.equals("/web/home") || uri.equals("/web/home/")));

        // 2. Atributos de usuario (inyectados si hay sesión)
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> me = restTemplate.getForObject(authUrl + "/v1/me", Map.class);

            if (me != null) {
                Object idObj = me.get("id");
                if (idObj instanceof Map) idObj = ((Map<?, ?>) idObj).get("value");
                model.addAttribute("currentUserId",   idObj);
                model.addAttribute("currentUserRol",  me.get("rol"));
                model.addAttribute("isAuthenticated", true);

                // Fetch Name from Backend PerfilLegal
                try {
                    Map<String, Object> perfil = restTemplate.getForObject(
                            apiUrl + "/v1/perfiles-legales/usuario/" + idObj, Map.class);
                    if (perfil != null) {
                        model.addAttribute("currentUserName", perfil.get("nombre") + " " + perfil.get("apellido"));
                    } else {
                        model.addAttribute("currentUserName", me.get("email")); // Fallback to email
                    }
                } catch (Exception e) {
                    model.addAttribute("currentUserName", me.get("email")); // Fallback to email
                }

                String rol = String.valueOf(me.get("rol"));
                model.addAttribute("isAdmin",      rol.contains("ADMIN"));
                model.addAttribute("isVoluntario", rol.contains("VOLUNTARIO") || rol.contains("ADMIN"));
                model.addAttribute("isAdoptante",  rol.contains("ADOPTANTE"));
                model.addAttribute("isPublico",    rol.contains("PUBLICO"));
            } else {
                setAnonymous(model);
            }
        } catch (Exception e) {
            setAnonymous(model);
        }
    }

    private void setAnonymous(Model model) {
        model.addAttribute("currentUserId",   null);
        model.addAttribute("currentUserName", null);
        model.addAttribute("currentUserRol",  null);
        model.addAttribute("isAuthenticated", false);
        model.addAttribute("isAdmin",         false);
        model.addAttribute("isVoluntario",    false);
        model.addAttribute("isAdoptante",     false);
        model.addAttribute("isPublico",       false);
    }
}
