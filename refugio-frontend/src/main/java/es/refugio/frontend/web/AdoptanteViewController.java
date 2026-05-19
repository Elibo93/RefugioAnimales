package es.refugio.frontend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientResponseException;
import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
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

/**
 * AdoptanteViewController — gestiona el flujo de conversión de usuario a adoptante.
 * Delega completamente en el backend a través de la API REST.
 */
@Controller
@RequiredArgsConstructor
public class AdoptanteViewController {

    private static final Logger logger = LoggerFactory.getLogger(AdoptanteViewController.class);

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.ADOPTANTES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model, 
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String q,
                        HttpServletRequest request) {
        
        String url = apiUrl + "/v1/adoptantes";
        if (q != null && !q.trim().isEmpty()) {
            url += "?q=" + q;
        }
        
        PaginatedResponse<AdoptanteRecord> pagination = helper.fetchPaginated(url, page, size, AdoptanteRecord.class);
        List<AdoptanteRecord> adoptantes = pagination.items();
        logger.info("[ADOPTANTES] URL: {}, page={}, size={}, total={}, items={}", url, page, size, pagination.total(), adoptantes.size());
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

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

        if ("true".equals(request.getHeader("HX-Request"))) {
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
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Adoptante_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adoptante_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ADOPTANTES_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        try {
            AdoptanteRecord adoptante = helper.fetchObject(apiUrl + "/v1/adoptantes/" + id, AdoptanteRecord.class);
            model.addAttribute(ModelAttribute.SINGLE_Adoptante.getName(), adoptante);
            
            if (adoptante != null && adoptante.usuarioId() != null) {
                Integer uId = adoptante.usuarioId();
                UsuarioRecord user = helper.fetchObject(authUrl + "/v1/usuarios/" + uId, UsuarioRecord.class);
                if (user != null) {
                    model.addAttribute("userEmail", user.email());
                }
                
                try {
                    PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + uId, PerfilLegalRecord.class);
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
            
            if ("true".equals(request.getHeader("HX-Request"))) {
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
            // 1. Crear/Actualizar PerfilLegal (Fuente de verdad para identidad)
            Map<String, Object> bodyPerfil = new HashMap<>();
            bodyPerfil.put("usuarioId", usuarioId);
            bodyPerfil.put("nombre", nombre);
            bodyPerfil.put("apellido", apellido);
            bodyPerfil.put("dni", dni);
            bodyPerfil.put("direccion", direccion);
            bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
            bodyPerfil.put("fechaNacimiento", fechaNacimiento);
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);

            // 2. Crear Perfil de Adoptante (Rol operativo)
            Map<String, Object> bodyAdoptante = new HashMap<>();
            bodyAdoptante.put("usuarioId", usuarioId);
            restTemplate.postForObject(apiUrl + "/v1/adoptantes", bodyAdoptante, Object.class);

            redirectAttributes.addFlashAttribute("successMessage", "Adoptante creado correctamente");
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (Exception e) {
            logger.error("Error al crear adoptante: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear adoptante: " + e.getMessage());
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
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            if (estadoValidacion != null) {
                body.put("estadoValidacion", estadoValidacion);
            }
            restTemplate.put(apiUrl + "/v1/adoptantes/" + id, body);

            // 2. Actualizar PerfilLegal
            Map<String, Object> bodyPerfil = new HashMap<>();
            bodyPerfil.put("usuarioId", usuarioId);
            bodyPerfil.put("nombre", nombre);
            bodyPerfil.put("apellido", apellido);
            bodyPerfil.put("dni", dni);
            bodyPerfil.put("direccion", direccion);
            bodyPerfil.put("fechaNacimiento", fechaNacimiento);
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);

            redirectAttributes.addFlashAttribute("successMessage", "Perfil de adoptante actualizado correctamente");
            return "redirect:" + WebRoutes.ADOPTANTES_BASE;
        } catch (RestClientResponseException e) {
            logger.error("Error del backend al actualizar adoptante: " + e.getResponseBodyAsString());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getResponseBodyAsString());
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
            restTemplate.delete(apiUrl + "/v1/adoptantes/" + id);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @PostMapping(WebRoutes.ADOPTANTES_APROBAR)
    public String aprobar(@PathVariable Integer id) {
        try {
            restTemplate.patchForObject(apiUrl + "/v1/adoptantes/" + id + "/approve", null, Object.class);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @PostMapping(WebRoutes.ADOPTANTES_RECHAZAR)
    public String rechazar(@PathVariable Integer id) {
        try {
            restTemplate.patchForObject(apiUrl + "/v1/adoptantes/" + id + "/reject", null, Object.class);
        } catch (Exception ignored) {}
        return "redirect:" + WebRoutes.ADOPTANTES_BASE;
    }

    @GetMapping(WebRoutes.ADOPTANTES_PDF)
    public void exportPdf(HttpServletResponse response) throws Exception {
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes", AdoptanteRecord.class);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

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

        Context context = new Context(org.springframework.context.i18n.LocaleContextHolder.getLocale());
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
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes", AdoptanteRecord.class);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

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
