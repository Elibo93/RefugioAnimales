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
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.auth.infrastructure.repository.UserRepository;
import org.springframework.security.core.Authentication;
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

import es.refugio.refugio.application.service.adoptante.CreateAdoptanteService;
import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.service.usuario.EditUsuarioService;
import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId; // NUEVO
import es.refugio.auth.domain.Rol;

@Controller
@RequiredArgsConstructor
public class SolicitudAdopcionViewController {

    private final FindSolicitudAdopcionService findSolicitudAdopcionService;
    private final CreateSolicitudAdopcionService createSolicitudAdopcionService;
    private final DeleteSolicitudAdopcionService deleteSolicitudAdopcionService;
    private final EditSolicitudAdopcionService editSolicitudAdopcionService;
    private final FindAnimalService findAnimalService;
    private final FindAdoptanteService findAdoptanteService;
    private final CreateAdoptanteService createAdoptanteService;
    private final EditUsuarioService editUsuarioService;
    private final FindUsuarioService findUsuarioService;
    private final UserRepository userRepository;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.solicitudes_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        try {
            model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), findSolicitudAdopcionService.findAll());
        } catch (Exception e) {
            model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), List.of());
        }
        
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute("currentUri", WebRoutes.solicitudes_BASE);
        model.addAttribute("showBack", false);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.solicitudes_NUEVA)
    public String formulario(Model model, @RequestParam(required = false) Integer animalId, Authentication authentication) {
        SolicitudAdopcion.SolicitudAdopcionBuilder builder = SolicitudAdopcion.builder().fecha(LocalDateTime.now());
        
        if (animalId != null) {
            builder.animalId(new AnimalId(animalId));
        }

        // Si es un usuario logueado, intentamos pre-seleccionar o crear su perfil de adoptante
        if (authentication != null && authentication.isAuthenticated()) {
            userRepository.findByEmail(authentication.getName()).ifPresent(userEntity -> {
                try {
                    // Intentamos buscarlo
                    try {
                        Adoptante adoptante = findAdoptanteService.findByUsuarioId(userEntity.getId());
                        builder.adoptanteId(adoptante.getId());
                    } catch (Exception e) {
                        // Si no tiene perfil social de adoptante, lo creamos automáticamente para que el flujo sea fluido
                        if (userEntity.getRol() == Rol.ROLE_PUBLICO || userEntity.getRol() == Rol.ROLE_ADOPTANTE) {
                            // 1. Crear el perfil básico
                            Adoptante newAdoptante = createAdoptanteService.createAdoptante(
                                new CreateAdoptanteCommand(userEntity.getId(), "PENDIENTE", "PENDIENTE", ""));
                            builder.adoptanteId(newAdoptante.getId());

                            // 2. Si era rol PUBLICO, lo promocionamos a ADOPTANTE
                            if (userEntity.getRol() == Rol.ROLE_PUBLICO) {
                                Usuario usuarioDomain = findUsuarioService.findById(new UsuarioId(userEntity.getId()));
                                editUsuarioService.update(new EditUsuarioCommand(
                                    usuarioDomain.getId(),
                                    usuarioDomain.getNombre(),
                                    usuarioDomain.getApellido(),
                                    usuarioDomain.getEmail(),
                                    usuarioDomain.getTelefono() != null ? usuarioDomain.getTelefono() : "",
                                    Rol.ROLE_ADOPTANTE
                                ));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Fallback silencioso para no romper la vista si falla algo en la conversion
                }
            });
        }

        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), builder.build());
        
        // Manejamos las listas con seguridad (pueden lanzar excepcion si estan vacias)
        try {
            model.addAttribute("animales", findAnimalService.findAll());
        } catch (Exception e) {
            model.addAttribute("animales", List.of());
        }

        try {
            model.addAttribute("adoptantes", findAdoptanteService.findAll());
        } catch (Exception e) {
            model.addAttribute("adoptantes", List.of());
        }

        model.addAttribute("estados", EstadoSolicitud.values());
        model.addAttribute("currentUri", WebRoutes.solicitudes_NUEVA);
        model.addAttribute("showBack", true);
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
