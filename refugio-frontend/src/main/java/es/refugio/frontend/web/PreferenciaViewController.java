package es.refugio.frontend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.refugio.frontend.service.PreferenciaService;

import java.util.Map;

@Controller
@RequestMapping("/web/preferencias")
@RequiredArgsConstructor
/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Preferencia.
 *
 * @author Elisabeth
 * @author Diego
 */
public class PreferenciaViewController {

    private final PreferenciaService preferenciaService;

    @GetMapping("/encuesta")
    public String mostrarEncuesta(Model model) {
        return "fragments/content/encuestas/encuesta-preferencias :: content";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public String guardarPreferencias(@RequestBody Map<String, Object> payload) {
        try {
            preferenciaService.guardarPreferencias(payload);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @PostMapping("/omitir")
    @ResponseBody
    public String omitirPreferencias(@RequestBody Map<String, Object> payload) {
        try {
            // Creamos una preferencia "vacia" pero marcada como omitida
            payload.put("encuestaOmitida", true);
            preferenciaService.guardarPreferencias(payload);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
