package es.refugio.vista.infraestructure.web;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.refugio.refugio.application.command.historial_medico.CreateHistorialMedicoCommand;
import es.refugio.refugio.application.command.historial_medico.EditHistorialMedicoCommand;
import es.refugio.refugio.application.service.animal.FindAnimalService;
import es.refugio.refugio.application.service.historial_medico.CreateHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.DeleteHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.EditHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.FindHistorialMedicoService;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.vista.infraestructure.web.enums.FragmentoContenido;
import es.refugio.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.vista.infraestructure.web.enums.ThymTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
public class HistorialMedicoViewController {

    private final FindHistorialMedicoService findHistorialMedicoService;
    private final CreateHistorialMedicoService createHistorialMedicoService;
    private final DeleteHistorialMedicoService deleteHistorialMedicoService;
    private final EditHistorialMedicoService editHistorialMedicoService;
    private final FindAnimalService findAnimalService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.HISTORIALES_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Historial_LIST.getName(), findHistorialMedicoService.findAll());
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.HISTORIALES_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(),
                HistorialMedico.builder().fecha(LocalDateTime.now()).build());
        model.addAttribute("animales", findAnimalService.findAll());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Historial_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.HISTORIALES_NUEVO)
    public String crear(@RequestParam Integer animalId,
            @RequestParam String descripcion,
            @RequestParam String tratamiento,
            @RequestParam String veterinario,
            RedirectAttributes redirectAttributes) {

        createHistorialMedicoService.create(
                new CreateHistorialMedicoCommand(animalId, LocalDateTime.now(), descripcion, tratamiento, veterinario));
        redirectAttributes.addFlashAttribute("successMessage", "Historial médico registrado correctamente");
        return "redirect:" + WebRoutes.HISTORIALES_BASE;
    }

    @GetMapping(WebRoutes.HISTORIALES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        HistorialMedico historial = findHistorialMedicoService.findById(new HistorialMedicoId(id));
        model.addAttribute(ModelAttribute.SINGLE_Historial.getName(), historial);
        model.addAttribute("animales", findAnimalService.findAll());
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

        editHistorialMedicoService.update(new EditHistorialMedicoCommand(new HistorialMedicoId(id), animalId,
                LocalDateTime.now(), descripcion, tratamiento, veterinario));
        redirectAttributes.addFlashAttribute("successMessage", "Historial médico editado correctamente");
        return "redirect:" + WebRoutes.HISTORIALES_BASE;
    }

    @PostMapping(WebRoutes.HISTORIALES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        deleteHistorialMedicoService.delete(new HistorialMedicoId(id));
        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.HISTORIALES_BASE).build();
    }

    @GetMapping(WebRoutes.HISTORIALES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<HistorialMedico> historiales = findHistorialMedicoService.findAll();
        Context context = new Context();
        context.setVariable("historiales", historiales);
        String htmlContent = templateEngine.process(ThymTemplates.Historial_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=historiales.pdf");
        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
    }
}
