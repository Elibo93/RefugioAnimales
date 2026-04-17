package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SolicitudAdopcionViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

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

    @GetMapping(WebRoutes.SOLICITUDES_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), fetchList("/v1/solicitudes-adopcion"));
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_BASE);
        model.addAttribute("showBack", false);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_NUEVA)
    public String formulario(Model model, @RequestParam(required = false) Integer animalId) {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("fecha", LocalDateTime.now().toString());
        if (animalId != null) solicitud.put("animalId", animalId);

        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales",   fetchList("/v1/animales"));
        model.addAttribute("adoptantes", fetchList("/v1/adoptantes"));
        model.addAttribute("estados",    List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_NUEVA);
        model.addAttribute("showBack", true);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_NUEVA)
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

    @GetMapping(WebRoutes.SOLICITUDES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object solicitud = restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales",   fetchList("/v1/animales"));
        model.addAttribute("adoptantes", fetchList("/v1/adoptantes"));
        model.addAttribute("estados",    List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_EDITAR)
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

    @PostMapping(WebRoutes.SOLICITUDES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/solicitudes-adopcion/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.SOLICITUDES_BASE).build();
    }

    @GetMapping(WebRoutes.SOLICITUDES_PDF)
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
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_REGISTRO.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_OPCIONES)
    public String formularioOpciones(Model model, @RequestParam Integer animalId) {
        try {
            Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", Map.of("id", animalId, "nombre", "Animal"));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_OPCIONES.getPath());
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
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_CONVERSION.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_CONVERTIR)
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

        // Actualizar contexto de seguridad (cambio de rol)
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
        var currentAuth = SecurityContextHolder.getContext().getAuthentication();
        var auth = new UsernamePasswordAuthenticationToken(currentAuth.getPrincipal(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud enviada con éxito. Tu perfil ha sido actualizado a Adoptante.");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.SOLICITUDES_DIRECTA)
    public String procesarAdopcionDirecta(
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId",   animalId);
        body.put("comentario", comentario);

        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/directa", body, Object.class);

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
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
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

        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/publico/registro-y-adopcion", body, Object.class);

        // Auto-login en el Frontend
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
        var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud enviada con éxito");
        return "redirect:" + WebRoutes.HOME;
    }

    private List<Object> fetchList(String path) {
        try {
            Object[] arr = restTemplate.getForObject(apiUrl + path, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { 
            e.printStackTrace();
            return List.of(); 
        }
    }
}
