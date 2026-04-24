package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * ControllerAdvice que añade a TODOS los modelos de la aplicación:
 *
 * - currentUri      : URI de la petición actual (para marcar menú activo)
 * - showBack        : si mostrar el botón de volver
 * - currentUserId   : ID del usuario autenticado (null si no hay sesión)
 * - currentUserName : nombre completo del usuario (null si no hay sesión)
 * - currentUserRol  : rol del usuario (null si no hay sesión)
 * - isAuthenticated : boolean — true si hay sesión válida en el backend
 * - isAdmin         : boolean — true si el usuario es ROLE_ADMIN
 * - isVoluntario    : boolean — true si el usuario es ROLE_VOLUNTARIO
 *
 * La información de usuario se obtiene llamando a GET /api/v1/me en el backend,
 * con la cookie de sesión del navegador reenvíada automáticamente por
 * SessionCookieRelayInterceptor. Si el backend devuelve 401, el usuario
 * no está autenticado y todos los atributos de usuario quedan a null/false.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributesAdvice {

    private final RestTemplate restTemplate;

    @Value("${auth.api.url}")
    private String authUrl;

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
    public void injectCurrentUser(Model model) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> me = restTemplate.getForObject(
                    authUrl + "/v1/me", Map.class);

            if (me != null) {
                Object idObj = me.get("id");
                if (idObj instanceof Map) idObj = ((Map<?, ?>) idObj).get("value");
                model.addAttribute("currentUserId",   idObj);
                model.addAttribute("currentUserName", me.get("nombreCompleto"));
                model.addAttribute("currentUserRol",  me.get("rol"));
                model.addAttribute("isAuthenticated", true);

                String rol = String.valueOf(me.get("rol"));
                model.addAttribute("isAdmin",      rol.contains("ADMIN"));
                model.addAttribute("isVoluntario", rol.contains("VOLUNTARIO") || rol.contains("ADMIN"));
                model.addAttribute("isAdoptante",  rol.contains("ADOPTANTE"));
            } else {
                setAnonymous(model);
            }
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            // Sin sesión válida — usuario anónimo
            setAnonymous(model);
        } catch (Exception e) {
            // Backend no disponible u otro error — tratar como anónimo
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
    }
}
