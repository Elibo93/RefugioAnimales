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
public class TareaViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.TAREAS_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        List<Object> tareas      = fetchList("/v1/tareas");
        List<Object> voluntarios = fetchList("/v1/voluntarios");
        List<Object> usuarios    = fetchList(authUrl + "/v1/usuarios");

        Map<Integer, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) usuariosMap.put(((Number) id).intValue(), (Map<String, Object>) u);
            }
        }

        Map<String, String> voluntarioNombres = new HashMap<>();
        for (Object v : voluntarios) {
            if (v instanceof Map) {
                Object vId = ((Map<?, ?>) v).get("id");
                Object uId = ((Map<?, ?>) v).get("usuarioId");
                if (vId instanceof Number && uId instanceof Number) {
                    Map<String, Object> user = usuariosMap.get(((Number) uId).intValue());
                    if (user != null) {
                        Object n = user.get("nombre");
                        Object a = user.get("apellido");
                        voluntarioNombres.put(vId.toString(), ((n!=null?n.toString():"") + " " + (a!=null?a.toString():"")).trim());
                    }
                }
            }
        }

        model.addAttribute(ModelAttribute.Tarea_LIST.getName(), tareas);
        model.addAttribute("voluntarioNombres", voluntarioNombres);
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.TAREAS_NUEVA)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), Map.of("fecha", LocalDateTime.now().toString()));
        model.addAttribute("voluntarios", fetchList("/v1/voluntarios"));
        model.addAttribute("estados", List.of("PENDIENTE", "EN_PROGRESO", "COMPLETADA", "CANCELADA"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.TAREAS_NUEVA)
    public String crear(@RequestParam String descripcion,
            @RequestParam String estado,
            @RequestParam(required = false) List<Integer> voluntarioIds,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("descripcion",   descripcion);
        body.put("estado",        estado);
        body.put("fecha",         LocalDateTime.now().toString());
        body.put("voluntarioIds", voluntarioIds != null ? voluntarioIds : List.of());

        restTemplate.postForObject(apiUrl + "/v1/tareas", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Tarea creada correctamente");
        return "redirect:" + WebRoutes.TAREAS_BASE;
    }

    @GetMapping(WebRoutes.TAREAS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object tarea = restTemplate.getForObject(apiUrl + "/v1/tareas/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), tarea);
        model.addAttribute("voluntarios", fetchList("/v1/voluntarios"));
        model.addAttribute("estados", List.of("PENDIENTE", "EN_PROGRESO", "COMPLETADA", "CANCELADA"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.TAREAS_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String descripcion,
            @RequestParam String estado,
            @RequestParam(required = false) List<Integer> voluntarioIds,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("descripcion",   descripcion);
        body.put("estado",        estado);
        body.put("fecha",         LocalDateTime.now().toString());
        body.put("voluntarioIds", voluntarioIds != null ? voluntarioIds : List.of());

        restTemplate.put(apiUrl + "/v1/tareas/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Tarea editada correctamente");
        return "redirect:" + WebRoutes.TAREAS_BASE;
    }

    @PostMapping(WebRoutes.TAREAS_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/tareas/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.TAREAS_BASE).build();
    }

    @GetMapping(WebRoutes.TAREAS_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> tareas = fetchList("/v1/tareas");
        Context context = new Context();
        context.setVariable("tareas", tareas);
        String html = templateEngine.process(ThymTemplates.Tarea_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=tareas.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { return List.of(); }
    }
}
