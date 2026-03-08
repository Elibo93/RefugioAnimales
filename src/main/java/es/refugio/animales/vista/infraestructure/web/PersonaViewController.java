package es.refugio.animales.vista.infraestructure.web;

import java.io.OutputStream;
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

import jakarta.servlet.http.HttpServletRequest;

import es.refugio.animales.refugio.application.command.persona.CreatePersonaCommand;
import es.refugio.animales.refugio.application.command.persona.EditPersonaCommand;
import es.refugio.animales.refugio.application.service.persona.CreatePersonaService;
import es.refugio.animales.refugio.application.service.persona.DeletePersonaService;
import es.refugio.animales.refugio.application.service.persona.EditPersonaService;
import es.refugio.animales.refugio.application.service.persona.FindPersonaService;
import es.refugio.animales.refugio.domain.model.persona.Persona;
import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import es.refugio.animales.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.animales.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.animales.vista.infraestructure.web.enums.ThymTemplates;
import es.refugio.animales.vista.infraestructure.web.enums.FragmentoContenido;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PersonaViewController {

    private final FindPersonaService findPersonaService;
    private final CreatePersonaService createPersonaService;
    private final DeletePersonaService deletePersonaService;
    private final EditPersonaService editPersonaService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.personas_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Persona_LIST.getName(), findPersonaService.findAll());
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.personas_NUEVO)
    public String formulario(Model model) {

        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), new Persona());

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.personas_NUEVO)
    public String crearPersona(@RequestParam String dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            Model model) {

        createPersonaService.createPersona(
                new CreatePersonaCommand(dni, nombre, apellido, email, telefono, direccion, fechaNacimiento));

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_CREATED.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.personas_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Persona persona = findPersonaService.findById(new PersonaId(id));
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.personas_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String direccion,
            RedirectAttributes redirectAttributes) {

        editPersonaService.update(
                new EditPersonaCommand(new PersonaId(id), email, telefono, direccion));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Persona editada correctamente");

        return "redirect:" + WebRoutes.personas_BASE;
    }

    @PostMapping(WebRoutes.personas_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        deletePersonaService.delete(new PersonaId(id));

        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Persona eliminado correctamente");

        return ResponseEntity.status(302)
                .header("Location", WebRoutes.personas_BASE)
                .build();
    }

    @GetMapping(WebRoutes.personas_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Persona> personas = findPersonaService.findAll();
        Context context = new Context();
        context.setVariable("personas", personas);
        String htmlContent = templateEngine.process(ThymTemplates.Persona_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=personas.pdf");
        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }
}
