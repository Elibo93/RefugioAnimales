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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.application.service.adoptante.FindAdoptanteService;
import es.refugio.refugio.application.service.animal.FindAnimalService;
import es.refugio.refugio.application.service.solicitud_adopcion.CreateSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.DeleteSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.EditSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.FindSolicitudAdopcionService;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.vista.infraestructure.web.enums.FragmentoContenido;
import es.refugio.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.vista.infraestructure.web.enums.ThymTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SolicitudAdopcionViewController {

    private final FindSolicitudAdopcionService findSolicitudAdopcionService;
    private final CreateSolicitudAdopcionService createSolicitudAdopcionService;
    private final DeleteSolicitudAdopcionService deleteSolicitudAdopcionService;
    private final EditSolicitudAdopcionService editSolicitudAdopcionService;
    private final FindAnimalService findAnimalService;
    private final FindAdoptanteService findAdoptanteService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.solicitudes_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), findSolicitudAdopcionService.findAll());
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.solicitudes_NUEVA)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), SolicitudAdopcion.builder().fecha(LocalDateTime.now()).build());
        model.addAttribute("animales", findAnimalService.findAll());
        model.addAttribute("adoptantes", findAdoptanteService.findAll());
        model.addAttribute("estados", EstadoSolicitud.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.solicitudes_NUEVA)
    public String crear(@RequestParam Integer animalId,
                        @RequestParam Integer adoptanteId,
                        @RequestParam String estado,
                        @RequestParam String comentario,
                        RedirectAttributes redirectAttributes) {

        createSolicitudAdopcionService.create(new CreateSolicitudAdopcionCommand(animalId, adoptanteId, LocalDateTime.now(), estado, comentario));
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud de adopción registrada correctamente");
        return "redirect:" + WebRoutes.solicitudes_BASE;
    }

    @GetMapping(WebRoutes.solicitudes_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        SolicitudAdopcion solicitud = findSolicitudAdopcionService.findById(new SolicitudAdopcionId(id));
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales", findAnimalService.findAll());
        model.addAttribute("adoptantes", findAdoptanteService.findAll());
        model.addAttribute("estados", EstadoSolicitud.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.solicitudes_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
                                 @RequestParam Integer animalId,
                                 @RequestParam Integer adoptanteId,
                                 @RequestParam String estado,
                                 @RequestParam String comentario,
                                 RedirectAttributes redirectAttributes) {

        editSolicitudAdopcionService.update(new EditSolicitudAdopcionCommand(new SolicitudAdopcionId(id), animalId, adoptanteId, LocalDateTime.now(), estado, comentario));
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud de adopción editada correctamente");
        return "redirect:" + WebRoutes.solicitudes_BASE;
    }

    @PostMapping(WebRoutes.solicitudes_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        deleteSolicitudAdopcionService.delete(new SolicitudAdopcionId(id));
        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.solicitudes_BASE).build();
    }

    @GetMapping(WebRoutes.solicitudes_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findAll();
        Context context = new Context();
        context.setVariable("solicitudes", solicitudes);
        String htmlContent = templateEngine.process(ThymTemplates.Solicitud_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.pdf");
        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
    }
}
