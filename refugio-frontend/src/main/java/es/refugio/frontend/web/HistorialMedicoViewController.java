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
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class HistorialMedicoViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping(WebRoutes.HISTORIALES_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Historial_LIST.getName(), fetchList("/v1/historiales"));
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.HISTORIALES_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), Map.of("fecha", LocalDateTime.now().toString()));
        model.addAttribute("animales", fetchList("/v1/animales"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.HISTORIALES_NUEVO)
    public String crear(@RequestParam Integer animalId,
            @RequestParam String descripcion,
            @RequestParam String tratamiento,
            @RequestParam String veterinario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId",    animalId);
        body.put("descripcion", descripcion);
        body.put("tratamiento", tratamiento);
        body.put("veterinario", veterinario);
        body.put("fecha",       LocalDateTime.now().toString());

        restTemplate.postForObject(apiUrl + "/v1/historiales", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Historial médico registrado correctamente");
        return "redirect:" + WebRoutes.HISTORIALES_BASE;
    }

    @GetMapping(WebRoutes.HISTORIALES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object historial = restTemplate.getForObject(apiUrl + "/v1/historiales/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), historial);
        model.addAttribute("animales", fetchList("/v1/animales"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.HISTORIALES_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer animalId,
            @RequestParam String descripcion,
            @RequestParam String tratamiento,
            @RequestParam String veterinario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId",    animalId);
        body.put("descripcion", descripcion);
        body.put("tratamiento", tratamiento);
        body.put("veterinario", veterinario);
        body.put("fecha",       LocalDateTime.now().toString());

        restTemplate.put(apiUrl + "/v1/historiales/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Historial médico editado correctamente");
        return "redirect:" + WebRoutes.HISTORIALES_BASE;
    }

    @PostMapping(WebRoutes.HISTORIALES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/historiales/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.HISTORIALES_BASE).build();
    }

    @GetMapping(WebRoutes.HISTORIALES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> historiales = fetchList("/v1/historiales");
        Context context = new Context();
        context.setVariable("historiales", historiales);
        String html = templateEngine.process(ThymTemplates.Historial_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=historiales.pdf");
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
