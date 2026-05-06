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
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
public class HistorialMedicoViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping(WebRoutes.HISTORIALES_BASE)
    public String listar(Model model, 
            @RequestParam(required = false) Integer animalId,
            @RequestParam(required = false) String successMessage) {
        
        List<Object> historiales;
        if (animalId != null) {
            historiales = fetchList("/v1/historial-medico/animal/" + animalId);
        } else {
            historiales = fetchList("/v1/historial-medico");
        }
        List<Object> animales = fetchList("/v1/animales");

        if (animalId != null) {
            historiales = historiales.stream()
                .filter(h -> h instanceof Map && Objects.equals(((Map<?,?>)h).get("animalId"), animalId))
                .toList();
        }

        Map<Integer, Object> animalesMap = new HashMap<>();
        for (Object a : animales) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                if (id instanceof Number) animalesMap.put(((Number) id).intValue(), a);
            }
        }

        model.addAttribute(ModelAttribute.Historial_LIST.getName(), historiales);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("selectedAnimalId", animalId);
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.HISTORIALES_NUEVO)
    public String formulario(Model model, HttpServletRequest request) {
        Map<String, Object> historial = new HashMap<>();
        historial.put("fecha", LocalDateTime.now().toString());
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), historial);
        model.addAttribute("animales", fetchList("/v1/animales"));
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Historial_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.HISTORIALES_NUEVO)
    public String crear(@RequestParam Integer animalId,
            @RequestParam String descripcion,
            @RequestParam String tratamiento,
            @RequestParam String veterinario,
            @RequestParam(required = false) String fecha,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId",    animalId);
        body.put("descripcion", descripcion);
        body.put("tratamiento", tratamiento);
        body.put("veterinario", veterinario);
        if (fecha != null && !fecha.isEmpty()) {
            body.put("fecha", fecha);
        } else {
            body.put("fecha", LocalDateTime.now().toString());
        }

        restTemplate.postForObject(apiUrl + "/v1/historial-medico", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Historial médico registrado correctamente");
        return "redirect:" + WebRoutes.HISTORIALES_BASE;
    }

    @GetMapping(WebRoutes.HISTORIALES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        Object historial = restTemplate.getForObject(apiUrl + "/v1/historial-medico/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), historial);
        model.addAttribute("animales", fetchList("/v1/animales"));

        // Cargar datos del animal para el selector inteligente
        if (historial instanceof Map) {
            Object animalId = ((Map<?, ?>) historial).get("animalId");
            if (animalId != null) {
                try {
                    Object animalData = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
                    model.addAttribute("animalData", animalData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Historial_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.HISTORIALES_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer animalId,
            @RequestParam String descripcion,
            @RequestParam String tratamiento,
            @RequestParam String veterinario,
            @RequestParam(required = false) String fecha,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId",    animalId);
        body.put("descripcion", descripcion);
        body.put("tratamiento", tratamiento);
        body.put("veterinario", veterinario);
        if (fecha != null && !fecha.isEmpty()) {
            body.put("fecha", fecha);
        } else {
            body.put("fecha", LocalDateTime.now().toString());
        }

        restTemplate.put(apiUrl + "/v1/historial-medico/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Historial médico editado correctamente");
        return "redirect:" + WebRoutes.HISTORIALES_BASE;
    }

    @PostMapping(WebRoutes.HISTORIALES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/historial-medico/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.HISTORIALES_BASE).build();
    }

    @GetMapping(WebRoutes.HISTORIALES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> historiales = fetchList("/v1/historial-medico");
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

    @GetMapping(WebRoutes.HISTORIALES_BASE + "/{id}/detalle")
    public String verDetalle(@PathVariable Integer id, Model model) {
        Object historial = restTemplate.getForObject(apiUrl + "/v1/historial-medico/" + id, Object.class);
        model.addAttribute("historial", historial);
        
        if (historial instanceof Map) {
            Object animalId = ((Map<?, ?>) historial).get("animalId");
            if (animalId != null) {
                try {
                    Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
                    model.addAttribute("animal", animal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return "fragments/content/historial-medico-detalle-modal :: detalle";
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
