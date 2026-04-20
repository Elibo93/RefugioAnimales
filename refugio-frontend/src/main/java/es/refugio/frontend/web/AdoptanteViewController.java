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

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.ADOPTANTES_BASE)
    public String listar(Model model) {
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        Map<String, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), u);
                }
            }
        }

        model.addAttribute(es.refugio.frontend.web.enums.ModelAttribute.Adoptante_LIST.getName(), adoptantes);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("currentUri", WebRoutes.ADOPTANTES_BASE);
        model.addAttribute("showBack", false);
        model.addAttribute(es.refugio.frontend.web.enums.ModelAttribute.FRAGMENTO_CONTENIDO.getName(), es.refugio.frontend.web.enums.FragmentoContenido.Adoptante_LIST.getPath());
        return es.refugio.frontend.web.enums.ThymTemplates.MAIN_LAYOUT.getPath();
    }

    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { return List.of(); }
    }

    @GetMapping(WebRoutes.ADOPTANTES_NUEVO)
    public String nuevo(Model model) {
        model.addAttribute(es.refugio.frontend.web.enums.ModelAttribute.SINGLE_Adoptante.getName(), new HashMap<>());
        model.addAttribute("currentUri", WebRoutes.ADOPTANTES_BASE);
        model.addAttribute("showBack", true);
        model.addAttribute(es.refugio.frontend.web.enums.ModelAttribute.FRAGMENTO_CONTENIDO.getName(), es.refugio.frontend.web.enums.FragmentoContenido.Adoptante_FORM.getPath());
        return es.refugio.frontend.web.enums.ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ADOPTANTES_NUEVO)
    public String guardarNuevo(
            @RequestParam Integer usuarioId,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            Model model) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            body.put("dni", dni);
            body.put("direccion", direccion);
            body.put("fechaNacimiento", fechaNacimiento);
            restTemplate.postForObject(apiUrl + "/v1/adoptantes", body, Object.class);
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (Exception e) {
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        }
    }

    @PostMapping(WebRoutes.ADOPTANTES_EDITAR)
    public String guardarEdicion(
            @PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            Model model) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            body.put("dni", dni);
            body.put("direccion", direccion);
            body.put("fechaNacimiento", fechaNacimiento);
            restTemplate.put(apiUrl + "/v1/adoptantes/" + id, body);
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (Exception e) {
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        }
    }

    @PostMapping(WebRoutes.ADOPTANTES_ELIMINAR)
    public String eliminar(@PathVariable Integer id, Model model) {
        try {
            restTemplate.delete(apiUrl + "/v1/adoptantes/" + id);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @PostMapping(WebRoutes.ADOPTANTES_APROBAR)
    public String aprobar(@PathVariable Integer id) {
        try {
            restTemplate.patchForObject(apiUrl + "/v1/adoptantes/" + id + "/approve", null, Object.class);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @PostMapping(WebRoutes.ADOPTANTES_RECHAZAR)
    public String rechazar(@PathVariable Integer id) {
        try {
            restTemplate.patchForObject(apiUrl + "/v1/adoptantes/" + id + "/reject", null, Object.class);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @GetMapping(WebRoutes.ADOPTANTES_PDF)
    public String exportPdf(Model model) {
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        Map<String, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), u);
                }
            }
        }
        model.addAttribute(es.refugio.frontend.web.enums.ModelAttribute.Adoptante_LIST.getName(), adoptantes);
        model.addAttribute("usuariosMap", usuariosMap);
        return es.refugio.frontend.web.enums.ThymTemplates.Adoptante_LIST_PDF.getPath();
    }

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
