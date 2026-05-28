package es.refugio.frontend.web;

import org.springframework.context.i18n.LocaleContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import es.refugio.common.util.ExcelExportHelper;
import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.dto.HistorialMedicoRecord;
import es.refugio.frontend.web.dto.AnimalRecord;
import es.refugio.frontend.web.dto.PaginatedResponse;
import es.refugio.frontend.service.MessageService;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import es.refugio.frontend.service.HistorialMedicoService;

/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Historial Medico.
 *
 * @author Elisabeth
 * @author Diego
 */
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
public class HistorialMedicoViewController {

    private final HistorialMedicoService historialMedicoService;
    private final TemplateEngine templateEngine;
    private final MessageService messageService;

    @GetMapping(WebRoutes.HISTORIALES_BASE)
    public String listar(Model model,
            @RequestParam(required = false) Integer animalId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String successMessage,
            HttpServletRequest request,
            HttpServletResponse response) {

        response.setHeader("Vary", "HX-Request");

        PaginatedResponse<HistorialMedicoRecord> pagination = null;
        List<HistorialMedicoRecord> historiales;

        if (animalId != null) {
            historiales = historialMedicoService.fetchByAnimalId(animalId);
        } else if (q != null && !q.trim().isEmpty()) {
            historiales = historialMedicoService.fetchAllHistoriales();
        } else {
            pagination = historialMedicoService.fetchPaginated(page, size);
            historiales = pagination.items();
        }
        List<AnimalRecord> animales = historialMedicoService.fetchAllAnimales();

        Map<Integer, AnimalRecord> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(a.id(), a);
        }

        if (animalId != null) {
            final Integer finalAnimalId = animalId;
            historiales = historiales.stream()
                    .filter(h -> Objects.equals(h.animalId(), finalAnimalId))
                    .toList();
        } else if (q != null && !q.trim().isEmpty()) {
            final String query = q.toLowerCase().trim();
            historiales = historiales.stream()
                    .filter(h -> {
                        AnimalRecord a = h.animalId() != null ? animalesMap.get(h.animalId()) : null;
                        if (a == null) return false;
                        String nombre = a.nombre() != null ? a.nombre().toLowerCase() : "";
                        String chipId = a.chipId() != null ? a.chipId().toLowerCase() : "";
                        String idStr = String.valueOf(a.id());
                        return nombre.contains(query) || chipId.contains(query) || idStr.contains(query);
                    })
                    .toList();

            // Paginar manualmente el resultado
            int total = historiales.size();
            int totalPages = (int) Math.ceil((double) total / size);
            int start = (page - 1) * size;
            int end = Math.min(start + size, total);
            if (start < total) {
                historiales = historiales.subList(start, end);
            } else {
                historiales = List.of();
            }
            pagination = new PaginatedResponse<>(
                    historiales,
                    totalPages,
                    total,
                    page,
                    size,
                    page < totalPages,
                    page > 1
            );
        }

        model.addAttribute(ModelAttribute.Historial_LIST.getName(), historiales);
        model.addAttribute("pagination", pagination);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("selectedAnimalId", animalId);
        model.addAttribute("q", q);
        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
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
        model.addAttribute("animales", historialMedicoService.fetchAllAnimales());

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
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
        body.put("animalId", animalId);
        body.put("descripcion", descripcion);
        body.put("tratamiento", tratamiento);
        body.put("veterinario", veterinario);
        if (fecha != null && !fecha.isEmpty()) {
            body.put("fecha", fecha);
        } else {
            body.put("fecha", LocalDateTime.now().toString());
        }

        historialMedicoService.crearHistorial(body);
        redirectAttributes.addFlashAttribute("successMessage", messageService.getMessage("toast.success.historial_creado"));
        return "redirect:" + WebRoutes.HISTORIALES_BASE;
    }

    @GetMapping(WebRoutes.HISTORIALES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        response.setHeader("Vary", "HX-Request");
        HistorialMedicoRecord historial = historialMedicoService.fetchHistorialById(id);
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), historial);
        model.addAttribute("animales", historialMedicoService.fetchAllAnimales());

        // Cargar datos del animal para el selector inteligente
        if (historial != null && historial.animalId() != null) {
            try {
                AnimalRecord animalData = historialMedicoService.fetchAnimalById(historial.animalId());
                model.addAttribute("animalData", animalData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
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
        body.put("animalId", animalId);
        body.put("descripcion", descripcion);
        body.put("tratamiento", tratamiento);
        body.put("veterinario", veterinario);
        if (fecha != null && !fecha.isEmpty()) {
            body.put("fecha", fecha);
        } else {
            body.put("fecha", LocalDateTime.now().toString());
        }

        historialMedicoService.editarHistorial(id, body);
        redirectAttributes.addFlashAttribute("successMessage", messageService.getMessage("toast.success.historial_editado"));
        return "redirect:" + WebRoutes.HISTORIALES_BASE + "/" + id + "/detalle";
    }

    @PostMapping(WebRoutes.HISTORIALES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        historialMedicoService.eliminarHistorial(id);
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request")))
            return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.HISTORIALES_BASE).build();
    }

    @GetMapping(WebRoutes.HISTORIALES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<HistorialMedicoRecord> historiales = historialMedicoService.fetchAllHistoriales();
        List<AnimalRecord> animales = historialMedicoService.fetchAllAnimales();
        Map<String, String> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a.nombre());
        }

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("historiales", historiales);
        context.setVariable("animalesMap", animalesMap);
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

    @GetMapping(WebRoutes.HISTORIALES_BASE + "/{id}/pdf")
    public void exportarDetallePDF(@PathVariable Integer id, HttpServletResponse response) throws Exception {
        HistorialMedicoRecord historial = historialMedicoService.fetchHistorialById(id);
        if (historial == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Historial no encontrado");
            return;
        }

        AnimalRecord animal = null;
        if (historial.animalId() != null) {
            animal = historialMedicoService.fetchAnimalById(historial.animalId());
        }

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("historial", historial);
        context.setVariable("animal", animal);
        String html = templateEngine.process(ThymTemplates.Historial_DETALLE_PDF.getPath(), context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=informe-clinico-" + id + ".pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    @GetMapping(WebRoutes.HISTORIALES_EXCEL)
    public void exportarExcel(HttpServletResponse response) throws Exception {
        List<HistorialMedicoRecord> historiales = historialMedicoService.fetchAllHistoriales();
        List<AnimalRecord> animales = historialMedicoService.fetchAllAnimales();

        Map<Integer, String> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(a.id(), a.nombre());
        }

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
                "Historiales Médicos",
                List.of("ID", "ID Animal", "Animal", "Fecha", "Veterinario", "Descripción", "Tratamiento"),
                historiales,
                List.of(
                        HistorialMedicoRecord::id,
                        HistorialMedicoRecord::animalId,
                        h -> h.animalId() != null ? animalesMap.getOrDefault(h.animalId(), "Animal #" + h.animalId())
                                : "",
                        h -> h.fecha() != null ? h.fecha().toString() : "",
                        h -> h.veterinario() != null ? h.veterinario() : "",
                        h -> h.descripcion() != null ? h.descripcion() : "",
                        h -> h.tratamiento() != null ? h.tratamiento() : ""));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=historiales.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @GetMapping(WebRoutes.HISTORIALES_BASE + "/{id}/detalle")
    public String verDetalle(@PathVariable Integer id, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        response.setHeader("Vary", "HX-Request");
        HistorialMedicoRecord historial = historialMedicoService.fetchHistorialById(id);
        model.addAttribute("historial", historial);

        if (historial != null && historial.animalId() != null) {
            try {
                AnimalRecord animal = historialMedicoService.fetchAnimalById(historial.animalId());
                model.addAttribute("animal", animal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return "fragments/content/historial_medico/historial-medico-detalle :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/historial_medico/historial-medico-detalle");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }
}
