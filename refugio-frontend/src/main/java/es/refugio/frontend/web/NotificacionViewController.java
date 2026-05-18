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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            HttpHeaders headers = new HttpHeaders();
            headers.add("HX-Trigger", "notificacionLeida");
            return new ResponseEntity<>("", headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
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
            HttpHeaders headers = new HttpHeaders();
            headers.add("HX-Trigger", "notificacionLeida");
            return new ResponseEntity<>("", headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }

    @GetMapping("/web/notificaciones/check-celebraciones")
    public ResponseEntity<Void> checkCelebraciones() {
        try {
            String url = apiUrl + "/v1/notificaciones/me";
            Object[] arr = restTemplate.getForObject(url, Object[].class);
            if (arr != null) {
                for (Object o : arr) {
                    if (o instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> n = (Map<String, Object>) o;
                        // Si es un logro desbloqueado y NO ha sido leído aún
                        if ("LOGRO_DESBLOQUEADO".equals(n.get("tipo")) && Boolean.FALSE.equals(n.get("leida"))) {
                            
                            String titulo = (String) n.get("titulo");
                            String mensaje = (String) n.get("mensaje");
                            
                            // Marcar como leída inmediatamente para que no se repita la celebración
                            restTemplate.put(apiUrl + "/v1/notificaciones/" + n.get("id") + "/leer", null);
                            
                            // Extraer la imagen basada en el texto del mensaje
                            String imageUrl = "";
                            if (mensaje.contains("Primeros Pasos")) imageUrl = "/images/logros/primeros-pasos.png";
                            else if (mensaje.contains("Compromiso Firme")) imageUrl = "/images/logros/compromiso-firme.png";
                            else if (mensaje.contains("Leyenda del Refugio")) imageUrl = "/images/logros/leyenda-refugio.png";
                            else if (mensaje.contains("Corazón Generoso")) imageUrl = "/images/logros/corazon-generoso.png";
                            else if (mensaje.contains("Mecenas de Huellas")) imageUrl = "/images/logros/mecenas-huellas.png";
                            else if (mensaje.contains("Ángel Guardián")) imageUrl = "/images/logros/angel-guardian.png";
                            
                            HttpHeaders headers = new HttpHeaders();
                            headers.add("X-Celebrate", "true");
                            headers.add("X-Celebrate-Title", java.net.URLEncoder.encode(titulo, java.nio.charset.StandardCharsets.UTF_8));
                            headers.add("X-Celebrate-Msg", java.net.URLEncoder.encode(mensaje, java.nio.charset.StandardCharsets.UTF_8));
                            if (!imageUrl.isEmpty()) {
                                headers.add("X-Celebrate-Img", java.net.URLEncoder.encode(imageUrl, java.nio.charset.StandardCharsets.UTF_8));
                            }
                            headers.add("HX-Trigger", "notificacionLeida"); // Actualizar badge también
                            
                            return new ResponseEntity<>(headers, HttpStatus.OK);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Silencioso
        }
        return ResponseEntity.noContent().build();
    }
}
