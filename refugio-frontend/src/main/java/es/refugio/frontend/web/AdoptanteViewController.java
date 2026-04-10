package es.refugio.frontend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import es.refugio.frontend.web.constants.WebRoutes;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AdoptanteViewController — gestiona el flujo de conversión de usuario a
 * adoptante.
 *
 * Delega completamente en el backend a través de la API REST.
 * El backend tiene los endpoints /api/v1/adoptantes para este proceso.
 */
@Controller
@RequiredArgsConstructor
public class AdoptanteViewController {

    private final RestTemplate restTemplate;

    @Value("${backend.api.url}")
    private String apiUrl;

    /**
     * Modal de conversión a adoptante (cuando el usuario no tiene perfil de
     * adoptante).
     */
    @GetMapping(WebRoutes.ADOPTANTES_MODAL_CONVERTIR)
    public String modalConvertir(@RequestParam Integer animalId, Model model) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of());
        }
        return "fragments/modals/modal-conversion-directa :: modal";
    }

    /**
     * Procesa la conversión: crea el perfil de adoptante y la solicitud de
     * adopción.
     * Delega en el backend RESTful.
     */
    @PostMapping(WebRoutes.ADOPTANTES_CONVERTIR_Y_SOLICITAR)
    public String convertirYSolicitar(
            @RequestParam Integer animalId,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String comentario,
            Model model) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("animalId", animalId);
            body.put("nombre", nombre);
            body.put("apellido", apellido);
            body.put("dni", dni != null ? dni : "");
            body.put("direccion", direccion != null ? direccion : "");
            body.put("fechaNacimiento", fechaNacimiento != null ? fechaNacimiento : "");
            body.put("comentario", comentario != null ? comentario : "Solicitud registrada");
            body.put("fecha", LocalDateTime.now().toString());

            restTemplate.postForObject(apiUrl + "/v1/adoptantes/convertir-y-solicitar", body, Object.class);
            return "fragments/content/solicitud-creada :: success-modal";
        } catch (Exception e) {
            try {
                Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
                model.addAttribute("animal", animal);
            } catch (Exception ignored) {
            }
            model.addAttribute("usuario", Map.of("nombre", nombre, "apellido", apellido));
            model.addAttribute("errorMessage", "Error al procesar la solicitud: " + e.getMessage());
            return "fragments/modals/modal-conversion-directa :: modal";
        }
    }
}
