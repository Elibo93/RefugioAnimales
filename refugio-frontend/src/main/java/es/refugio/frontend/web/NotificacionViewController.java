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
import org.springframework.context.MessageSource;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NotificacionViewController {

    private final RestTemplate restTemplate;
    private final MessageSource messageSource;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping("/web/notificaciones")
    public String listar(Model model,
                        HttpServletRequest request,
                        java.util.Locale locale) {
        try {
            String url = apiUrl + "/v1/notificaciones/me";
            Object[] arr = restTemplate.getForObject(url, Object[].class);
            List<Object> notificaciones = arr != null ? Arrays.asList(arr) : List.of();
            
            for (Object obj : notificaciones) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> n = (Map<String, Object>) obj;
                    translateNotification(n, locale);
                }
            }
            
            model.addAttribute("notificaciones", notificaciones);
        } catch (Exception e) {
            model.addAttribute("notificaciones", List.of());
        }

        model.addAttribute("currentUri", WebRoutes.NOTIFICACIONES_BASE);
        
        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
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
    public ResponseEntity<Void> checkCelebraciones(java.util.Locale locale) {
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
                            
                            // Marcar como leída inmediatamente para que no se repita la celebración
                            restTemplate.put(apiUrl + "/v1/notificaciones/" + n.get("id") + "/leer", null);
                            
                            String mensaje = (String) n.get("mensaje");
                            
                            // Extraer la imagen basada en el texto del mensaje (antes de traducir)
                            String imageUrl = "";
                            if (mensaje.contains("Primeros Pasos")) { imageUrl = "/images/logros/primeros-pasos.png"; }
                            else if (mensaje.contains("Compromiso Firme")) { imageUrl = "/images/logros/compromiso-firme.png"; }
                            else if (mensaje.contains("Leyenda del Refugio")) { imageUrl = "/images/logros/leyenda-refugio.png"; }
                            else if (mensaje.contains("Corazón Generoso")) { imageUrl = "/images/logros/corazon-generoso.png"; }
                            else if (mensaje.contains("Mecenas de Huellas")) { imageUrl = "/images/logros/mecenas-huellas.png"; }
                            else if (mensaje.contains("Ángel Guardián")) { imageUrl = "/images/logros/angel-guardian.png"; }
                            
                            // Traducir notificación en el mapa
                            translateNotification(n, locale);
                            
                            String translatedTitle = (String) n.get("titulo");
                            String translatedMsg = (String) n.get("mensaje");
                            
                            HttpHeaders headers = new HttpHeaders();
                            headers.add("X-Celebrate", "true");
                            headers.add("X-Celebrate-Title", java.net.URLEncoder.encode(translatedTitle, java.nio.charset.StandardCharsets.UTF_8));
                            headers.add("X-Celebrate-Msg", java.net.URLEncoder.encode(translatedMsg, java.nio.charset.StandardCharsets.UTF_8));
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

    /**
     * Traduce una notificación al idioma del usuario.
     * Gestiona dos tipos de notificaciones:
     * 1. Texto hardcodeado en español por el backend (donaciones, logros) → traducción por contenido
     * 2. Claves i18n almacenadas directamente en BD (voluntario, adopción) → resolución por clave
     */
    private void translateNotification(Map<String, Object> n, java.util.Locale locale) {
        String titulo = (String) n.get("titulo");
        String mensaje = (String) n.get("mensaje");
        if (titulo == null) return;

        // Caso 1: El backend guardó claves i18n directamente (ej: notificacion.voluntario.*)
        // Las resolvemos aquí para que el template no las muestre como texto crudo
        String tituloResolved = messageSource.getMessage(titulo, null, (String) null, locale);
        if (tituloResolved != null) {
            n.put("titulo", tituloResolved);
            if (mensaje != null) {
                String mensajeResolved = messageSource.getMessage(mensaje, null, (String) null, locale);
                if (mensajeResolved != null) n.put("mensaje", mensajeResolved);
            }
            return; // Ya resuelto como clave, no procesar más
        }

        if (mensaje == null) return;

        // Caso 2: Texto hardcodeado en español por el backend (donaciones, logros)
        if (titulo.contains("Logro Desbloqueado")) {
            n.put("titulo", messageSource.getMessage("alert.achievement.title", null, titulo, locale));
            
            String keyName = null;
            String keyDesc = null;
            if (mensaje.contains("Primeros Pasos")) { keyName = "achievement.name.primeros_pasos"; keyDesc = "achievement.desc.primeros_pasos"; }
            else if (mensaje.contains("Compromiso Firme")) { keyName = "achievement.name.compromiso_firme"; keyDesc = "achievement.desc.compromiso_firme"; }
            else if (mensaje.contains("Leyenda del Refugio")) { keyName = "achievement.name.leyenda_refugio"; keyDesc = "achievement.desc.leyenda_refugio"; }
            else if (mensaje.contains("Coraz\u00f3n Generoso")) { keyName = "achievement.name.corazon_generoso"; keyDesc = "achievement.desc.corazon_generoso"; }
            else if (mensaje.contains("Mecenas de Huellas")) { keyName = "achievement.name.mecenas_huellas"; keyDesc = "achievement.desc.mecenas_huellas"; }
            else if (mensaje.contains("\u00c1ngel Guardi\u00e1n")) { keyName = "achievement.name.angel_guardian"; keyDesc = "achievement.desc.angel_guardian"; }
            
            if (keyName != null && keyDesc != null) {
                String localName = messageSource.getMessage(keyName, null, "", locale);
                String localDesc = messageSource.getMessage(keyDesc, null, "", locale);
                n.put("mensaje", messageSource.getMessage("alert.achievement.subtitle", new Object[]{ localName, localDesc }, mensaje, locale));
            }
        } else if (titulo.contains("Gracias por tu donaci\u00f3n")) {
            n.put("titulo", messageSource.getMessage("notification.donation.title", null, titulo, locale));
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(\\.\\d+)?)").matcher(mensaje);
            String amount = m.find() ? m.group(1) : "";
            n.put("mensaje", messageSource.getMessage("notification.donation.message", new Object[]{amount}, mensaje, locale));
        } else if (titulo.contains("Nueva Donaci\u00f3n Recibida")) {
            n.put("titulo", messageSource.getMessage("notification.donation.admin.title", null, titulo, locale));
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("([\\w.@+-]+).*?(\\d+\\.?\\d*)").matcher(mensaje);
            if (m.find()) {
                n.put("mensaje", messageSource.getMessage("notification.donation.admin.message",
                    new Object[]{ m.group(1), m.group(2) }, mensaje, locale));
            }
        }
    }
}
