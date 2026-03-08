package es.refugio.animales.vista.infraestructure.web;

import java.io.OutputStream;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.refugio.animales.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.animales.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.animales.refugio.application.service.voluntario.CreateVoluntarioService;
import es.refugio.animales.refugio.application.service.voluntario.DeleteVoluntarioService;
import es.refugio.animales.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.animales.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import es.refugio.animales.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.animales.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.animales.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.animales.vista.infraestructure.web.enums.ThymTemplates;
import es.refugio.animales.vista.infraestructure.web.enums.FragmentoContenido;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VoluntarioViewController {

    private final FindVoluntarioService findVoluntarioService;
    private final CreateVoluntarioService createVoluntarioService;
    private final DeleteVoluntarioService deleteVoluntarioService;
    private final EditVoluntarioService editVoluntarioService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.voluntarios_BASE)
    public String listar(Model model) {
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), findVoluntarioService.findAll());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.voluntarios_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), Voluntario.builder().build());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.voluntarios_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Voluntario voluntario = findVoluntarioService.findById(new VoluntarioId(id));
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), voluntario);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.voluntarios_NUEVO)
    public String crearVoluntario(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String especialidad,
            @RequestParam String email,
            @RequestParam String telefono,
            RedirectAttributes redirectAttributes) {

        createVoluntarioService.createVoluntario(
                new CreateVoluntarioCommand(nombre, apellido, especialidad, email, telefono));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Voluntario creado correctamente");

        return "redirect:" + WebRoutes.voluntarios_BASE;
    }

    @PostMapping(WebRoutes.voluntarios_EDITAR)
    public String editarVoluntario(@PathVariable Integer id,
            @RequestParam String especialidad,
            @RequestParam String email,
            @RequestParam String telefono,
            RedirectAttributes redirectAttributes) {
        editVoluntarioService.update(
                new EditVoluntarioCommand(new VoluntarioId(id), especialidad, email, telefono));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Voluntario editado correctamente");

        return "redirect:" + WebRoutes.voluntarios_BASE;
    }

    @PostMapping(WebRoutes.voluntarios_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        try {
            deleteVoluntarioService.delete(new VoluntarioId(id));

            if ("true".equals(request.getHeader("HX-Request"))) {
                return ResponseEntity.ok("");
            }

            redirectAttributes.addFlashAttribute("successMessage", "Voluntario eliminado correctamente");
        } catch (Exception e) {
            if ("true".equals(request.getHeader("HX-Request"))) {
                return ResponseEntity.unprocessableEntity()
                        .body("<div class='toast error'><span>No se puede eliminar: tiene animales asignados.</span></div>");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No se puede eliminar el Voluntario porque tiene animales asignados.");
        }

        return ResponseEntity.status(302)
                .header("Location", WebRoutes.voluntarios_BASE)
                .build();
    }

    @GetMapping(WebRoutes.voluntarios_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Voluntario> voluntarios = findVoluntarioService.findAll();
        Context context = new Context();
        context.setVariable("voluntarios", voluntarios);
        String htmlContent = templateEngine.process(ThymTemplates.Voluntario_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=voluntarios.pdf");
        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }
}
