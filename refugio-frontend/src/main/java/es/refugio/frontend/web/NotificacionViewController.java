package es.refugio.frontend.web;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificacionViewController {

    private final RestTemplate restTemplate;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping("/web/notificaciones")
    public String listar(Model model, 
                        HttpServletRequest request) {
        try {
            String url = apiUrl + "/v1/notificaciones/me";
            Object[] arr = restTemplate.getForObject(url, Object[].class);
            List<Object> notificaciones = arr != null ? Arrays.asList(arr) : List.of();
            model.addAttribute("notificaciones", notificaciones);
        } catch (Exception e) {
            model.addAttribute("notificaciones", List.of());
        }

        model.addAttribute("currentUri", WebRoutes.NOTIFICACIONES_BASE);
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Notificacion_LIST.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Notificacion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/web/notificaciones/{id}/leer")
    public ResponseEntity<String> marcarComoLeida(@PathVariable Integer id) {
        try {
            restTemplate.exchange(apiUrl + "/v1/notificaciones/" + id + "/leer", HttpMethod.PUT, null, Void.class);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.add("HX-Trigger", "notificacionLeida");
            return new org.springframework.http.ResponseEntity<>("", headers, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }

    @GetMapping("/web/notificaciones/count")
    @ResponseBody
    public String obtenerConteoNoLeidas() {
        try {
            Long count = restTemplate.getForObject(apiUrl + "/v1/notificaciones/me/count", Long.class);
            if (count != null && count > 0) {
                return "<span class='notification-badge'>" + count + "</span>";
            }
        } catch (Exception e) {}
        return "";
    }

    @DeleteMapping("/web/notificaciones/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            restTemplate.delete(apiUrl + "/v1/notificaciones/" + id);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.add("HX-Trigger", "notificacionLeida");
            return new org.springframework.http.ResponseEntity<>("", headers, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
