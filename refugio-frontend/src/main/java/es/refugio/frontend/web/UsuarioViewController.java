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
import java.util.*;

@Controller
@RequiredArgsConstructor
public class UsuarioViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping(WebRoutes.PERSONAS_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Persona_LIST.getName(), fetchList("/v1/usuarios"));
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), Map.of());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_NUEVO)
    public String crearPersona(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            Model model) {

        Map<String, Object> body = new HashMap<>();
        body.put("nombre",    nombre);    body.put("apellido",   apellido);
        body.put("email",     email);     body.put("telefono",   telefono);
        body.put("contrasena", contrasena); body.put("rol", "ROLE_PUBLICO");

        restTemplate.postForObject(apiUrl + "/v1/usuarios", body, Object.class);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_CREATED.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object persona = restTemplate.getForObject(apiUrl + "/v1/usuarios/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("nombre",   nombre);   body.put("apellido", apellido);
        body.put("email",    email);    body.put("telefono", telefono);

        restTemplate.put(apiUrl + "/v1/usuarios/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Persona editada correctamente");
        return "redirect:" + WebRoutes.PERSONAS_BASE;
    }

    @PostMapping(WebRoutes.PERSONAS_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/usuarios/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.PERSONAS_BASE).build();
    }

    @GetMapping(WebRoutes.PERSONAS_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> personas = fetchList("/v1/usuarios");
        Context context = new Context();
        context.setVariable("personas", personas);
        String html = templateEngine.process(ThymTemplates.Persona_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=personas.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    private List<Object> fetchList(String path) {
        try {
            Object[] arr = restTemplate.getForObject(apiUrl + path, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { return List.of(); }
    }
}
