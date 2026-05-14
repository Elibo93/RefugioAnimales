package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
 
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class SolicitudAdopcionViewController {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudAdopcionViewController.class);

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.SOLICITUDES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model, HttpServletRequest request, @RequestParam(required = false) String successMessage) {
        List<Object> solicitudes = fetchList("/v1/solicitudes-adopcion");
        
        // Filtrar pendientes y en revisión para la sección superior
        List<Object> pendientes = solicitudes.stream()
                .filter(s -> s instanceof Map<?, ?> && 
                        ("PENDIENTE".equals(((Map<?, ?>) s).get("estado")) || "EN_REVISION".equals(((Map<?, ?>) s).get("estado"))))
                .toList();
        List<Object> animales = fetchList("/v1/animales");
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        Map<String, Object> animalesMap = new HashMap<>();
        for (Object a : animales) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                if (id instanceof Number)
                    animalesMap.put(String.valueOf(((Number) id).intValue()), a);
            }
        }

        Map<String, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> um = (Map<String, Object>) u;
                Object id = um.get("id");
                if (id instanceof Number)
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), um);
            }
        }

        Map<String, String> adoptanteNombres = new HashMap<>();
        for (Object a : adoptantes) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                Object uid = ((Map<?, ?>) a).get("usuarioId");
                if (id instanceof Number && uid instanceof Number) {
                    try {
                        Map<String, Object> perfil = restTemplate.exchange(
                                apiUrl + "/v1/perfiles-legales/usuario/" + uid,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                        if (perfil != null) {
                            Object nombre = perfil.get("nombre");
                            Object apellido = perfil.get("apellido");
                            adoptanteNombres.put(String.valueOf(((Number) id).intValue()),
                                    ((nombre != null ? nombre.toString() : "") + " "
                                            + (apellido != null ? apellido.toString() : "")).trim());
                        }
                    } catch (Exception e) {
                        logger.warn("No se encontró PerfilLegal para adoptante con usuario " + uid);
                    }
                }
            }
        }

        // Build adoptanteUsuarioIds: Map<adoptanteId, usuarioId>
        Map<String, String> adoptanteUsuarioIds = new HashMap<>();
        for (Object a : adoptantes) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                Object uid = ((Map<?, ?>) a).get("usuarioId");
                if (id instanceof Number && uid instanceof Number) {
                    adoptanteUsuarioIds.put(id.toString(), uid.toString());
                }
            }
        }

        // Cargar mapa de Adopciones para botones de contrato
        Map<String, Integer> solicitudToAdopcionMap = new HashMap<>();
        try {
            List<Map<String, Object>> allAdopciones = restTemplate.exchange(
                    apiUrl + "/v1/adopciones",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
            
            if (allAdopciones != null && solicitudes != null) {
                for (Object sObj : solicitudes) {
                    if (sObj instanceof Map) {
                        Map<String, Object> s = (Map<String, Object>) sObj;
                        if ("APROBADA".equals(s.get("estado"))) {
                            String key = s.get("adoptanteId") + "_" + s.get("animalId");
                            allAdopciones.stream()
                                .filter(ad -> key.equals(ad.get("adoptanteId") + "_" + ad.get("animalId")))
                                .findFirst()
                                .ifPresent(ad -> solicitudToAdopcionMap.put(String.valueOf(s.get("id")), (Integer) ad.get("id")));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error al cargar mapa de adopciones para lista: " + e.getMessage());
        }

        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), solicitudes);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("adoptanteNombres", adoptanteNombres);
        model.addAttribute("adoptanteUsuarioIds", adoptanteUsuarioIds);
        model.addAttribute("solicitudToAdopcionMap", solicitudToAdopcionMap);
        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_BASE);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Solicitud_LIST.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    @GetMapping(WebRoutes.SOLICITUDES_MIS_ADOPTADOS)
    public String misAdoptados(Model model, HttpServletRequest request) {
        // 1. Obtener ID del usuario actual del modelo (inyectado por
        // GlobalModelAttributesAdvice)
        Object userIdObj = model.getAttribute("currentUserId");
        if (userIdObj == null)
            return "redirect:/login";

        Integer currentUserId = (userIdObj instanceof Number) ? ((Number) userIdObj).intValue()
                : Integer.parseInt(userIdObj.toString());

        // 2. Buscar perfil de adoptante para este usuario usando el endpoint específico
        // por usuarioId
        Integer adoptanteId = null;
        try {
            Map<String, Object> adoptante = restTemplate.exchange(
                    apiUrl + "/v1/adoptantes/usuario/" + currentUserId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
            if (adoptante != null && adoptante.get("id") != null) {
                adoptanteId = ((Number) adoptante.get("id")).intValue();
            }
        } catch (Exception e) {
            logger.warn("El usuario " + currentUserId + " no tiene un perfil de adoptante activo.");
        }

        // 3. Filtrar solicitudes si existe el adoptante
        List<Object> todas = fetchList("/v1/solicitudes-adopcion");
        List<Object> misSolicitudes = new java.util.ArrayList<>();

        if (adoptanteId != null) {
            for (Object obj : todas) {
                if (obj instanceof Map) {
                    Map<String, Object> s = (Map<String, Object>) obj;
                    Object aid = s.get("adoptanteId");
                    if (aid != null && ((Number) aid).intValue() == adoptanteId) {
                        misSolicitudes.add(s);
                    }
                }
            }
            
            // AGREGAR ADOPCIONES DIRECTAS (SIN SOLICITUD ASOCIADA) COMO SI FUERAN SOLICITUDES APROBADAS
            try {
                List<Map<String, Object>> adopciones = restTemplate.exchange(
                    apiUrl + "/v1/adopciones/adoptante/" + adoptanteId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
                
                if (adopciones != null) {
                    for (Map<String, Object> adopcion : adopciones) {
                        if (adopcion.get("solicitudAdopcionId") == null) {
                            Map<String, Object> fakeSolicitud = new java.util.HashMap<>();
                            // ID negativo para identificar que es una adopción directa en el front
                            fakeSolicitud.put("id", -((Number) adopcion.get("id")).intValue()); 
                            fakeSolicitud.put("animalId", adopcion.get("animalId"));
                            fakeSolicitud.put("adoptanteId", adopcion.get("adoptanteId"));
                            fakeSolicitud.put("estado", "APROBADA");
                            fakeSolicitud.put("fecha", adopcion.get("fechaAdopcion"));
                            misSolicitudes.add(fakeSolicitud);
                        }
                    }
                }
            } catch(Exception e) {
                logger.warn("No se pudieron obtener adopciones directas para adoptante " + adoptanteId);
            }
        }

        // 4. Si no hay solicitudes, mostrar vista vacía con mensaje personalizado
        if (misSolicitudes.isEmpty()) {
            model.addAttribute("mensajeVacio",
                    "Vaya, parece que aún no te has hecho con ninguno de nuestros amiguitos");
            model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                    FragmentoContenido.MIS_ADOPTADOS_VACIO.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        // 5. Enriquecer con datos de animales para mostrar en la lista
        List<Object> animales = fetchList("/v1/animales");
        Map<String, Object> animalesMap = new HashMap<>();
        for (Object a : animales) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                if (id instanceof Number)
                    animalesMap.put(id.toString(), a);
            }
        }

        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), misSolicitudes);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.MIS_ADOPTADOS_LISTA.getPath());

        if (request != null && "true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.MIS_ADOPTADOS_LISTA.getPath() + " :: content";
        }

        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_NUEVA)
    public String formulario(Model model, @RequestParam(required = false) Integer animalId,
            HttpServletRequest request) {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("fecha", LocalDateTime.now().toString());
        if (animalId != null)
            solicitud.put("animalId", animalId);

        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales", fetchList("/v1/animales"));
        model.addAttribute("adoptantes", fetchList("/v1/adoptantes"));
        model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_NUEVA);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Solicitud_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_NUEVA)
    @PreAuthorize("hasRole('ADMIN')")
    public String crear(@RequestParam Integer animalId,
            @RequestParam Integer adoptanteId,
            @RequestParam String comentario,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("comentario", comentario);
        body.put("fecha", LocalDateTime.now().toString());

        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion", body, Object.class);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return "fragments/content/solicitud-creada :: success-modal";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud de adopción registrada correctamente");
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping(WebRoutes.SOLICITUDES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        Map<String, Object> sol = restTemplate.exchange(
                apiUrl + "/v1/solicitudes-adopcion/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), sol);

        if (sol != null && sol.get("adoptanteId") != null) {
            try {
                Map<String, Object> adoptante = restTemplate.exchange(
                        apiUrl + "/v1/adoptantes/" + sol.get("adoptanteId"),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                if (adoptante != null && adoptante.get("usuarioId") != null) {
                    try {
                        Map<String, Object> perfil = restTemplate.exchange(
                                apiUrl + "/v1/perfiles-legales/usuario/" + adoptante.get("usuarioId"),
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                        if (perfil != null) {
                            model.addAttribute("nombreAdoptante", perfil.get("nombre") + " " + perfil.get("apellido"));
                        }
                    } catch (Exception e) {
                        logger.error("Error al cargar PerfilLegal para editar solicitud: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.error("Error al cargar datos del adoptante para editar solicitud: " + e.getMessage());
            }
        }

        if (sol != null && sol.get("animalId") != null) {
            try {
                Map<String, Object> animal = restTemplate.exchange(
                        apiUrl + "/v1/animales/" + sol.get("animalId"),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                model.addAttribute("animalData", animal);
            } catch (Exception e) {
                logger.error("Error al cargar datos del animal para editar solicitud: " + e.getMessage());
            }
        }

        model.addAttribute("animales", fetchList("/v1/animales"));
        model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Solicitud_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer animalId,
            @RequestParam Integer adoptanteId,
            @RequestParam String estado,
            @RequestParam String comentario,
            @RequestParam(required = false) String comentarioAdmin,
            @RequestParam(required = false) String redireccion,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("estado", estado);
        body.put("comentario", comentario);
        body.put("comentarioAdmin", comentarioAdmin);
        body.put("fecha", LocalDateTime.now().toString());

        restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud actualizada correctamente");
        
        if ("detalle".equals(redireccion)) {
            return "redirect:/web/solicitudes/" + id + "/detalle";
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/solicitudes-adopcion/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok()
                .header("HX-Trigger", "{\"showToast\": {\"message\": \"Solicitud eliminada correctamente\", \"type\": \"success\"}}")
                .body("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.SOLICITUDES_BASE).build();
    }

    @PostMapping(WebRoutes.SOLICITUDES_APROBAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String aprobarSolicitud(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "Solicitud aprobada y adopción registrada correctamente.";
        try {
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/" + id + "/aprobar", null, Object.class);
            redirectAttributes.addFlashAttribute("successMessage", msg);
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"success\"}}");
            }
        } catch (Exception e) {
            logger.error("Error al aprobar solicitud: " + e.getMessage());
            String errorMsg = "Error al procesar la aprobación.";
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + errorMsg + "\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))) {
            return listar(model, request, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_RECHAZAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String rechazarSolicitud(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "La solicitud ha sido rechazada correctamente.";
        try {
            Map<String, Object> existing = restTemplate.exchange(
                apiUrl + "/v1/solicitudes-adopcion/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                
            if (existing != null) {
                Map<String, Object> body = new HashMap<>(existing);
                body.put("estado", "RECHAZADA");
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", msg);
                if ("true".equals(request.getHeader("HX-Request"))) {
                    response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"success\"}}");
                }
            }
        } catch (Exception e) {
            logger.error("Error al rechazar solicitud: " + e.getMessage());
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"Error al rechazar la solicitud\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))) {
            return listar(model, request, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_REVISION)
    @PreAuthorize("hasRole('ADMIN')")
    public String ponerEnRevision(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "Solicitud marcada 'En Revisión' correctamente.";
        try {
            Map<String, Object> existing = restTemplate.exchange(
                apiUrl + "/v1/solicitudes-adopcion/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                
            if (existing != null) {
                Map<String, Object> body = new HashMap<>(existing);
                body.put("estado", "EN_REVISION");
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", msg);
                if ("true".equals(request.getHeader("HX-Request"))) {
                    response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"success\"}}");
                }
            }
        } catch (Exception e) {
            logger.error("Error al poner en revisión: " + e.getMessage());
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"Error al mover a revisión\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))) {
            return listar(model, request, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping(WebRoutes.SOLICITUDES_PDF)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> solicitudes = fetchList("/v1/solicitudes-adopcion");
        Context context = new Context();
        context.setVariable("solicitudes", solicitudes);
        String html = templateEngine.process(ThymTemplates.Solicitud_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    @GetMapping(WebRoutes.SOLICITUDES_PUBLICO_REGISTRO)
    public String formularioPublico(Model model, @RequestParam Integer animalId) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_REGISTRO.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_OPCIONES)
    public String formularioOpciones(Model model, @RequestParam Integer animalId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean isAdoptante = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADOPTANTE"));

            if (isAdoptante) {
                return "redirect:" + WebRoutes.SOLICITUDES_DIRECTA_FORM + "?animalId=" + animalId;
            } else {
                return "redirect:" + WebRoutes.SOLICITUDES_CONVERTIR + "?animalId=" + animalId;
            }
        }

        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_OPCIONES.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_CONVERTIR)
    public String formularioConversion(Model model, @RequestParam Integer animalId) {
        try {
            Map<String, Object> animal = restTemplate.exchange(
                    apiUrl + "/v1/animales/" + animalId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }

        // Inicializar variables por defecto para evitar errores de renderizado
        model.addAttribute("perfilExistente", false);
        model.addAttribute("nombre", "");
        model.addAttribute("apellido", "");
        model.addAttribute("telefono", "");
        model.addAttribute("dni", "");
        model.addAttribute("direccion", "");
        model.addAttribute("fechaNacimiento", "");
        model.addAttribute("comentario", "");

        // Pre-cargar datos del perfil legal si existen
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                Map<String, Object> me = restTemplate.exchange(
                        authUrl + "/v1/me",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                if (me != null && me.get("id") != null) {
                    Object idObj = me.get("id");
                    if (idObj instanceof Map)
                        idObj = ((Map<?, ?>) idObj).get("value");

                    if (idObj != null) {
                        Map<String, Object> perfil = restTemplate.exchange(
                                apiUrl + "/v1/perfiles-legales/usuario/" + idObj,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                        if (perfil != null) {
                            model.addAttribute("nombre", perfil.get("nombre") != null ? perfil.get("nombre") : "");
                            model.addAttribute("apellido", perfil.get("apellido") != null ? perfil.get("apellido") : "");
                            model.addAttribute("telefono", perfil.get("telefono") != null ? perfil.get("telefono") : "");
                            model.addAttribute("dni", perfil.get("dni") != null ? perfil.get("dni") : "");
                            model.addAttribute("direccion", perfil.get("direccion") != null ? perfil.get("direccion") : "");
                            model.addAttribute("fechaNacimiento", perfil.get("fechaNacimiento") != null ? perfil.get("fechaNacimiento") : "");
                            model.addAttribute("perfilExistente", true);
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("No se pudo pre-cargar el perfil legal: " + e.getMessage());
            }
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_CONVERSION.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_DIRECTA_FORM)
    public String formularioDirecta(Model model, @RequestParam Integer animalId) {
        // Inicializar variables por defecto para evitar errores de renderizado
        model.addAttribute("perfilExistente", false);
        model.addAttribute("nombre", "");
        model.addAttribute("apellido", "");

        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);

            // Verificar si el usuario ya tiene una solicitud para este animal
            @SuppressWarnings("unchecked")
            Set<Integer> solicitados = (Set<Integer>) model.getAttribute("animalesSolicitadosIds");
            if (solicitados != null && solicitados.contains(animalId)) {
                return "redirect:" + WebRoutes.HOME + "?errorMessage=Ya tienes una solicitud pendiente para este animal.";
            }

            // Cargar datos del perfil para la ficha
            Map<String, Object> me = restTemplate.exchange(
                    authUrl + "/v1/me",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
            if (me != null && me.get("id") != null) {
                Object idObj = me.get("id");
                if (idObj instanceof Map) idObj = ((Map<?, ?>) idObj).get("value");
                
                try {
                    Map<String, Object> perfil = restTemplate.exchange(
                            apiUrl + "/v1/perfiles-legales/usuario/" + idObj,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                    if (perfil != null) {
                        model.addAttribute("nombre", perfil.get("nombre") != null ? perfil.get("nombre") : "");
                        model.addAttribute("apellido", perfil.get("apellido") != null ? perfil.get("apellido") : "");
                        model.addAttribute("perfilExistente", true);
                    }
                } catch (Exception e) {
                    logger.warn("No se encontró perfil legal para la ficha de adopción directa");
                }
            }
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_DIRECTA_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_CONVERTIR)
    public String procesarConversionYAdopcion(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response,
            Model model) {

        // 1. Obtener usuarioId del contexto
        Map<String, Object> me = restTemplate.getForObject(authUrl + "/v1/me", Map.class);
        if (me == null || !me.containsKey("id")) {
             return "redirect:/login";
        }
        Integer usuarioId = ((Number) me.get("id")).intValue();

        // 3. Procesar Conversión y Adopción (Unificado: PerfilLegal + Adoptante + Solicitud)
        Map<String, Object> bodySolicitud = new HashMap<>();
        bodySolicitud.put("animalId", animalId);
        bodySolicitud.put("comentario", comentario);
        
        // Incluimos los datos del perfil para que el backend los actualice/cree atómicamente
        bodySolicitud.put("nombre", nombre);
        bodySolicitud.put("apellido", apellido);
        bodySolicitud.put("dni", dni);
        bodySolicitud.put("direccion", direccion);
        bodySolicitud.put("telefono", telefono);
        bodySolicitud.put("fechaNacimiento", fechaNacimiento);

        logger.info("Enviando solicitud de conversión y adopción para usuario: " + usuarioId);
        try {
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/convertir-y-adopcion", bodySolicitud, Object.class);
        } catch (org.springframework.web.client.RestClientResponseException e) {
            Map<String, Object> responseBody = e.getResponseBodyAs(new ParameterizedTypeReference<Map<String, Object>>() {});
            String errorMsg = "Error al procesar la adopción. Es posible que el DNI ya esté en uso.";

            if (responseBody != null) {
                if (responseBody.containsKey("message")) {
                    errorMsg = (String) responseBody.get("message");
                } else if (responseBody.containsKey("dni")) {
                    errorMsg = (String) responseBody.get("dni");
                } else if (responseBody.containsKey("fechaNacimiento")) {
                    errorMsg = (String) responseBody.get("fechaNacimiento");
                } else if (!responseBody.isEmpty()) {
                    // Coger el primer error que encontremos
                    errorMsg = responseBody.values().iterator().next().toString();
                }
            }

            // Si hay error, volvemos a mostrar el formulario de conversión con los datos
            // introducidos
            try {
                Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
                model.addAttribute("animal", animal);
            } catch (Exception ignored) {
                model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
            }

            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("dni", dni);
            model.addAttribute("direccion", direccion);
            model.addAttribute("fechaNacimiento", fechaNacimiento);
            model.addAttribute("comentario", comentario);
            model.addAttribute("errorMessage", errorMsg);

            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                    FragmentoContenido.Solicitud_CONVERSION.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        // Actualizar el rol en el microservicio de Auth
        try {
            if (me != null && me.containsKey("id")) {
                String currentRol = (String) me.get("rol");
                Object rawId = me.get("id");
                Integer usuarioIdAuth = (rawId instanceof Number) ? ((Number) rawId).intValue()
                        : Integer.parseInt(rawId.toString());

                String newRol = null;
                if ("ROLE_PUBLICO".equals(currentRol)) {
                    newRol = "ROLE_ADOPTANTE";
                } else if ("ROLE_VOLUNTARIO".equals(currentRol)) {
                    newRol = "ROLE_VOLUNTARIO_ADOPTANTE";
                }

                if (newRol != null) {
                    Map<String, String> patchBody = new HashMap<>();
                    patchBody.put("rol", newRol);

                    // Usar exchange para obtener la respuesta con el nuevo token
                    var responseEntity = restTemplate.exchange(
                            authUrl + "/v1/usuarios/" + usuarioIdAuth + "/rol",
                            HttpMethod.PUT,
                            new org.springframework.http.HttpEntity<>(patchBody),
                            new ParameterizedTypeReference<Map<String, Object>>() {});

                    if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                        String newToken = (String) responseEntity.getBody().get("token");
                        if (newToken != null) {
                            // 1. Actualizar la cookie en el navegador
                            jakarta.servlet.http.Cookie authCookie = new jakarta.servlet.http.Cookie("JWT_TOKEN",
                                    newToken);
                            authCookie.setHttpOnly(true);
                            authCookie.setPath("/");
                            authCookie.setMaxAge(86400);
                            response.addCookie(authCookie);
                            logger.info("Cookie JWT_TOKEN actualizada en el frontend con el nuevo rol " + newRol);

                            // 2. Actualizar contexto de seguridad local para la petición actual
                            List<SimpleGrantedAuthority> authoritiesList = new java.util.ArrayList<>();
                            authoritiesList.add(new SimpleGrantedAuthority(newRol));
                            if ("ROLE_VOLUNTARIO_ADOPTANTE".equals(newRol)) {
                                authoritiesList.add(new SimpleGrantedAuthority("ROLE_VOLUNTARIO"));
                                authoritiesList.add(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
                            }

                            var auth = new UsernamePasswordAuthenticationToken(me.get("email"), null, authoritiesList);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error al actualizar el rol del usuario: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Solicitud enviada con éxito.En breve nos pondremos en contacto contigo.");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.SOLICITUDES_DIRECTA)
    public String procesarAdopcionDirecta(
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("comentario", comentario);

        try {
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/directa", body, Object.class);
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debe iniciar sesión para realizar esta acción.");
            return "redirect:" + WebRoutes.SOLICITUDES_OPCIONES + "?animalId=" + animalId;
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String errorMsg = "Error al procesar la solicitud.";
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> errorMap = e.getResponseBodyAs(Map.class);
                if (errorMap != null && errorMap.containsKey("message")) {
                    errorMsg = (String) errorMap.get("message");
                }
            } catch (Exception ignored) {}
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:" + WebRoutes.SOLICITUDES_DIRECTA_FORM + "?animalId=" + animalId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud enviada con éxito");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.SOLICITUDES_PUBLICO_REGISTRO)
    public String procesarRegistroYAdopcion(
            @RequestParam("userName") String userName,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam("password") String password,
            @RequestParam String telefono,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        // 1. Crear usuario en Auth
        Map<String, Object> userBody = new java.util.LinkedHashMap<>();
        userBody.put("email", email);
        userBody.put("username", userName);
        userBody.put("contrasena", password);
        userBody.put("rol", "ROLE_ADOPTANTE");

        Integer usuarioId = null;
        try {
            Map<String, Object> respUser = restTemplate.exchange(
                    authUrl + "/v1/usuarios/publico",
                    HttpMethod.POST,
                    new HttpEntity<>(userBody),
                    new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
            if (respUser != null && respUser.get("id") != null) {
                Object rawId = respUser.get("id");
                usuarioId = (rawId instanceof Number) ? ((Number) rawId).intValue()
                        : Integer.parseInt(rawId.toString());
            } else {
                throw new Exception("No se pudo obtener el ID del usuario tras el registro");
            }
        } catch (Exception e) {
            String errorMsg = "Error inesperado al contactar con el servicio de autenticación.";
            if (e instanceof org.springframework.web.client.RestClientResponseException) {
                var ex = (org.springframework.web.client.RestClientResponseException) e;
                try {
                    Map<String, Object> errorMap = ex.getResponseBodyAs(Map.class);
                    if (errorMap != null) {
                        if (errorMap.containsKey("message")) {
                            errorMsg = (String) errorMap.get("message");
                        } else if (errorMap.containsKey("username")) {
                            errorMsg = (String) errorMap.get("username");
                        } else if (errorMap.containsKey("error")) {
                            errorMsg = (String) errorMap.get("error");
                        }
                    } else {
                        errorMsg = ex.getStatusText();
                    }
                } catch (Exception ignored) {
                    errorMsg = ex.getMessage();
                }
            } else {
                errorMsg = e.getMessage();
            }

            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("userName", userName);
            redirectAttributes.addFlashAttribute("nombre", nombre);
            redirectAttributes.addFlashAttribute("apellido", apellido);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("telefono", telefono);
            redirectAttributes.addFlashAttribute("dni", dni);
            redirectAttributes.addFlashAttribute("direccion", direccion);
            redirectAttributes.addFlashAttribute("fechaNacimiento", fechaNacimiento);
            redirectAttributes.addFlashAttribute("comentario", comentario);

            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        // 2. Crear solicitud en Backend (Unificado: PerfilLegal + Adoptante + Solicitud)
        Map<String, Object> bodySolicitud = new HashMap<>();
        bodySolicitud.put("usuarioId", usuarioId);
        bodySolicitud.put("animalId", animalId);
        bodySolicitud.put("comentario", comentario);
        
        // Datos del perfil para la creación automática en el backend
        bodySolicitud.put("nombre", nombre);
        bodySolicitud.put("apellido", apellido);
        bodySolicitud.put("dni", dni);
        bodySolicitud.put("direccion", direccion);
        bodySolicitud.put("telefono", telefono);
        bodySolicitud.put("fechaNacimiento", fechaNacimiento);

        try {
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/publico/registro-y-adopcion", bodySolicitud, Object.class);
        } catch (Exception e) {
            String errorMsg = "Error inesperado al procesar la adopción en el backend.";
            if (e instanceof org.springframework.web.client.RestClientResponseException) {
                var ex = (org.springframework.web.client.RestClientResponseException) e;
                try {
                    Map<String, Object> errorMap = ex.getResponseBodyAs(Map.class);
                    if (errorMap != null && errorMap.containsKey("message")) {
                        errorMsg = (String) errorMap.get("message");
                    } else {
                        errorMsg = ex.getStatusText();
                    }
                } catch (Exception ignored) {
                    errorMsg = ex.getMessage();
                }
            } else {
                errorMsg = e.getMessage();
            }

            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        // 4. Auto-login tras registro exitoso (Relevo de Cookie JWT)
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

            // Auth service requires 'username' parameter (mapped to email in
            // CustomUserDetailsService)
            String loginBody = "username=" + email + "&password=" + password;
            HttpEntity<String> entity = new HttpEntity<>(loginBody, headers);

            // Stripping /api from authUrl to get the base for /login-post
            String authBaseUrl = authUrl.substring(0, authUrl.lastIndexOf("/api"));
            String loginUrl = authBaseUrl + "/login-post";

            ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);

            List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                for (String cookieStr : cookies) {
                    if (cookieStr.startsWith("JWT_TOKEN=")) {
                        String value = cookieStr.substring(10, cookieStr.indexOf(";"));
                        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JWT_TOKEN", value);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(86400);
                        response.addCookie(cookie);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error en auto-login tras registro: " + e.getMessage());
        }

        logger.info("Registro exitoso para usuario ID: " + usuarioId);

        redirectAttributes.addFlashAttribute("successMessage",
                "¡Registro y solicitud completados!");
        return "redirect:" + WebRoutes.HOME;
    }

    @GetMapping(WebRoutes.SOLICITUDES_DETALLE)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public String verDetalle(@PathVariable Integer id, Model model, HttpServletRequest request) {
        Map<String, Object> sol = restTemplate.exchange(
                apiUrl + "/v1/solicitudes-adopcion/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), sol);

        if (sol != null && sol.get("adoptanteId") != null) {
            try {
                Map<String, Object> adoptante = restTemplate.exchange(
                        apiUrl + "/v1/adoptantes/" + sol.get("adoptanteId"),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                if (adoptante != null && adoptante.get("usuarioId") != null) {
                    model.addAttribute("adoptanteData", adoptante);
                    try {
                        Map<String, Object> perfil = restTemplate.exchange(
                                apiUrl + "/v1/perfiles-legales/usuario/" + adoptante.get("usuarioId"),
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                        model.addAttribute("perfilAdoptante", perfil);
                    } catch (Exception e) {
                        logger.error("Error al cargar PerfilLegal para detalle: " + e.getMessage());
                    }

                    // Si la solicitud está APROBADA, buscamos la adopción para el contrato
                    if ("APROBADA".equals(sol.get("estado"))) {
                        try {
                            List<Map<String, Object>> adopciones = restTemplate.exchange(
                                    apiUrl + "/v1/adopciones/adoptante/" + sol.get("adoptanteId"),
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
                            
                            if (adopciones != null) {
                                String animalIdStr = String.valueOf(sol.get("animalId"));
                                adopciones.stream()
                                    .filter(a -> animalIdStr.equals(String.valueOf(a.get("animalId"))))
                                    .findFirst()
                                    .ifPresent(a -> model.addAttribute("adopcionId", a.get("id")));
                            }
                        } catch (Exception e) {
                            logger.error("Error al buscar adopción para contrato: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error al cargar adoptante para detalle: " + e.getMessage());
            }
        }

        if (sol != null && sol.get("animalId") != null) {
            try {
                Map<String, Object> animal = restTemplate.exchange(
                        apiUrl + "/v1/animales/" + sol.get("animalId"),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
                model.addAttribute("animalData", animal);
            } catch (Exception e) {
                logger.error("Error al cargar animal para detalle: " + e.getMessage());
            }
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_DETALLE.getPath());
        
        // Aseguramos que el ID del usuario actual esté disponible para comparaciones de propiedad
        Object userIdObj = model.getAttribute("currentUserId");
        if (userIdObj != null) {
            model.addAttribute("currentUserId", userIdObj);
        }

        // Si es una petición HTMX, devolvemos solo el fragmento del contenido
        if (request != null && "true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Solicitud_DETALLE.getPath() + " :: content";
        }
        
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/web/solicitudes/{id}")
    public String redireccionDetalle(@PathVariable Integer id) {
        return "redirect:/web/solicitudes/" + id + "/detalle";
    }

    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
