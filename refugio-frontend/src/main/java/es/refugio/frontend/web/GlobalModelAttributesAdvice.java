package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
                if (idObj instanceof Map)
                    idObj = ((Map<?, ?>) idObj).get("value");
                // Garantizar que el ID sea siempre un Integer plano, nunca un Map
                Integer userId = (idObj instanceof Number) ? ((Number) idObj).intValue() : null;

                System.out.println("DEBUG: Usuario identificado: " + me.get("email") + " con ID: " + userId + " y ROL: "
                        + me.get("rol"));
                model.addAttribute("currentUserId", userId);
                model.addAttribute("currentUserRol", me.get("rol"));
                model.addAttribute("isAuthenticated", true);

                // Fetch Name from Backend PerfilLegal
                try {
                    Map<String, Object> perfil = restTemplate.getForObject(
                            apiUrl + "/v1/perfiles-legales/usuario/" + userId, Map.class);
                    if (perfil != null) {
                        model.addAttribute("currentUserName", perfil.get("nombre") + " " + perfil.get("apellido"));
                    } else {
                        model.addAttribute("currentUserName", me.get("username"));
                    }
                } catch (Exception e) {
                    model.addAttribute("currentUserName", me.get("username"));
                }

                String rol = me.get("rol") != null ? String.valueOf(me.get("rol")).toUpperCase() : "";
                boolean isAdmin = rol.contains("ADMIN");
                boolean isVol = rol.contains("VOLUNTARIO") || isAdmin;
                boolean isAdop = rol.contains("ADOPTANTE");

                model.addAttribute("isAdmin", isAdmin);
                model.addAttribute("isVoluntario", isVol);
                model.addAttribute("isAdoptante", isAdop);
                model.addAttribute("isPublico", rol.contains("PUBLICO"));

                // 3. Atributos de solicitudes (para prevenir duplicados)
                try {
                    // Usamos el endpoint personal para que el badge "SOLICITADO" sea solo del
                    // usuario actual
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> solicitudes = restTemplate
                            .getForObject(apiUrl + "/v1/solicitudes-adopcion/mis-solicitudes", List.class);
                    if (solicitudes != null) {
                        Set<Integer> animalesSolicitadosIds = solicitudes.stream()
                                .map(s -> {
                                    Object val = s.get("animalId");
                                    if (val instanceof Number)
                                        return ((Number) val).intValue();
                                    return null;
                                })
                                .filter(id -> id != null)
                                .collect(Collectors.toSet());
                        model.addAttribute("animalesSolicitadosIds", animalesSolicitadosIds);
                    } else {
                        model.addAttribute("animalesSolicitadosIds", new HashSet<>());
                    }
                } catch (Exception e) {
                    model.addAttribute("animalesSolicitadosIds", new HashSet<>());
                }

                // 4. Check Preferences
                try {
                    Map<String, Object> prefs = restTemplate.getForObject(apiUrl + "/v1/preferencias/usuario/" + userId, Map.class);
                    model.addAttribute("hasPreferences", prefs != null);
                } catch (Exception e) {
                    model.addAttribute("hasPreferences", false);
                }
            } else {
                setAnonymous(model);
            }
        } catch (Exception e) {
            System.err.println("DEBUG ERROR: Error en GlobalModelAttributesAdvice: " + e.getMessage());
            setAnonymous(model);
        }
    }

    private void setAnonymous(Model model) {
        model.addAttribute("currentUserId", null);
        model.addAttribute("currentUserName", null);
        model.addAttribute("currentUserRol", null);
        model.addAttribute("isAuthenticated", false);
        model.addAttribute("isAdmin", false);
        model.addAttribute("isVoluntario", false);
        model.addAttribute("isAdoptante", false);
        model.addAttribute("isPublico", false);
        model.addAttribute("animalesSolicitadosIds", new HashSet<>());
        model.addAttribute("hasPreferences", true); // Default true to hide banner for anonymous
    }
}
