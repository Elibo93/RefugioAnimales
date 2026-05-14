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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.HttpEntity;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.security.CustomUserDetails;

import java.io.OutputStream;
import java.util.ArrayList;
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

    @SuppressWarnings("unchecked")
    @GetMapping(WebRoutes.VOLUNTARIOS_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "false") boolean modoSeleccion,
            @RequestParam(required = false) Integer tareaIdSeleccion,
            HttpServletRequest request,
            HttpServletResponse response) {

        // Indicar al navegador que la respuesta varía según si es HTMX o no para evitar
        // problemas de caché al dar atrás
        response.setHeader("Vary", "HX-Request");

        // Si estamos en modo selección, intentamos obtener la descripción de la tarea
        if (modoSeleccion && tareaIdSeleccion != null) {
            try {
                Map<String, Object> tarea = (Map<String, Object>) restTemplate
                        .getForObject(apiUrl + "/v1/tareas/" + tareaIdSeleccion, Map.class);
                if (tarea != null) {
                    model.addAttribute("tareaNombreSeleccion", tarea.get("descripcion"));

                    // Obtener IDs de voluntarios ya asignados
                    List<Integer> assignedIds = new ArrayList<>();
                    Object vIds = tarea.get("voluntarioIds");
                    if (vIds instanceof List) {
                        for (Object o : (List<?>) vIds) {
                            if (o instanceof Number)
                                assignedIds.add(((Number) o).intValue());
                            else if (o instanceof Map) {
                                Object val = ((Map<?, ?>) o).get("value");
                                if (val instanceof Number)
                                    assignedIds.add(((Number) val).intValue());
                            }
                        }
                    }
                    model.addAttribute("assignedVoluntarioIds", assignedIds);
                }
            } catch (Exception e) {
                logger.warn("No se pudo obtener la información de la tarea para el modo selección: " + e.getMessage());
            }
        }

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
        model.addAttribute("modoSeleccion", modoSeleccion);
        model.addAttribute("tareaIdSeleccion", tareaIdSeleccion);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_BASE);

        if ("true".equals(request.getHeader("HX-Request"))) {
            String hxTarget = request.getHeader("HX-Target");
            // Si el objetivo es solo la lista (búsqueda), devolvemos solo el body
            if ("voluntarios-list-body".equals(hxTarget)) {
                return FragmentoContenido.Voluntario_LIST.getPath() + " :: list-body";
            }
            // Para navegación completa o cualquier otro caso, devolvemos el contenido
            // completo
            return FragmentoContenido.Voluntario_LIST.getPath() + " :: content";
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
                if (uId instanceof Number)
                    perfilesMap.put(((Number) uId).intValue(), (Map<String, Object>) p);
            }
        }

        Map<Integer, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number)
                    usuariosMap.put(((Number) id).intValue(), (Map<String, Object>) u);
            }
        }

        String query = q.toLowerCase();
        List<Map<String, Object>> voluntariosEncontrados = new java.util.ArrayList<>();

        for (Object v : voluntarios) {
            if (v instanceof Map) {
                Map<String, Object> vm = (Map<String, Object>) v;
                Object uIdRaw = vm.get("usuarioId");
                if (uIdRaw instanceof Map)
                    uIdRaw = ((Map<?, ?>) uIdRaw).get("value");

                if (uIdRaw instanceof Number) {
                    int uId = ((Number) uIdRaw).intValue();
                    Map<String, Object> perfil = perfilesMap.get(uId);
                    Map<String, Object> user = usuariosMap.get(uId);

                    String nombre = perfil != null && perfil.get("nombre") != null
                            ? String.valueOf(perfil.get("nombre"))
                            : "";
                    String apellido = perfil != null && perfil.get("apellido") != null
                            ? String.valueOf(perfil.get("apellido"))
                            : "";
                    String email = user != null && user.get("email") != null ? String.valueOf(user.get("email")) : "";
                    String username = user != null && user.get("username") != null
                            ? String.valueOf(user.get("username"))
                            : "";

                    if (nombre.toLowerCase().contains(query) || apellido.toLowerCase().contains(query) ||
                            email.toLowerCase().contains(query) || username.toLowerCase().contains(query)) {

                        Map<String, Object> suggestion = new HashMap<>();
                        Object vId = vm.get("id");
                        if (vId instanceof Map)
                            vId = ((Map<?, ?>) vId).get("value");

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
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLICO', 'VOLUNTARIO', 'ADOPTANTE') or isAnonymous()")
    public String formulario(Model model, HttpServletRequest request) {
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), new HashMap<String, Object>());
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_NUEVO);
        
        // Inicializar flags para evitar errores de null en el template
        model.addAttribute("perfilLegalExists", false);
        model.addAttribute("perfilExistente", false);

        // Si el usuario está autenticado, intentamos cargar su PerfilLegal para
        // pre-rellenar o verificar si le falta
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                // Obtener ID del usuario desde el modelo (inyectado por
                // GlobalModelAttributesAdvice)
                Object currentUserId = model.getAttribute("currentUserId");
                if (currentUserId != null) {
                    Map<String, Object> perfil = restTemplate.exchange(
                            apiUrl + "/v1/perfiles-legales/usuario/" + currentUserId,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Map<String, Object>>() {
                            }).getBody();
                    if (perfil != null) {
                        flattenId(perfil, "id");
                        flattenId(perfil, "usuarioId");
                        model.addAttribute("userPhone",           String.valueOf(perfil.getOrDefault("telefono", "")));
                        model.addAttribute("userDni",             String.valueOf(perfil.getOrDefault("dni", "")));
                        model.addAttribute("userDireccion",       String.valueOf(perfil.getOrDefault("direccion", "")));
                        model.addAttribute("userFechaNacimiento", String.valueOf(perfil.getOrDefault("fechaNacimiento", "")));
                        model.addAttribute("userNombre",          String.valueOf(perfil.getOrDefault("nombre", "")));
                        model.addAttribute("userApellido",        String.valueOf(perfil.getOrDefault("apellido", "")));
                        model.addAttribute("nombreCompleto",      perfil.getOrDefault("nombre", "") + " " + perfil.getOrDefault("apellido", ""));
                        model.addAttribute("perfilLegalExists",   true);
                        model.addAttribute("perfilExistente",     true);
                    } else {
                        model.addAttribute("perfilLegalExists", false);
                        model.addAttribute("perfilExistente", false);
                    }

                    // Verificar si ya tiene un registro de voluntario
                    try {
                        Map<String, Object> voluntarioExistente = restTemplate.exchange(
                                apiUrl + "/v1/voluntarios/usuario/" + currentUserId,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<Map<String, Object>>() {
                                }).getBody();
                        if (voluntarioExistente != null) {
                            model.addAttribute("voluntarioExistente", voluntarioExistente);
                        }
                    } catch (Exception e) {
                        // No es voluntario todavía, es normal
                        logger.debug("El usuario no tiene registro de voluntario previo.");
                    }
                }
            } catch (Exception e) {
                logger.info("El usuario no tiene perfil legal aún: " + e.getMessage());
            }
        }

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Voluntario_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        logger.info("DEBUG: Entrando en editarFormulario para ID: {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
        // Inicializar flags para evitar errores de null en el template
        model.addAttribute("perfilLegalExists", false);
        model.addAttribute("perfilExistente", false);

        try {
            Map<String, Object> voluntario = restTemplate.exchange(
                    apiUrl + "/v1/voluntarios/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }).getBody();

            logger.info("DEBUG: Voluntario recuperado: {}", voluntario != null ? "SI" : "NULL");

            if (voluntario == null) {
                logger.warn("No se encontró el voluntario con ID: {}", id);
                return "redirect:/web/home";
            }

            // Aplanar IDs para que Thymeleaf pueda usarlos en URLs (th:action, th:href)
            // sin que serialice objetos Map{value:X} como texto
            flattenId(voluntario, "id");
            flattenId(voluntario, "usuarioId");

            model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), voluntario);

            Integer voluntarioUsuarioId = (voluntario.get("usuarioId") instanceof Number)
                    ? ((Number) voluntario.get("usuarioId")).intValue()
                    : null;

            // SEGURIDAD: Solo admin o el propio voluntario pueden editar
            if (!isAdmin && (voluntarioUsuarioId == null || !currentUser.getId().equals(voluntarioUsuarioId))) {
                logger.warn("Usuario {} intentó editar voluntario {} sin permiso", currentUser.getId(), id);
                return "redirect:/web/home";
            }

            if (voluntarioUsuarioId != null) {
                // 2. Datos de usuario (email)
                try {
                    Map<String, Object> user = restTemplate.exchange(
                            authUrl + "/v1/usuarios/" + voluntarioUsuarioId,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Map<String, Object>>() {
                            }).getBody();
                    if (user != null) {
                        model.addAttribute("userEmail", user.get("email"));
                    }
                } catch (Exception e) {
                    logger.warn("No se pudo obtener datos de usuario {} para el voluntario {}", voluntarioUsuarioId, id);
                }

                // 3. PerfilLegal
                try {
                    Map<String, Object> perfil = restTemplate.exchange(
                            apiUrl + "/v1/perfiles-legales/usuario/" + voluntarioUsuarioId,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Map<String, Object>>() {
                            }).getBody();
                    if (perfil != null) {
                        // Aplanar IDs del perfil también
                        flattenId(perfil, "id");
                        flattenId(perfil, "usuarioId");
                        model.addAttribute("nombreCompleto", perfil.get("nombre") + " " + perfil.get("apellido"));
                        model.addAttribute("userPhone",         String.valueOf(perfil.getOrDefault("telefono", "")));
                        model.addAttribute("userDni",           String.valueOf(perfil.getOrDefault("dni", "")));
                        model.addAttribute("userDireccion",     String.valueOf(perfil.getOrDefault("direccion", "")));
                        model.addAttribute("userFechaNacimiento", String.valueOf(perfil.getOrDefault("fechaNacimiento", "")));
                        model.addAttribute("userNombre",        String.valueOf(perfil.getOrDefault("nombre", "")));
                        model.addAttribute("userApellido",      String.valueOf(perfil.getOrDefault("apellido", "")));
                        // NO añadimos el Map completo para evitar HttpMessageNotWritableException
                        model.addAttribute("perfilLegalExists", true);
                        model.addAttribute("perfilExistente", true);
                    } else {
                        model.addAttribute("perfilLegalExists", false);
                        model.addAttribute("perfilExistente", false);
                    }
                } catch (Exception e) {
                    logger.warn("No se encontró PerfilLegal para usuario: {}", voluntarioUsuarioId);
                    model.addAttribute("perfilLegalExists", false);
                    model.addAttribute("perfilExistente", false);
                }
            }
        } catch (Exception e) {
            logger.error("Error al cargar voluntario/usuario para editar: " + e.getMessage());
            return "redirect:/web/home";
        }

        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_EDITAR);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Voluntario_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Aplana un ID anidado {value: X} a su valor entero plano.
     * Necesario porque el backend serializa IDs compuestos como objetos Map.
     */
    private void flattenId(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return;
        Object val = map.get(key);
        if (val instanceof Map) {
            Object inner = ((Map<?, ?>) val).get("value");
            map.put(key, inner);
        }
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLICO', 'VOLUNTARIO', 'ADOPTANTE') or isAnonymous()")
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
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        Integer finalUsuarioId = idUsuario;

        // SEGURIDAD: Si el usuario está autenticado, verificamos que no esté intentando
        // suplantar a otro
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());

        if (isAuthenticated) {
            try {
                Map<String, Object> me = restTemplate.exchange(
                        authUrl + "/v1/me",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }).getBody();
                if (me != null) {
                    Object realIdObj = me.get("id");
                    if (realIdObj instanceof Map)
                        realIdObj = ((Map<?, ?>) realIdObj).get("value");
                    Integer realUserId = ((Number) realIdObj).intValue();
                    String rol = (String) me.get("rol");
                    boolean isAdmin = rol != null && rol.contains("ADMIN");

                    // Si no es admin y el ID enviado no coincide con el suyo, forzamos el suyo real
                    if (!isAdmin) {
                        if (finalUsuarioId != null && !finalUsuarioId.equals(realUserId)) {
                            logger.warn("Usuario {} intentó suplantar al ID {} en registro voluntario", realUserId, finalUsuarioId);
                        }
                        finalUsuarioId = realUserId;

                        // BLOQUEO: Evitar doble solicitud
                        try {
                            Map<String, Object> existing = restTemplate.exchange(
                                    apiUrl + "/v1/voluntarios/usuario/" + realUserId,
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<Map<String, Object>>() {
                                    }).getBody();
                            if (existing != null) {
                                logger.info("Bloqueada solicitud duplicada para usuario {}", realUserId);
                                redirectAttributes.addFlashAttribute("errorMessage", "Ya tienes una solicitud de voluntariado registrada.");
                                return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
                            }
                        } catch (Exception e) {
                            // Normal, no hay registro previo
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error al verificar identidad en creación de voluntario: " + e.getMessage());
            }
        }

        // 1. Si no hay usuarioId, registramos al usuario primero
        if (finalUsuarioId == null) {
            Map<String, Object> userBody = new HashMap<>();
            userBody.put("email", email);
            userBody.put("contrasena", contrasena);
            userBody.put("rol", "ROLE_VOLUNTARIO");

            try {
                String targetUrl = authUrl + "/v1/usuarios";
                logger.info("Registrando usuario en Auth: " + targetUrl);
                Map<String, Object> createdUser = restTemplate.exchange(
                        targetUrl,
                        HttpMethod.POST,
                        new HttpEntity<>(userBody),
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }).getBody();
                if (createdUser != null && createdUser.get("id") != null) {
                    Object idObj = createdUser.get("id");
                    if (idObj instanceof Map) {
                        finalUsuarioId = (Integer) ((Map<?, ?>) idObj).get("value");
                    } else if (idObj instanceof Number) {
                        finalUsuarioId = ((Number) idObj).intValue();
                    }
                }
            } catch (Exception e) {
                logger.error("Error al registrar usuario para voluntario: " + e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la cuenta de usuario.");
                return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
            }
        } // 2. Asegurar PerfilLegal (Identidad)
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

        // 2. Registrar el perfil de voluntario (Solo datos operativos)
        Map<String, Object> bodyVol = new HashMap<>();
        bodyVol.put("usuarioId", finalUsuarioId);
        bodyVol.put("disponibilidad", disponibilidad);
        bodyVol.put("especialidad", especialidad);

        try {
            restTemplate.postForObject(apiUrl + "/v1/voluntarios", bodyVol, Object.class);

            // 3. Ya no actualizamos el Rol inmediatamente. 
            // El usuario debe esperar la aprobación del Administrador.
            
            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Solicitud enviada! Tu perfil de voluntario está pendiente de revisión por un administrador.");
        } catch (Exception e) {
            String errorMsg = "Error al crear el perfil: " + e.getMessage();
            if (e.getCause() != null)
                errorMsg += " (Causa: " + e.getCause().getMessage() + ")";
            logger.error(errorMsg);
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
        }
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
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

        // SEGURIDAD: Solo admin o el propio voluntario pueden editar
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin && (usuarioId == null || !currentUser.getId().equals(usuarioId))) {
            logger.warn("Usuario {} intentó procesar edición de voluntario {} (usuarioId {}) sin permiso",
                    currentUser.getId(), id, usuarioId);
            return "redirect:/web/home";
        }

        // 1. Actualización de disponibilidad en el servicio de backend
        Map<String, Object> bodyVol = new HashMap<>();
        bodyVol.put("disponibilidad", disponibilidad);
        bodyVol.put("especialidad", especialidad);
        restTemplate.put(apiUrl + "/v1/voluntarios/" + id, bodyVol);

        // 2. Actualización de PerfilLegal
        try {
            Map<String, Object> bodyPerfil = new HashMap<>();
            bodyPerfil.put("usuarioId", usuarioId);
            bodyPerfil.put("nombre", nombre);
            bodyPerfil.put("apellido", apellido);
            bodyPerfil.put("dni", dni);
            bodyPerfil.put("telefono", telefono);
            bodyPerfil.put("direccion", direccion);
            bodyPerfil.put("fechaNacimiento", fechaNacimiento);
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);
        } catch (Exception e) {
            logger.error("Error al actualizar PerfilLegal: " + e.getMessage());
        }

        // 3. Actualización de datos de usuario en Auth
        try {
            Map<String, Object> user = restTemplate.exchange(
                    authUrl + "/v1/usuarios/" + usuarioId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }).getBody();
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
            logger.error("Error al actualizar usuario en Auth: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage", "Voluntario actualizado correctamente.");

        if (isAdmin) {
            return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
        } else {
            return "redirect:/web/personas/" + usuarioId;
        }
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_ELIMINAR)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            restTemplate.delete(apiUrl + "/v1/voluntarios/" + id);
            if ("true".equals(request.getHeader("HX-Request")))
                return ResponseEntity.ok("");
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
    public String verDetalle(@PathVariable Integer id, Model model) {
        try {
            Map<String, Object> voluntario = restTemplate.exchange(
                    apiUrl + "/v1/voluntarios/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }).getBody();
            if (voluntario != null && voluntario.containsKey("usuarioId")) {
                return "redirect:/web/personas/" + voluntario.get("usuarioId");
            }
        } catch (Exception ignored) {
        }

        return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
    }

    @GetMapping("/web/voluntarios/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPendientes(Model model, HttpServletRequest request) {
        List<Object> pendientes = fetchList("/v1/voluntarios/pendientes");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
        List<Object> perfilesLegales = fetchList("/v1/perfiles-legales");

        Map<String, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) usuariosMap.put(String.valueOf(((Number) id).intValue()), u);
            }
        }

        Map<String, Object> perfilesMap = new HashMap<>();
        for (Object p : perfilesLegales) {
            if (p instanceof Map) {
                Object uId = ((Map<?, ?>) p).get("usuarioId");
                if (uId instanceof Number) perfilesMap.put(String.valueOf(((Number) uId).intValue()), p);
            }
        }

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("perfilesMap", perfilesMap);
        model.addAttribute("currentUri", "/web/voluntarios/pendientes");

        if ("true".equals(request.getHeader("HX-Request"))) {
            return "fragments/content/voluntarios-pendientes :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/voluntarios-pendientes");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/web/voluntarios/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> aprobar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/aprobar", null, Void.class);
            
            if ("true".equals(request.getHeader("HX-Request"))) {
                return ResponseEntity.ok()
                    .header("HX-Trigger", "{\"showToast\": {\"message\": \"¡Solicitud aprobada! El nuevo voluntario ya está activo en el equipo.\", \"type\": \"success\"}}")
                    .body(""); // Eliminamos la fila de la lista
            }
        } catch (Exception e) {
            logger.error("Error al aprobar voluntario {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Error al procesar la aprobación");
        }
        return ResponseEntity.status(302).header("Location", "/web/voluntarios/pendientes").build();
    }

    @PostMapping("/web/voluntarios/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> rechazar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/rechazar", null, Void.class);
            
            if ("true".equals(request.getHeader("HX-Request"))) {
                return ResponseEntity.ok()
                    .header("HX-Trigger", "{\"showToast\": {\"message\": \"Solicitud rechazada. Se ha actualizado el estado del candidato correctamente.\", \"type\": \"warning\"}}")
                    .body("");
            }
        } catch (Exception e) {
            logger.error("Error al rechazar voluntario {}: {}", id, e.getMessage());
        }
        return ResponseEntity.status(302).header("Location", "/web/voluntarios/pendientes").build();
    }

    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
