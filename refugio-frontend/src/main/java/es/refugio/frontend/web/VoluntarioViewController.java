package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class VoluntarioViewController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(VoluntarioViewController.class);

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.VOLUNTARIOS_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model, 
                        @RequestParam(required = false) String q,
                        HttpServletRequest request) {
        List<Object> voluntarios = fetchList("/v1/voluntarios");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
        List<Object> perfilesLegales = fetchList("/v1/perfiles-legales");

        // Mapa auxiliar para asociar voluntarios con sus datos de usuario por ID
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
            voluntarios = voluntarios.stream()
                .filter(v -> {
                    if (v instanceof Map) {
                        Map<?, ?> vm = (Map<?, ?>) v;
                        String uId = String.valueOf(vm.get("usuarioId"));
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

        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), voluntarios);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("perfilesMap", perfilesMap);
        model.addAttribute("query", q);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_BASE);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Voluntario_LIST.getPath() + " :: list-body";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/web/voluntarios/sugerencias")
    public String sugerencias(@RequestParam(required = false) String q, Model model) {
        if (q == null || q.trim().isEmpty()) {
            return FragmentoContenido.VOLUNTARIO_SUGERENCIAS.getPath() + " :: suggestions";
        }

        List<Object> voluntarios = fetchList("/v1/voluntarios");
        List<Object> perfiles = fetchList("/v1/perfiles-legales");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        Map<Integer, Map<String, Object>> perfilesMap = new HashMap<>();
        for (Object p : perfiles) {
            if (p instanceof Map) {
                Object uId = ((Map<?, ?>) p).get("usuarioId");
                if (uId instanceof Number) perfilesMap.put(((Number) uId).intValue(), (Map<String, Object>) p);
            }
        }

        Map<Integer, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) usuariosMap.put(((Number) id).intValue(), (Map<String, Object>) u);
            }
        }

        String query = q.toLowerCase();
        List<Map<String, Object>> voluntariosEncontrados = new java.util.ArrayList<>();

        for (Object v : voluntarios) {
            if (v instanceof Map) {
                Map<String, Object> vm = (Map<String, Object>) v;
                Object uIdRaw = vm.get("usuarioId");
                if (uIdRaw instanceof Map) uIdRaw = ((Map<?, ?>) uIdRaw).get("value");
                
                if (uIdRaw instanceof Number) {
                    int uId = ((Number) uIdRaw).intValue();
                    Map<String, Object> perfil = perfilesMap.get(uId);
                    Map<String, Object> user = usuariosMap.get(uId);

                    String nombre = perfil != null && perfil.get("nombre") != null ? String.valueOf(perfil.get("nombre")) : "";
                    String apellido = perfil != null && perfil.get("apellido") != null ? String.valueOf(perfil.get("apellido")) : "";
                    String email = user != null && user.get("email") != null ? String.valueOf(user.get("email")) : "";
                    String username = user != null && user.get("username") != null ? String.valueOf(user.get("username")) : "";

                    if (nombre.toLowerCase().contains(query) || apellido.toLowerCase().contains(query) || 
                        email.toLowerCase().contains(query) || username.toLowerCase().contains(query)) {
                        
                        Map<String, Object> suggestion = new HashMap<>();
                        Object vId = vm.get("id");
                        if (vId instanceof Map) vId = ((Map<?, ?>) vId).get("value");
                        
                        suggestion.put("id", vId); 
                        suggestion.put("nombre", nombre);
                        suggestion.put("apellido", apellido);
                        suggestion.put("email", email);
                        suggestion.put("username", username);
                        voluntariosEncontrados.add(suggestion);
                    }
                }
            }
        }

        model.addAttribute("voluntariosEncontrados", voluntariosEncontrados);
        return FragmentoContenido.VOLUNTARIO_SUGERENCIAS.getPath() + " :: suggestions";
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    public String formulario(Model model, HttpServletRequest request) {
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), new HashMap<String, Object>());
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_NUEVO);
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Voluntario_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("unchecked")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        try {
            Map<String, Object> voluntario = restTemplate.getForObject(apiUrl + "/v1/voluntarios/" + id, Map.class);
            model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), voluntario);
            
            if (voluntario != null && voluntario.get("usuarioId") != null) {
                Object uId = voluntario.get("usuarioId");
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
        } catch (Exception e) {
            logger.error("Error al cargar voluntario/usuario para editar: " + e.getMessage());
        }

        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_EDITAR);
        model.addAttribute("isAdmin", true);
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Voluntario_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }


    @PostMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    public String crearVoluntario(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam String disponibilidad,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String contrasena,
            @RequestParam(required = false) String especialidad,
            RedirectAttributes redirectAttributes) {

        Integer finalUsuarioId = idUsuario;

        // 1. Si no hay usuarioId, registramos al usuario primero
        if (finalUsuarioId == null) {
            Map<String, Object> userBody = new HashMap<>();
            userBody.put("email", email);
            userBody.put("contrasena", contrasena);
            userBody.put("rol", "ROLE_VOLUNTARIO");

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> createdUser = restTemplate.postForObject(authUrl + "/v1/usuarios", userBody, Map.class);
                if (createdUser != null && createdUser.get("id") != null) {
                    finalUsuarioId = (Integer) createdUser.get("id");
                }
            } catch (Exception e) {
                logger.error("Error al registrar usuario para voluntario: " + e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la cuenta de usuario.");
                return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
            }
        }        // 2. Asegurar PerfilLegal (Identidad)
        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", finalUsuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("direccion", (direccion != null) ? direccion : "");
        bodyPerfil.put("fechaNacimiento", (fechaNacimiento != null) ? fechaNacimiento : "2000-01-01");
        
        try {
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);
        } catch (Exception e) {
            logger.error("Error al sincronizar PerfilLegal en creación: " + e.getMessage());
        }

        // 3. Registrar el perfil de voluntario (Rol operativo)
        Map<String, Object> bodyVol = new HashMap<>();
        bodyVol.put("usuarioId", finalUsuarioId);
        bodyVol.put("disponibilidad", disponibilidad);
        bodyVol.put("especialidad", especialidad);

        try {
            restTemplate.postForObject(apiUrl + "/v1/voluntarios", bodyVol, Object.class);
            redirectAttributes.addFlashAttribute("successMessage", "¡Solicitud enviada con éxito! El equipo revisará tu perfil pronto.");
        } catch (Exception e) {
            logger.error("Error al registrar perfil de voluntario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el perfil de voluntario.");
        }
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarVoluntario(@PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String disponibilidad,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String especialidad,
            RedirectAttributes redirectAttributes) {

        // 1. Actualización de disponibilidad en el servicio de backend
        Map<String, Object> bodyVol = new HashMap<>();
        bodyVol.put("disponibilidad", disponibilidad);
        bodyVol.put("especialidad",   especialidad);
        restTemplate.put(apiUrl + "/v1/voluntarios/" + id, bodyVol);

        // 2. Actualización de PerfilLegal
        try {
            Map<String, Object> bodyPerfil = new HashMap<>();
            bodyPerfil.put("usuarioId", usuarioId);
            bodyPerfil.put("nombre", nombre != null ? nombre : ""); // Necesitamos el nombre en el form
            bodyPerfil.put("apellido", apellido != null ? apellido : ""); 
            bodyPerfil.put("dni", dni);
            bodyPerfil.put("telefono", telefono);
            bodyPerfil.put("direccion", direccion);
            bodyPerfil.put("fechaNacimiento", fechaNacimiento);
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);
        } catch (Exception e) {
            logger.error("Error al sincronizar PerfilLegal: " + e.getMessage());
        }

        // 3. Sincronización de datos básicos en el servicio de autenticación
        try {
            Map<String, Object> user = restTemplate.getForObject(authUrl + "/v1/usuarios/" + usuarioId, Map.class);
            if (user != null) {
                Map<String, Object> bodyUser = new HashMap<>(user);
                bodyUser.put("email", email);
                
                // No enviamos teléfono a auth ya que ahora se gestiona en PerfilLegal
                bodyUser.remove("telefono");

                if (bodyUser.get("contrasena") == null) {
                    bodyUser.put("contrasena", "secret_placeholder"); 
                }
                restTemplate.put(authUrl + "/v1/usuarios/" + usuarioId, bodyUser);
            }
        } catch (Exception e) {
            logger.error("Error al sincronizar datos en el servicio de Auth: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage", "Voluntario editado correctamente");
        return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_ELIMINAR)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            restTemplate.delete(apiUrl + "/v1/voluntarios/" + id);
            if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        } catch (Exception e) {
            if ("true".equals(request.getHeader("HX-Request"))) {
                return ResponseEntity.unprocessableEntity()
                        .body("<div class='toast error'><span>No se puede eliminar: tiene animales asignados.</span></div>");
            }
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.VOLUNTARIOS_BASE).build();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_PDF)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> voluntarios = fetchList("/v1/voluntarios");
        List<Object> perfilesLegales = fetchList("/v1/perfiles-legales");

        Map<String, Object> perfilesMap = new HashMap<>();
        for (Object p : perfilesLegales) {
            if (p instanceof Map) {
                Object uId = ((Map<?, ?>) p).get("usuarioId");
                if (uId instanceof Number) {
                    perfilesMap.put(String.valueOf(((Number) uId).intValue()), p);
                }
            }
        }

        Context context = new Context();
        context.setVariable("voluntarios", voluntarios);
        context.setVariable("perfilesMap", perfilesMap);
        String html = templateEngine.process(ThymTemplates.Voluntario_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=voluntarios.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_DETALLE)
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("unchecked")
    public String verDetalle(@PathVariable Integer id, Model model) {
        try {
            Map<String, Object> voluntario = (Map<String, Object>) restTemplate.getForObject(apiUrl + "/v1/voluntarios/" + id, Map.class);
            if (voluntario != null && voluntario.containsKey("usuarioId")) {
                return "redirect:/web/personas/" + voluntario.get("usuarioId");
            }
        } catch (Exception ignored) {}
        
        return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
    }

    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { return List.of(); }
    }
}
