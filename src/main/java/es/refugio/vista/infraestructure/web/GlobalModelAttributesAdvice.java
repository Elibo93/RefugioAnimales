package es.refugio.vista.infraestructure.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
public class GlobalModelAttributesAdvice {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("showBack")
    public boolean showBack(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // Solo ocultamos el botón de volver en las variantes de la home
        return !(uri.equals("/") || uri.equals("/web/home") || uri.equals("/web/home/"));
    }
}
















