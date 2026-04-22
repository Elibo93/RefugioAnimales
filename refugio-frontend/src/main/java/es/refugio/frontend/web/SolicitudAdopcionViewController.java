package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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
@RequestMapping("/web/solicitudes")
@RequiredArgsConstructor
public class SolicitudAdopcionViewController {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudAdopcionViewController.class);

    @Autowired
    private RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping("/modal-nueva")
    public String modalNueva(@RequestParam Integer animalId, Model model) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of());
        }
        return "fragments/modals/modal-solicitud-directa :: modal";
    }

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        List<Object> solicitudes = fetchList("/v1/solicitudes-adopcion");
        List<Object> animales   = fetchList("/v1/animales");
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        List<Object> usuarios   = fetchList(authUrl + "/v1/usuarios");

        Map<String, Object> animalesMap = new HashMap<>();
        for (Object a : animales) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                if (id instanceof Number) animalesMap.put(String.valueOf(((Number) id).intValue()), a);
            }
        }

        Map<String, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) usuariosMap.put(String.valueOf(((Number) id).intValue()), (Map<String, Object>) u);
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
                            ((nombre != null ? nombre.toString() : "") + " " + (apellido != null ? apellido.toString() : "")).trim());
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

    @GetMapping("/nueva")
    public String formulario(Model model, @RequestParam(required = false) Integer animalId) {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("fecha", LocalDateTime.now().toString());
        if (animalId != null) solicitud.put("animalId", animalId);

        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales",   fetchList("/v1/animales"));
        model.addAttribute("adoptantes", fetchList("/v1/adoptantes"));
        model.addAttribute("estados",    List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_NUEVA);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/nueva")
    public String crear(@RequestParam Integer animalId,
            @RequestParam Integer adoptanteId,
            @RequestParam String comentario,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId",    animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("comentario",  comentario);
        body.put("fecha",       LocalDateTime.now().toString());

        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion", body, Object.class);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return "fragments/content/solicitud-creada :: success-modal";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud de adopción registrada correctamente");
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object solicitud = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales",   fetchList("/v1/animales"));
        model.addAttribute("adoptantes", fetchList("/v1/adoptantes"));
        model.addAttribute("estados",    List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/{id}/editar")
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer animalId,
            @RequestParam Integer adoptanteId,
            @RequestParam String estado,
            @RequestParam String comentario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId); body.put("adoptanteId", adoptanteId);
        body.put("estado",   estado);   body.put("comentario",  comentario);
        body.put("fecha",    LocalDateTime.now().toString());

        restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud editada correctamente");
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping("/{id}/borrar")
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/solicitudes-adopcion/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.SOLICITUDES_BASE).build();
    }

    @PostMapping("/web/solicitudes/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        // Fetch existing using GET, update estado to APROBADA
        try {
            Map<String, Object> existing = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id, Map.class);
            if (existing != null) {
                Map<String, Object> body = new HashMap<>(existing);
                body.put("estado", "APROBADA");
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", "Solicitud aprobada");
            }
        } catch(Exception e) {}
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping("/web/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> existing = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id, Map.class);
            if (existing != null) {
                Map<String, Object> body = new HashMap<>(existing);
                body.put("estado", "RECHAZADA");
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", "Solicitud rechazada");
            }
        } catch(Exception e) {}
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping("/pdf")
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

    @GetMapping("/publico/registro-y-adopcion")
    public String formularioPublico(Model model, @RequestParam Integer animalId) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_REGISTRO.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/publico/opciones")
    public String formularioOpciones(Model model, @RequestParam Integer animalId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean isAdoptante = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADOPTANTE"));
            if (isAdoptante) {
                return "redirect:/web/solicitudes/publico/directa/formulario?animalId=" + animalId;
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
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_OPCIONES.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/publico/convertir")
    public String formularioConversion(Model model, @RequestParam Integer animalId) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_CONVERSION.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/publico/directa/formulario")
    public String formularioDirecta(Model model, @RequestParam Integer animalId) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_DIRECTA_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/publico/convertir")
    public String procesarConversionYAdopcion(
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("dni",             dni);
        body.put("direccion",       direccion);
        body.put("fechaNacimiento", fechaNacimiento);
        body.put("animalId",        animalId);
        body.put("comentario",      comentario);

        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/convertir-y-adopcion", body, Object.class);

        // Actualizar el rol en el microservicio de Auth
        try {
            // CRÍTICO: El endpoint /me pertenece a AUTH, no al Backend
            Map<String, Object> me = restTemplate.getForObject(authUrl + "/v1/me", Map.class);
            if (me != null && me.containsKey("id")) {
                Object rawId = me.get("id");
                Integer usuarioId = (rawId instanceof Number) ? ((Number)rawId).intValue() : Integer.parseInt(rawId.toString());
                
                Map<String, String> patchBody = new HashMap<>();
                patchBody.put("rol", "ROLE_ADOPTANTE");
                restTemplate.put(authUrl + "/v1/usuarios/" + usuarioId + "/rol", patchBody);
            }
        } catch(Exception e) { 
            System.err.println("Error updating role in Auth: " + e.getMessage());
            e.printStackTrace(); 
        }

        // Actualizar contexto de seguridad local (cambio de rol)
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
        var currentAuth = SecurityContextHolder.getContext().getAuthentication();
        var auth = new UsernamePasswordAuthenticationToken(currentAuth.getPrincipal(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud enviada con éxito. Tu perfil ha sido actualizado a Adoptante.");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping("/publico/directa")
    public String procesarAdopcionDirecta(
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId",   animalId);
        body.put("comentario", comentario);

        try { restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/directa", body, Object.class); } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) { redirectAttributes.addFlashAttribute("errorMessage", "Debe iniciar sesión para realizar esta acción."); return "redirect:" + WebRoutes.SOLICITUDES_OPCIONES + "?animalId=" + animalId; }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud enviada con éxito");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping("/publico/registro-y-adopcion")
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
        userBody.put("nombre",          nombre);
        userBody.put("apellido",        apellido);
        userBody.put("email",           email);
        userBody.put("contrasena",      contrasena);
        userBody.put("telefono",        telefono);
        userBody.put("rol",             "ROLE_ADOPTANTE");

        Integer usuarioId = null;
        try {
            Map respUser = restTemplate.postForObject(authUrl + "/v1/usuarios/publico", userBody, Map.class);
            if (respUser != null && respUser.get("id") != null) {
                Object rawId = respUser.get("id");
                usuarioId = (rawId instanceof Number) ? ((Number)rawId).intValue() : Integer.parseInt(rawId.toString());
            } else {
                throw new Exception("No se pudo obtener el ID del usuario tras el registro");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar el usuario en Auth: " + e.getMessage()); 
            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        // 2. Crear solicitud en Backend
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId",       usuarioId);
        body.put("nombre",          nombre);
        body.put("apellido",        apellido);
        body.put("email",           email);
        body.put("contrasena",      contrasena);
        body.put("telefono",        telefono);
        body.put("dni",             dni);
        body.put("direccion",       direccion);
        body.put("fechaNacimiento", fechaNacimiento);
        body.put("animalId",        animalId);
        body.put("comentario",      comentario);

        try { restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/publico/registro-y-adopcion", body, Object.class); } catch (Exception e) { redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar la solicitud: " + e.getMessage()); return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId; }

        // 3. Auto-login real
        try {
            // Generar token JWT localmente ya que compartimos el secreto
            // Necesitamos acceder a la instancia de JwtTokenProvider del Frontend
            // No podemos inyectarla directamente al ser un Controller, pero podemos usarlo si está disponible.
            // Para persistir la sesión en el navegador:
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE")))
            );
            
            // Simular un mensaje de bienvenida y pedir login es más seguro si no queremos relayar tokens complejos
            logger.info("Registro exitoso para usuario ID: " + usuarioId);
        } catch (Exception e) {
            logger.error("Error setting local auth context: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage", "¡Registro y solicitud completados! Por favor, inicie sesión con su nuevo usuario.");
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
