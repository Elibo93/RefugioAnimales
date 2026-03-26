package es.refugio.vista.infraestructure.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Proporciona atributos comunes a todas las vistas de los controladores.
 */
@ControllerAdvice
public class GlobalViewControllerAdvice {

    /**
     * Determina si se debe mostrar el botón de volver atrás.
     * Se muestra en todas las vistas excepto en el Home.
     */
    @ModelAttribute("showBack")
    public boolean showBack(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // Ocultar solo en el home (raíz o /web/home)
        return !(uri.equals("/") || uri.equals("/web/home") || uri.equals("/web/home/"));
    }

    /**
     * Proporciona la URI actual a las plantillas para resaltar elementos del menú.
     */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
