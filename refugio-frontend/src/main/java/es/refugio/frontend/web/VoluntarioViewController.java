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

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.VOLUNTARIOS_BASE)
    public String listar(Model model) {
        List<Object> voluntarios = fetchList("/v1/voluntarios");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        // Build usuariosMap: Map<userId, userObject>
        Map<String, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), u);
                }
            }
        }

        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), voluntarios);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_BASE);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), new HashMap<String, Object>());
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_NUEVO);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object voluntario = restTemplate.getForObject(apiUrl + "/v1/voluntarios/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), voluntario);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_EDITAR);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    public String crearVoluntario(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam String disponibilidad,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String contrasena,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("disponibilidad", disponibilidad);
        if (idUsuario != null) body.put("usuarioId", idUsuario);
        if (nombre    != null) body.put("nombre",    nombre);
        if (email     != null) body.put("email",     email);
        if (contrasena != null) body.put("contrasena", contrasena);

        restTemplate.postForObject(apiUrl + "/v1/voluntarios", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "¡Bienvenido al equipo voluntario!");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    public String editarVoluntario(@PathVariable Integer id,
            @RequestParam String disponibilidad,
            @RequestParam String email,
            @RequestParam String telefono,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("disponibilidad", disponibilidad);
        body.put("email",          email);
        body.put("telefono",       telefono);

        restTemplate.put(apiUrl + "/v1/voluntarios/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Voluntario editado correctamente");
        return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_ELIMINAR)
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
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> voluntarios = fetchList("/v1/voluntarios");
        Context context = new Context();
        context.setVariable("voluntarios", voluntarios);
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
