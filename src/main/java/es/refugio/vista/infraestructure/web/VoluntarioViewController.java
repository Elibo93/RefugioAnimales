package es.refugio.vista.infraestructure.web;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

import es.refugio.auth.domain.Rol;
import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.application.service.voluntario.CreateVoluntarioService;
import es.refugio.refugio.application.service.voluntario.DeleteVoluntarioService;
import es.refugio.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.refugio.application.service.usuario.CreateUsuarioService;
import es.refugio.refugio.application.service.usuario.EditUsuarioService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.vista.infraestructure.web.enums.FragmentoContenido;
import es.refugio.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.vista.infraestructure.web.enums.ThymTemplates;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VoluntarioViewController {

    private final FindVoluntarioService findVoluntarioService;
    private final CreateVoluntarioService createVoluntarioService;
    private final DeleteVoluntarioService deleteVoluntarioService;
    private final EditVoluntarioService editVoluntarioService;
    private final FindUsuarioService findUsuarioService;
    private final EditUsuarioService editUsuarioService;
    private final CreateUsuarioService createUsuarioService;
    private final AuthenticationManager authenticationManager;

    private final TemplateEngine templateEngine;
    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping(WebRoutes.VOLUNTARIOS_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model) {
        try {
            List<Voluntario> voluntarios = findVoluntarioService.findAll();
            // Mapa usuarioId -> Usuario para lookup en la plantilla
            Map<Integer, Usuario> usuariosMap = voluntarios.stream()
                    .map(v -> findUsuarioService.findById(v.getUsuarioId()))
                    .collect(Collectors.toMap(u -> u.getId().getValue(), u -> u, (a, b) -> a));
            model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), voluntarios);
            model.addAttribute("usuariosMap", usuariosMap);
        } catch (Exception e) {
            model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), List.of());
            model.addAttribute("usuariosMap", Map.of());
        }

        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_BASE);
        model.addAttribute("showBack", false);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), Voluntario.builder().build());
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_NUEVO);
        model.addAttribute("showBack", true);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Voluntario voluntario = findVoluntarioService.findById(new VoluntarioId(id));
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), voluntario);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_EDITAR);
        model.addAttribute("showBack", true);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    public String crearVoluntario(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam String disponibilidad,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String contrasena,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 1. Determinar el Usuario ID (ya sea por autenticación o por registro nuevo)
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            // Usuario NO registrado: Crear registro de Usuario con rol Voluntario
            Usuario newUser = createUsuarioService.createUsuario(
                    new CreateUsuarioCommand(nombre, "Voluntario", email, contrasena, "", Rol.ROLE_VOLUNTARIO));
            idUsuario = newUser.getId().getValue();

            // AUTO-LOGIN PERSISTENTE: Autenticar y guardar en la sesión
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, contrasena);
            Authentication authenticated = authenticationManager.authenticate(token);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authenticated);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        } else if (idUsuario == null) {
            // Usuario YA registrado: Obtener su ID y asegurar que tenga rol de Voluntario
            Usuario usuarioActual = findUsuarioService.findByEmail(auth.getName());
            idUsuario = usuarioActual.getId().getValue();

            if (usuarioActual.getRol() != Rol.ROLE_VOLUNTARIO && usuarioActual.getRol() != Rol.ROLE_ADMIN) {
                editUsuarioService.update(new EditUsuarioCommand(
                        usuarioActual.getId(),
                        usuarioActual.getNombre(),
                        usuarioActual.getApellido(),
                        usuarioActual.getEmail(),
                        usuarioActual.getTelefono(),
                        Rol.ROLE_VOLUNTARIO));
            }
        }

        // 2. Crear el registro de voluntario
        createVoluntarioService.createVoluntario(
                new CreateVoluntarioCommand(new UsuarioId(idUsuario), disponibilidad));

        redirectAttributes.addFlashAttribute("successMessage",
                "¡Bienvenido al equipo voluntario! Ya puedes empezar a ayudar.");

        // Redirigir a la URL guardada por Spring Security si existe
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            return "redirect:" + savedRequest.getRedirectUrl();
        }

        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    public String editarVoluntario(@PathVariable Integer id,
            @RequestParam String disponibilidad,
            @RequestParam String email,
            @RequestParam String telefono,
            RedirectAttributes redirectAttributes) {

        // 1. Buscamos el voluntario que estamos editando
        Voluntario voluntarioExistente = findVoluntarioService.findById(new VoluntarioId(id));

        // 2. Buscamos su Usuario en la base de datos (para no borrarle el nombre sin
        // querer)
        Usuario usuarioExistente = findUsuarioService.findById(voluntarioExistente.getUsuarioId());

        // 3. Actualizamos los datos personales en la tabla Usuario
        editUsuarioService.update(new EditUsuarioCommand(
                usuarioExistente.getId(),
                usuarioExistente.getNombre(), // Mantenemos el que tenía
                usuarioExistente.getApellido(), // Mantenemos el que tenía
                email, // <--- NUEVO EMAIL
                telefono, // <--- NUEVO TELEFONO
                usuarioExistente.getRol() // Mantenemos su rol
        ));

        // 4. Actualizamos sus datos específicos en la tabla Voluntario
        editVoluntarioService.update(
                new EditVoluntarioCommand(new VoluntarioId(id), disponibilidad));

        redirectAttributes.addFlashAttribute("successMessage", "Voluntario editado correctamente");

        return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_ELIMINAR)
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
                .header("Location", WebRoutes.VOLUNTARIOS_BASE)
                .build();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_PDF)
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
