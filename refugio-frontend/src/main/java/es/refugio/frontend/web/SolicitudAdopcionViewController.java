package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping(WebRoutes.SOLICITUDES_MODAL_NUEVA)
    public String modalNueva(@RequestParam Integer animalId, Model model) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of());
        }
        return "fragments/modals/modal-solicitud-directa :: modal";
    }

    @GetMapping(WebRoutes.SOLICITUDES_MODAL_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @SuppressWarnings("unchecked")
    public String modalEditar(@PathVariable Integer id, Model model) {
        try {
            Map<String, Object> solicitud = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id, Map.class);
            model.addAttribute("solicitud", solicitud);

            // Cargar datos para resolver nombres
            List<Object> animales = fetchList("/v1/animales");
            List<Object> adoptantes = fetchList("/v1/adoptantes");
            List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

            Map<String, Object> animalesMap = new HashMap<>();
            for (Object a : animales) {
                if (a instanceof Map) {
                    animalesMap.put(String.valueOf(((Map<?, ?>) a).get("id")), a);
                }
            }

            Map<String, Map<String, Object>> usuariosMap = new HashMap<>();
            for (Object u : usuarios) {
                if (u instanceof Map) {
                    usuariosMap.put(String.valueOf(((Map<?, ?>) u).get("id")), (Map<String, Object>) u);
                }
            }

            // Resolver nombres para el modal (solo los necesarios)
            if (solicitud != null) {
                Object aId = solicitud.get("animalId");
                Object animal = animalesMap.get(String.valueOf(aId));
                model.addAttribute("animalNombre", animal != null ? ((Map<?,?>)animal).get("nombre") : "Desconocido");

                Object adId = solicitud.get("adoptanteId");
                // Buscar el adoptante para sacar su usuarioId
                for (Object ad : adoptantes) {
                    Map<?,?> adMap = (Map<?,?>) ad;
                    if (String.valueOf(adMap.get("id")).equals(String.valueOf(adId))) {
                        Object uId = adMap.get("usuarioId");
                        Map<String, Object> user = usuariosMap.get(String.valueOf(uId));
                        if (user != null) {
                            model.addAttribute("adoptanteNombre", user.get("nombre") + " " + user.get("apellido"));
                        }
                        break;
                    }
                }
            }

            model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        } catch (Exception e) {
            logger.error("Error al cargar datos para el modal de edición: " + e.getMessage());
        }
        return "fragments/modals/modal-solicitud-editar :: modal";
    }

    @GetMapping(WebRoutes.SOLICITUDES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        List<Object> solicitudes = fetchList("/v1/solicitudes-adopcion");
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
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number)
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), (Map<String, Object>) u);
            }
        }

        Map<String, String> adoptanteNombres = new HashMap<>();
        for (Object a : adoptantes) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                Object uid = ((Map<?, ?>) a).get("usuarioId");
                if (id instanceof Number && uid instanceof Number) {
                    Map<String, Object> user = usuariosMap.get(String.valueOf(((Number) uid).intValue()));
                    if (user != null) {
                        Object nombre = user.get("nombre");
                        Object apellido = user.get("apellido");
                        adoptanteNombres.put(String.valueOf(((Number) id).intValue()),
                                ((nombre != null ? nombre.toString() : "") + " "
                                        + (apellido != null ? apellido.toString() : "")).trim());
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

        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), solicitudes);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("adoptanteNombres", adoptanteNombres);
        model.addAttribute("adoptanteUsuarioIds", adoptanteUsuarioIds);
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_BASE);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    @GetMapping(WebRoutes.SOLICITUDES_MIS_ADOPTADOS)
    public String misAdoptados(Model model) {
        // 1. Obtener ID del usuario actual del modelo (inyectado por GlobalModelAttributesAdvice)
        Object userIdObj = model.getAttribute("currentUserId");
        if (userIdObj == null) return "redirect:/login";
        
        Integer currentUserId = (userIdObj instanceof Number) ? ((Number) userIdObj).intValue() : Integer.parseInt(userIdObj.toString());

        // 2. Buscar perfil de adoptante para este usuario
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        Integer adoptanteId = null;
        for (Object obj : adoptantes) {
            if (obj instanceof Map) {
                Map<String, Object> a = (Map<String, Object>) obj;
                Object uid = a.get("usuarioId");
                if (uid != null && ((Number) uid).intValue() == currentUserId) {
                    adoptanteId = ((Number) a.get("id")).intValue();
                    break;
                }
            }
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
        }

        // 4. Si no hay solicitudes, mostrar vista vacía con mensaje personalizado
        if (misSolicitudes.isEmpty()) {
            model.addAttribute("mensajeVacio", "Vaya, parece que aún no te has hecho con ninguno de nuestros amiguitos");
            model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.MIS_ADOPTADOS_VACIO.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        // 5. Enriquecer con datos de animales para mostrar en la lista
        List<Object> animales = fetchList("/v1/animales");
        Map<String, Object> animalesMap = new HashMap<>();
        for (Object a : animales) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                if (id instanceof Number) animalesMap.put(id.toString(), a);
            }
        }

        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), misSolicitudes);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.MIS_ADOPTADOS_LISTA.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_NUEVA)
    public String formulario(Model model, @RequestParam(required = false) Integer animalId) {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("fecha", LocalDateTime.now().toString());
        if (animalId != null)
            solicitud.put("animalId", animalId);

        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales", fetchList("/v1/animales"));
        model.addAttribute("adoptantes", fetchList("/v1/adoptantes"));
        model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_NUEVA);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
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
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object solicitud = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales", fetchList("/v1/animales"));
        model.addAttribute("adoptantes", fetchList("/v1/adoptantes"));
        model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
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
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("estado", estado);
        body.put("comentario", comentario);
        body.put("fecha", LocalDateTime.now().toString());

        restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud editada correctamente");
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/solicitudes-adopcion/" + id);
        if ("true".equals(request.getHeader("HX-Request")))
            return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.SOLICITUDES_BASE).build();
    }

    @PostMapping(WebRoutes.SOLICITUDES_APROBAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public String aprobarSolicitud(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> existing = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id,
                    Map.class);
            if (existing != null) {
                Map<String, Object> body = new HashMap<>(existing);
                body.put("estado", "APROBADA");
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", "Solicitud aprobada");
            }
        } catch (Exception e) {
            logger.error("Error al aprobar solicitud: " + e.getMessage());
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_RECHAZAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public String rechazarSolicitud(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> existing = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id,
                    Map.class);
            if (existing != null) {
                Map<String, Object> body = new HashMap<>(existing);
                body.put("estado", "RECHAZADA");
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", "Solicitud rechazada");
            }
        } catch (Exception e) {
            logger.error("Error al rechazar solicitud: " + e.getMessage());
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
            boolean isStaff = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VOLUNTARIO") || a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdoptante || isStaff) {
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
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_CONVERSION.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_DIRECTA_FORM)
    public String formularioDirecta(Model model, @RequestParam Integer animalId) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_DIRECTA_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_CONVERTIR)
    public String procesarConversionYAdopcion(
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response,
            Model model) {

        Map<String, Object> body = new HashMap<>();
        body.put("dni", dni);
        body.put("direccion", direccion);
        body.put("fechaNacimiento", fechaNacimiento);
        body.put("animalId", animalId);
        body.put("comentario", comentario);

        logger.info("Enviando solicitud de conversión y adopción al backend: " + apiUrl + "/v1/solicitudes-adopcion/convertir-y-adopcion");
        try {
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/convertir-y-adopcion", body, Object.class);
        } catch (org.springframework.web.client.RestClientResponseException e) {
            Map<String, Object> responseBody = e.getResponseBodyAs(Map.class);
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
            
            // Si hay error, volvemos a mostrar el formulario de conversión con los datos introducidos
            try {
                Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
                model.addAttribute("animal", animal);
            } catch (Exception ignored) {
                model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
            }
            
            model.addAttribute("dni", dni);
            model.addAttribute("direccion", direccion);
            model.addAttribute("fechaNacimiento", fechaNacimiento);
            model.addAttribute("comentario", comentario);
            model.addAttribute("errorMessage", errorMsg);
            
            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_CONVERSION.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        // Actualizar el rol en el microservicio de Auth
        try {
            Map<String, Object> me = restTemplate.getForObject(authUrl + "/v1/me", Map.class);
            if (me != null && me.containsKey("id")) {
                String currentRol = (String) me.get("rol");
                Object rawId = me.get("id");
                Integer usuarioId = (rawId instanceof Number) ? ((Number) rawId).intValue()
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
                        authUrl + "/v1/usuarios/" + usuarioId + "/rol",
                        org.springframework.http.HttpMethod.PUT,
                        new org.springframework.http.HttpEntity<>(patchBody),
                        Map.class
                    );

                    if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                        String newToken = (String) responseEntity.getBody().get("token");
                        if (newToken != null) {
                            // 1. Actualizar la cookie en el navegador
                            jakarta.servlet.http.Cookie authCookie = new jakarta.servlet.http.Cookie("JWT_TOKEN", newToken);
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
                "Solicitud enviada con éxito. Tu perfil ha sido actualizado a Adoptante.");
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
        }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud enviada con éxito");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.SOLICITUDES_PUBLICO_REGISTRO)
    public String procesarRegistroYAdopcion(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String contrasena,
            @RequestParam String telefono,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        // 1. Crear usuario en Auth
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("nombre", nombre);
        userBody.put("apellido", apellido);
        userBody.put("email", email);
        userBody.put("contrasena", contrasena);
        userBody.put("telefono", telefono);
        userBody.put("rol", "ROLE_ADOPTANTE");

        Integer usuarioId = null;
        try {
            Map respUser = restTemplate.postForObject(authUrl + "/v1/usuarios/publico", userBody, Map.class);
            if (respUser != null && respUser.get("id") != null) {
                Object rawId = respUser.get("id");
                usuarioId = (rawId instanceof Number) ? ((Number) rawId).intValue()
                        : Integer.parseInt(rawId.toString());
            } else {
                throw new Exception("No se pudo obtener el ID del usuario tras el registro");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al registrar el usuario en Auth: " + e.getMessage());
            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        // 2. Crear solicitud en Backend
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        body.put("nombre", nombre);
        body.put("apellido", apellido);
        body.put("email", email);
        body.put("contrasena", contrasena);
        body.put("telefono", telefono);
        body.put("dni", dni);
        body.put("direccion", direccion);
        body.put("fechaNacimiento", fechaNacimiento);
        body.put("animalId", animalId);
        body.put("comentario", comentario);

        try {
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/publico/registro-y-adopcion", body,
                    Object.class);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar la solicitud: " + e.getMessage());
            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        // 3. Auto-login tras registro exitoso
        try {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(email, null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE"))));

            logger.info("Registro exitoso para usuario ID: " + usuarioId);
        } catch (Exception e) {
            logger.error("Error al establecer el contexto de autenticación local: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "¡Registro y solicitud completados! Por favor, inicie sesión con su nuevo usuario.");
        return "redirect:" + WebRoutes.HOME;
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
