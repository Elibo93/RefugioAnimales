package es.refugio.frontend.web;
import org.springframework.context.i18n.LocaleContextHolder;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import es.refugio.frontend.web.util.ErrorMessageExtractor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import java.util.*;
import java.util.stream.Collectors;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import es.refugio.common.util.ExcelExportHelper;

import es.refugio.frontend.service.AdoptanteService;

/**
 * AdoptanteViewController — gestiona el flujo de conversión de usuario a adoptante.
 * Delega completamente en el backend a través de la API REST.
 */
@Controller
@RequiredArgsConstructor
/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Adoptante.
 *
 * @author Elisabeth
 * @author Diego
 */
public class AdoptanteViewController {

    private static final Logger logger = LoggerFactory.getLogger(AdoptanteViewController.class);

    private final AdoptanteService adoptanteService;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @GetMapping(WebRoutes.ADOPTANTES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model, 
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String q,
                        HttpServletRequest request) {
        
        PaginatedResponse<AdoptanteRecord> pagination = adoptanteService.fetchPaginatedAdoptantes(page, size, q);
        List<AdoptanteRecord> adoptantes = pagination.items();
        logger.info("[ADOPTANTES] page={}, size={}, total={}, items={}", page, size, pagination.total(), adoptantes.size());
        List<UsuarioRecord> usuarios = adoptanteService.fetchAllUsuarios();
        List<PerfilLegalRecord> perfilesLegales = adoptanteService.fetchAllPerfilesLegales();

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        // Solo mostrar adoptantes con rol ROLE_ADOPTANTE o ROLE_VOLUNTARIO_ADOPTANTE
        Set<Integer> adoptanteRoleIds = usuarios.stream()
                .filter(u -> u.rol() != null && (
                        u.rol().equalsIgnoreCase("ROLE_ADOPTANTE") ||
                        u.rol().equalsIgnoreCase("ROLE_VOLUNTARIO_ADOPTANTE")))
                .map(u -> u.id())
                .collect(Collectors.toSet());
        adoptantes = adoptantes.stream()
                .filter(a -> a.usuarioId() != null && adoptanteRoleIds.contains(a.usuarioId()))
                .toList();

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) perfilesMap.put(String.valueOf(p.usuarioId()), p);
        }

        model.addAttribute(ModelAttribute.Adoptante_LIST.getName(), adoptantes);
        model.addAttribute("pagination", pagination);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("perfilesMap", perfilesMap);
        model.addAttribute("query", q);
        model.addAttribute("currentUri", WebRoutes.ADOPTANTES_BASE);

        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Adoptante_LIST.getPath() + " :: list-body";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adoptante_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ADOPTANTES_NUEVO)
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevo(Model model, HttpServletRequest request) {
        Map<String, Object> emptyAdoptante = new HashMap<>();
        emptyAdoptante.put("id", null);
        emptyAdoptante.put("usuarioId", null);
        emptyAdoptante.put("dni", null);
        emptyAdoptante.put("direccion", null);
        emptyAdoptante.put("estadoValidacion", null);
        model.addAttribute(ModelAttribute.SINGLE_Adoptante.getName(), emptyAdoptante);
        model.addAttribute("currentUri", WebRoutes.ADOPTANTES_BASE);
        
        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Adoptante_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adoptante_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ADOPTANTES_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        try {
            AdoptanteRecord adoptante = adoptanteService.fetchAdoptanteById(id);
            model.addAttribute(ModelAttribute.SINGLE_Adoptante.getName(), adoptante);
            
            if (adoptante != null && adoptante.usuarioId() != null) {
                Integer uId = adoptante.usuarioId();
                UsuarioRecord user = adoptanteService.fetchUsuarioById(uId);
                if (user != null) {
                    model.addAttribute("userEmail", user.email());
                }
                
                try {
                    PerfilLegalRecord perfil = adoptanteService.fetchPerfilLegalByUsuarioId(uId);
                    if (perfil != null) {
                        model.addAttribute("nombreCompleto", perfil.nombre() + " " + perfil.apellido());
                        model.addAttribute("userPhone", perfil.telefono());
                        model.addAttribute("userDni", perfil.dni());
                        model.addAttribute("userDireccion", perfil.direccion());
                        model.addAttribute("userFechaNacimiento", perfil.fechaNacimiento());
                    }
                } catch (Exception e) {
                    logger.warn("No se encontró PerfilLegal para usuario " + uId);
                }
            }

            model.addAttribute("currentUri", WebRoutes.ADOPTANTES_EDITAR);
            model.addAttribute("estados", List.of("PENDIENTE", "APROBADO", "RECHAZADO"));
            
            if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                return FragmentoContenido.Adoptante_FORM.getPath() + " :: content";
            }
        } catch (Exception e) {
            logger.error("Error al cargar adoptante para editar: " + e.getMessage());
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adoptante_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ADOPTANTES_NUEVO)
    public String guardarNuevo(
            @RequestParam Integer usuarioId,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam String fechaNacimiento,
            RedirectAttributes redirectAttributes) {
        try {
            adoptanteService.crearAdoptanteYPerfil(usuarioId, nombre, apellido, dni, direccion, telefono, fechaNacimiento);

            redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.adoptante_creado"));
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (Exception e) {
            logger.error("Error al crear adoptante: " + ErrorMessageExtractor.extract(e));
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear adoptante: " + ErrorMessageExtractor.extract(e));
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        }
    }

    @PostMapping(WebRoutes.ADOPTANTES_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    public String guardarEdicion(
            @PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam(required = false) String estadoValidacion,
            RedirectAttributes redirectAttributes) {
        try {
            adoptanteService.editarAdoptanteYPerfil(id, usuarioId, nombre, apellido, dni, direccion, fechaNacimiento, estadoValidacion);

            redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.adoptante_editado"));
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (RestClientResponseException e) {
            logger.error("Error inesperado al actualizar adoptante: " + ErrorMessageExtractor.extract(e));
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el adoptante: " + ErrorMessageExtractor.extract(e));
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar adoptante: " + e.getMessage());
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        }
    }

    @PostMapping(WebRoutes.ADOPTANTES_ELIMINAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Integer id, Model model) {
        try {
            adoptanteService.eliminarAdoptante(id);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @PostMapping(WebRoutes.ADOPTANTES_APROBAR)
    public String aprobar(@PathVariable Integer id) {
        try {
            adoptanteService.aprobarAdoptante(id);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @PostMapping(WebRoutes.ADOPTANTES_RECHAZAR)
    public String rechazar(@PathVariable Integer id) {
        try {
            adoptanteService.rechazarAdoptante(id);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @GetMapping(WebRoutes.ADOPTANTES_PDF)
    public void exportPdf(HttpServletResponse response) throws Exception {
        List<AdoptanteRecord> adoptantes = adoptanteService.fetchAllAdoptantes();
        List<UsuarioRecord> usuarios = adoptanteService.fetchAllUsuarios();
        List<PerfilLegalRecord> perfilesLegales = adoptanteService.fetchAllPerfilesLegales();

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) {
                perfilesMap.put(String.valueOf(p.usuarioId()), p);
            }
        }

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable(ModelAttribute.Adoptante_LIST.getName(), adoptantes);
        context.setVariable("usuariosMap", usuariosMap);
        context.setVariable("perfilesMap", perfilesMap);

        String htmlContent = templateEngine.process(ThymTemplates.Adoptante_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=adoptantes.pdf");

        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
    }

    @GetMapping(WebRoutes.ADOPTANTES_EXCEL)
    public void exportarExcel(HttpServletResponse response) throws Exception {
        List<AdoptanteRecord> adoptantes = adoptanteService.fetchAllAdoptantes();
        List<UsuarioRecord> usuarios = adoptanteService.fetchAllUsuarios();
        List<PerfilLegalRecord> perfilesLegales = adoptanteService.fetchAllPerfilesLegales();

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) {
                perfilesMap.put(String.valueOf(p.usuarioId()), p);
            }
        }

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
            "Adoptantes",
            List.of("ID", "ID Usuario", "Username", "Email", "Nombre", "Apellido", "DNI", "Teléfono", "Dirección", "Fecha Nacimiento", "Estado Validación", "Fecha Registro"),
            adoptantes,
            List.of(
                AdoptanteRecord::id,
                AdoptanteRecord::usuarioId,
                a -> {
                    UsuarioRecord u = usuariosMap.get(String.valueOf(a.usuarioId()));
                    return u != null ? u.username() : "";
                },
                a -> {
                    UsuarioRecord u = usuariosMap.get(String.valueOf(a.usuarioId()));
                    return u != null ? u.email() : "";
                },
                a -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(a.usuarioId()));
                    return p != null ? p.nombre() : "";
                },
                a -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(a.usuarioId()));
                    return p != null ? p.apellido() : "";
                },
                a -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(a.usuarioId()));
                    return p != null ? p.dni() : "-";
                },
                a -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(a.usuarioId()));
                    return p != null ? p.telefono() : "-";
                },
                a -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(a.usuarioId()));
                    return p != null ? p.direccion() : "-";
                },
                a -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(a.usuarioId()));
                    return p != null ? p.fechaNacimiento() : "-";
                },
                AdoptanteRecord::estadoValidacion,
                a -> a.fechaRegistro() != null ? a.fechaRegistro().toString() : "-"
            )
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=adoptantes.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

}
