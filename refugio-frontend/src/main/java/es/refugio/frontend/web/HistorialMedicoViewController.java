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
import es.refugio.frontend.web.dto.HistorialMedicoRecord;
import es.refugio.frontend.web.dto.AnimalRecord;
import es.refugio.frontend.web.dto.PaginatedResponse;
import es.refugio.frontend.web.util.ViewControllerHelper;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
public class HistorialMedicoViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping(WebRoutes.HISTORIALES_BASE)
    public String listar(Model model, 
            @RequestParam(required = false) Integer animalId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String successMessage,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        response.setHeader("Vary", "HX-Request");
        
        PaginatedResponse<HistorialMedicoRecord> pagination = null;
        List<HistorialMedicoRecord> historiales;
        
        if (animalId != null) {
            historiales = helper.fetchList(apiUrl + "/v1/historial-medico/animal/" + animalId, HistorialMedicoRecord.class);
        } else {
            pagination = helper.fetchPaginated(apiUrl + "/v1/historial-medico", page, size, HistorialMedicoRecord.class);
            historiales = pagination.items();
        }
        List<AnimalRecord> animales = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);

        if (animalId != null) {
            final Integer finalAnimalId = animalId;
            historiales = historiales.stream()
                .filter(h -> Objects.equals(h.animalId(), finalAnimalId))
                .toList();
        }

        Map<Integer, AnimalRecord> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(a.id(), a);
        }

        model.addAttribute(ModelAttribute.Historial_LIST.getName(), historiales);
        model.addAttribute("pagination", pagination);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("selectedAnimalId", animalId);
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Historial_LIST.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.HISTORIALES_NUEVO)
    public String formulario(Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Vary", "HX-Request");
        Map<String, Object> historial = new HashMap<>();
        historial.put("id", null);
        historial.put("animalId", null);
        historial.put("veterinario", null);
        historial.put("descripcion", null);
        historial.put("tratamiento", null);
        historial.put("fecha", LocalDateTime.now().toString());
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), historial);
        model.addAttribute("animales", helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class));
        
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
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Vary", "HX-Request");
        HistorialMedicoRecord historial = helper.fetchObject(apiUrl + "/v1/historial-medico/" + id, HistorialMedicoRecord.class);
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), historial);
        model.addAttribute("animales", helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class));

        // Cargar datos del animal para el selector inteligente
        if (historial != null && historial.animalId() != null) {
            try {
                AnimalRecord animalData = helper.fetchObject(apiUrl + "/v1/animales/" + historial.animalId(), AnimalRecord.class);
                model.addAttribute("animalData", animalData);
            } catch (Exception e) {
                e.printStackTrace();
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
        return "redirect:" + WebRoutes.HISTORIALES_BASE + "/" + id + "/detalle";
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
        List<HistorialMedicoRecord> historiales = helper.fetchList(apiUrl + "/v1/historial-medico", HistorialMedicoRecord.class);
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
    public String verDetalle(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Vary", "HX-Request");
        HistorialMedicoRecord historial = helper.fetchObject(apiUrl + "/v1/historial-medico/" + id, HistorialMedicoRecord.class);
        model.addAttribute("historial", historial);
        
        if (historial != null && historial.animalId() != null) {
            try {
                AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + historial.animalId(), AnimalRecord.class);
                model.addAttribute("animal", animal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return "fragments/content/historial-medico-detalle :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/historial-medico-detalle");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }
}
