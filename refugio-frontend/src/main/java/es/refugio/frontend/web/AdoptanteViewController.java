package es.refugio.frontend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import es.refugio.frontend.web.constants.WebRoutes;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AdoptanteViewController.class);

    private final RestTemplate restTemplate;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.ADOPTANTES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model, 
                        @RequestParam(required = false) String q,
                        HttpServletRequest request) {
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
        List<Object> perfilesLegales = fetchList("/v1/perfiles-legales");

        Map<String, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), u);
                }
            }
        }

        Map<String, Object> perfilesMap = new HashMap<>();
        for (Object p : perfilesLegales) {
            if (p instanceof Map) {
                Object uId = ((Map<?, ?>) p).get("usuarioId");
                if (uId instanceof Number) {
                    perfilesMap.put(String.valueOf(((Number) uId).intValue()), p);
                }
            }
        }

        // Filtrado por Búsqueda (q)
        if (q != null && !q.trim().isEmpty()) {
            String query = q.toLowerCase();
            adoptantes = adoptantes.stream()
                .filter(a -> {
                    if (a instanceof Map) {
                        Map<?, ?> am = (Map<?, ?>) a;
                        String uId = String.valueOf(am.get("usuarioId"));
                        Map<?, ?> user = (Map<?, ?>) usuariosMap.get(uId);
                        Map<?, ?> legal = (Map<?, ?>) perfilesMap.get(uId);
                        
                        String username = user != null ? String.valueOf(user.get("username")).toLowerCase() : "";
                        String email = user != null ? String.valueOf(user.get("email")).toLowerCase() : "";
                        String nombre = legal != null ? String.valueOf(legal.get("nombre")).toLowerCase() : "";
                        String apellido = legal != null ? String.valueOf(legal.get("apellido")).toLowerCase() : "";
                        String dni = legal != null ? String.valueOf(legal.get("dni")).toLowerCase() : "";
                        
                        return username.contains(query) || email.contains(query) || 
                               nombre.contains(query) || apellido.contains(query) || dni.contains(query);
                    }
                    return false;
                }).toList();
        }

        model.addAttribute(es.refugio.frontend.web.enums.ModelAttribute.Adoptante_LIST.getName(), adoptantes);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("perfilesMap", perfilesMap);
        model.addAttribute("query", q);
        model.addAttribute("currentUri", WebRoutes.ADOPTANTES_BASE);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return es.refugio.frontend.web.enums.FragmentoContenido.Adoptante_LIST.getPath() + " :: list-body";
        }

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
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevo(Model model, HttpServletRequest request) {
        model.addAttribute(ModelAttribute.SINGLE_Adoptante.getName(), new HashMap<>());
        model.addAttribute("currentUri", WebRoutes.ADOPTANTES_BASE);
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Adoptante_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adoptante_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ADOPTANTES_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    @SuppressWarnings("unchecked")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        try {
            Map<String, Object> adoptante = restTemplate.getForObject(apiUrl + "/v1/adoptantes/" + id, Map.class);
            model.addAttribute(ModelAttribute.SINGLE_Adoptante.getName(), adoptante);
            
            if (adoptante != null) {
                Object uId = adoptante.get("usuarioId");
                Map<String, Object> user = restTemplate.getForObject(authUrl + "/v1/usuarios/" + uId, Map.class);
                if (user != null) {
                    model.addAttribute("userEmail", user.get("email"));
                }
                
                // Fetch PerfilLegal
                try {
                    Map<String, Object> perfil = restTemplate.getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + uId, Map.class);
                    if (perfil != null) {
                        model.addAttribute("nombreCompleto", perfil.get("nombre") + " " + perfil.get("apellido"));
                        model.addAttribute("userPhone", perfil.get("telefono"));
                        model.addAttribute("userDni", perfil.get("dni"));
                        model.addAttribute("userDireccion", perfil.get("direccion"));
                        model.addAttribute("userFechaNacimiento", perfil.get("fechaNacimiento"));
                    }
                } catch (Exception e) {
                    logger.warn("No se encontró PerfilLegal para usuario " + uId);
                }
            }

            model.addAttribute("currentUri", WebRoutes.ADOPTANTES_EDITAR);
            model.addAttribute("estados", List.of("PENDIENTE", "APROBADO", "RECHAZADO"));
            
            if ("true".equals(request.getHeader("HX-Request"))) {
                return FragmentoContenido.Adoptante_FORM.getPath() + " :: content";
            }
        } catch (Exception e) {
            logger.error("Error al cargar adoptante para editar: " + e.getMessage());
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adoptante_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ADOPTANTES_NUEVO)
    public String guardarNuevo(
            @RequestParam Integer usuarioId,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam String fechaNacimiento,
            RedirectAttributes redirectAttributes) {
        try {
            // 1. Crear/Actualizar PerfilLegal (Fuente de verdad para identidad)
            Map<String, Object> bodyPerfil = new HashMap<>();
            bodyPerfil.put("usuarioId", usuarioId);
            bodyPerfil.put("nombre", nombre);
            bodyPerfil.put("apellido", apellido);
            bodyPerfil.put("dni", dni);
            bodyPerfil.put("direccion", direccion);
            bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
            bodyPerfil.put("fechaNacimiento", fechaNacimiento);
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);

            // 2. Crear Perfil de Adoptante (Rol operativo)
            Map<String, Object> bodyAdoptante = new HashMap<>();
            bodyAdoptante.put("usuarioId", usuarioId);
            restTemplate.postForObject(apiUrl + "/v1/adoptantes", bodyAdoptante, Object.class);

            redirectAttributes.addFlashAttribute("successMessage", "Adoptante creado correctamente");
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (Exception e) {
            logger.error("Error al crear adoptante: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear adoptante: " + e.getMessage());
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        }
    }

    @PostMapping(WebRoutes.ADOPTANTES_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    public String guardarEdicion(
            @PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam(required = false) String estadoValidacion,
            RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            if (estadoValidacion != null) {
                body.put("estadoValidacion", estadoValidacion);
            }
            restTemplate.put(apiUrl + "/v1/adoptantes/" + id, body);

            // 2. Actualizar PerfilLegal
            Map<String, Object> bodyPerfil = new HashMap<>();
            bodyPerfil.put("usuarioId", usuarioId);
            bodyPerfil.put("nombre", nombre);
            bodyPerfil.put("apellido", apellido);
            bodyPerfil.put("dni", dni);
            bodyPerfil.put("direccion", direccion);
            bodyPerfil.put("fechaNacimiento", fechaNacimiento);
            // El teléfono se mantiene o se añade campo al form si es necesario
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);

            redirectAttributes.addFlashAttribute("successMessage", "Perfil de adoptante actualizado correctamente");
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (org.springframework.web.client.RestClientResponseException e) {
            logger.error("Error del backend al actualizar adoptante: " + e.getResponseBodyAsString());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getResponseBodyAsString());
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar adoptante: " + e.getMessage());
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        }
    }

    @PostMapping(WebRoutes.ADOPTANTES_ELIMINAR)
    @PreAuthorize("hasRole('ADMIN')")
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
        List<Object> perfilesLegales = fetchList("/v1/perfiles-legales");

        Map<String, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), u);
                }
            }
        }

        Map<String, Object> perfilesMap = new HashMap<>();
        for (Object p : perfilesLegales) {
            if (p instanceof Map) {
                Object uId = ((Map<?, ?>) p).get("usuarioId");
                if (uId instanceof Number) {
                    perfilesMap.put(String.valueOf(((Number) uId).intValue()), p);
                }
            }
        }

        model.addAttribute(es.refugio.frontend.web.enums.ModelAttribute.Adoptante_LIST.getName(), adoptantes);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("perfilesMap", perfilesMap);
        return es.refugio.frontend.web.enums.ThymTemplates.Adoptante_LIST_PDF.getPath();
    }

}
