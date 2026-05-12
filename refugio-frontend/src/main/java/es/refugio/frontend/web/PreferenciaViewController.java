package es.refugio.frontend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/web/preferencias")
@RequiredArgsConstructor
public class PreferenciaViewController {

    private final RestTemplate restTemplate;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping("/encuesta")
    public String mostrarEncuesta(Model model) {
        return "fragments/content/encuesta-preferencias :: content";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public String guardarPreferencias(@RequestBody Map<String, Object> payload) {
        try {
            restTemplate.postForObject(apiUrl + "/v1/preferencias", payload, Map.class);
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
            restTemplate.postForObject(apiUrl + "/v1/preferencias", payload, Map.class);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
