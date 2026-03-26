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

import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.application.command.tarea.EditTareaCommand;
import es.refugio.refugio.application.service.tarea.CreateTareaService;
import es.refugio.refugio.application.service.tarea.DeleteTareaService;
import es.refugio.refugio.application.service.tarea.EditTareaService;
import es.refugio.refugio.application.service.tarea.FindTareaService;
import es.refugio.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import java.util.Map;
import java.util.stream.Collectors;
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
public class TareaViewController {

    private final FindTareaService findTareaService;
    private final CreateTareaService createTareaService;
    private final DeleteTareaService deleteTareaService;
    private final EditTareaService editTareaService;
    private final FindVoluntarioService findVoluntarioService;
    private final FindUsuarioService findUsuarioService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.tareas_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Tarea_LIST.getName(), findTareaService.findAll());
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.tareas_NUEVA)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), Tarea.builder().fecha(LocalDateTime.now()).build());
        
        List<Voluntario> voluntarios = findVoluntarioService.findAll();
        Map<Integer, Usuario> usuariosMap = voluntarios.stream()
                .map(v -> findUsuarioService.findById(v.getUsuarioId()))
                .collect(Collectors.toMap(u -> u.getId().getValue(), u -> u, (a, b) -> a));
        
        model.addAttribute("voluntarios", voluntarios);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("estados", EstadoTarea.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.tareas_NUEVA)
    public String crear(@RequestParam String descripcion,
                        @RequestParam String estado,
                        @RequestParam(required = false) List<Integer> voluntarioIds,
                        RedirectAttributes redirectAttributes) {

        createTareaService.create(new CreateTareaCommand(descripcion, LocalDateTime.now(), estado, voluntarioIds));
        redirectAttributes.addFlashAttribute("successMessage", "Tarea creada correctamente");
        return "redirect:" + WebRoutes.tareas_BASE;
    }

    @GetMapping(WebRoutes.tareas_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Tarea tarea = findTareaService.findById(new TareaId(id));
        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), tarea);
        
        List<Voluntario> voluntarios = findVoluntarioService.findAll();
        Map<Integer, Usuario> usuariosMap = voluntarios.stream()
                .map(v -> findUsuarioService.findById(v.getUsuarioId()))
                .collect(Collectors.toMap(u -> u.getId().getValue(), u -> u, (a, b) -> a));
        
        model.addAttribute("voluntarios", voluntarios);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("estados", EstadoTarea.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.tareas_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
                                 @RequestParam String descripcion,
                                 @RequestParam String estado,
                                 @RequestParam(required = false) List<Integer> voluntarioIds,
                                 RedirectAttributes redirectAttributes) {

        editTareaService.update(new EditTareaCommand(new TareaId(id), descripcion, LocalDateTime.now(), estado, voluntarioIds));
        redirectAttributes.addFlashAttribute("successMessage", "Tarea editada correctamente");
        return "redirect:" + WebRoutes.tareas_BASE;
    }

    @PostMapping(WebRoutes.tareas_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        deleteTareaService.delete(new TareaId(id));
        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.tareas_BASE).build();
    }

    @GetMapping(WebRoutes.tareas_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Tarea> tareas = findTareaService.findAll();
        Context context = new Context();
        context.setVariable("tareas", tareas);
        String htmlContent = templateEngine.process(ThymTemplates.Tarea_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=tareas.pdf");
        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
    }
}
