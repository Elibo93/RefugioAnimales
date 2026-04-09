package es.refugio.vista.infraestructure.web;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.refugio.auth.domain.Rol;
import jakarta.servlet.http.HttpServletRequest;
import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.application.service.usuario.CreateUsuarioService;
import es.refugio.refugio.application.service.usuario.DeleteUsuarioService;
import es.refugio.refugio.application.service.usuario.EditUsuarioService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.vista.infraestructure.web.enums.FragmentoContenido;
import es.refugio.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.vista.infraestructure.web.enums.ThymTemplates;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioViewController {

    private final FindUsuarioService findUsuarioService;
    private final CreateUsuarioService createUsuarioService;
    private final DeleteUsuarioService deleteUsuarioService;
    private final EditUsuarioService editUsuarioService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.PERSONAS_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Persona_LIST.getName(), findUsuarioService.findAll());
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_NUEVO)
    public String formulario(Model model) {

        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), new Usuario());

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_NUEVO)
    public String crearPersona(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            Model model) {

        createUsuarioService.createUsuario(
                new CreateUsuarioCommand(nombre, apellido, email, contrasena, telefono, Rol.ROLE_PUBLICO));

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_CREATED.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Usuario persona = findUsuarioService.findById(new UsuarioId(id));
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            RedirectAttributes redirectAttributes) {

        // Obtenemos el usuario existente para mantener su nombre, apellido y rol
        Usuario usuario = findUsuarioService.findById(new UsuarioId(id));

        editUsuarioService.update(
                new EditUsuarioCommand(new UsuarioId(id), nombre, apellido, email, telefono,
                        usuario.getRol()));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Persona editada correctamente");

        return "redirect:" + WebRoutes.PERSONAS_BASE;
    }

    @PostMapping(WebRoutes.PERSONAS_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        deleteUsuarioService.delete(new UsuarioId(id));

        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Persona eliminado correctamente");

        return ResponseEntity.status(302)
                .header("Location", WebRoutes.PERSONAS_BASE)
                .build();
    }

    @GetMapping(WebRoutes.PERSONAS_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Usuario> personas = findUsuarioService.findAll();
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
