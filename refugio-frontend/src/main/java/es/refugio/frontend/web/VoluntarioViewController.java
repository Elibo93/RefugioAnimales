package es.refugio.frontend.web;
import org.springframework.context.i18n.LocaleContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.springframework.security.core.context.SecurityContextHolder;
import es.refugio.common.util.ExcelExportHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.security.CustomUserDetails;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import es.refugio.frontend.web.util.ErrorMessageExtractor;

import java.io.OutputStream;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import es.refugio.frontend.service.VoluntarioService;

@Controller
@RequiredArgsConstructor
/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class VoluntarioViewController {

    private static final Logger logger = LoggerFactory.getLogger(VoluntarioViewController.class);

    private final VoluntarioService voluntarioService;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @GetMapping(WebRoutes.VOLUNTARIOS_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "false") boolean modoSeleccion,
            @RequestParam(required = false) Integer tareaIdSeleccion,
            HttpServletRequest request,
            HttpServletResponse response) {

        response.setHeader("Vary", "HX-Request");

        PaginatedResponse<VoluntarioRecord> pagination = voluntarioService.fetchPaginatedVoluntarios(page, size, q);
        List<VoluntarioRecord> voluntarios = new java.util.ArrayList<>(pagination.items());

        if (modoSeleccion && tareaIdSeleccion != null) {
            try {
                TareaRecord tarea = voluntarioService.fetchTareaById(tareaIdSeleccion);
                if (tarea != null) {
                    model.addAttribute("tareaNombreSeleccion", tarea.descripcion());

                    List<Integer> assignedIds = new java.util.ArrayList<>();
                    List<Integer> vIds = tarea.voluntarioIds();
                    if (vIds != null) {
                        assignedIds.addAll(vIds);
                    }
                    model.addAttribute("assignedVoluntarioIds", assignedIds);

                    java.time.LocalDateTime limit = tarea.fechaLimite();
                    boolean isWeekend = false;
                    String dayOfWeekSpanish = "";
                    if (limit != null) {
                        java.time.DayOfWeek dow = limit.getDayOfWeek();
                        isWeekend = (dow == java.time.DayOfWeek.SATURDAY || dow == java.time.DayOfWeek.SUNDAY);
                        switch (dow) {
                            case MONDAY: dayOfWeekSpanish = "LUNES"; break;
                            case TUESDAY: dayOfWeekSpanish = "MARTES"; break;
                            case WEDNESDAY: dayOfWeekSpanish = "MIERCOLES"; break;
                            case THURSDAY: dayOfWeekSpanish = "JUEVES"; break;
                            case FRIDAY: dayOfWeekSpanish = "VIERNES"; break;
                            case SATURDAY: dayOfWeekSpanish = "SABADO"; break;
                            case SUNDAY: dayOfWeekSpanish = "DOMINGO"; break;
                        }
                    }

                    final boolean finalIsWeekend = isWeekend;
                    final String finalDayOfWeekSpanish = dayOfWeekSpanish;

                    voluntarios.removeIf(v -> {
                        if (assignedIds.contains(v.id())) return true;
                        
                        if (limit != null && v.disponibilidad() != null) {
                            String disp = v.disponibilidad().toUpperCase();
                            if (disp.contains("FINES DE SEMANA") && !finalIsWeekend) return true;
                            if ((disp.equals("LUNES") || disp.equals("MARTES") || disp.equals("MIERCOLES") || disp.equals("MIÉRCOLES") || 
                                 disp.equals("JUEVES") || disp.equals("VIERNES") || disp.equals("SABADO") || disp.equals("SÁBADO") || disp.equals("DOMINGO"))
                                 && !disp.replace("Á", "A").replace("É", "E").equals(finalDayOfWeekSpanish)) {
                                return true;
                            }
                        }
                        return false;
                    });
                }
            } catch (Exception e) {
                logger.warn("No se pudo obtener la información de la tarea para el modo selección: " + e.getMessage());
            }
        }
        List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();
        List<PerfilLegalRecord> perfilesLegales = voluntarioService.fetchAllPerfilesLegales();

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) perfilesMap.put(String.valueOf(p.usuarioId()), p);
        }

        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), voluntarios);
        model.addAttribute("pagination", pagination);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("perfilesMap", perfilesMap);
        model.addAttribute("query", q);
        model.addAttribute("modoSeleccion", modoSeleccion);
        model.addAttribute("tareaIdSeleccion", tareaIdSeleccion);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_BASE);

        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Voluntario_LIST.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/web/voluntarios/sugerencias")
    public String sugerencias(@RequestParam(required = false) String q,
                              @RequestParam(required = false) String fechaLimite,
                              @RequestParam(required = false) List<Integer> voluntarioIds,
                              Model model) {
        if (q == null || q.trim().isEmpty()) {
            return FragmentoContenido.VOLUNTARIO_SUGERENCIAS.getPath() + " :: suggestions";
        }

        List<VoluntarioRecord> voluntarios = voluntarioService.fetchAllVoluntarios();
        List<PerfilLegalRecord> perfiles = voluntarioService.fetchAllPerfilesLegales();
        List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();

        // Extraer el día de la semana de fechaLimite si se proporciona
        String diaSemanaRequerido = null;
        if (fechaLimite != null && !fechaLimite.trim().isEmpty()) {
            try {
                java.time.LocalDateTime fecha = java.time.LocalDateTime.parse(fechaLimite);
                java.time.DayOfWeek day = fecha.getDayOfWeek();
                switch (day) {
                    case MONDAY: case TUESDAY: case WEDNESDAY: case THURSDAY: case FRIDAY:
                        diaSemanaRequerido = "ENTRE_SEMANA";
                        break;
                    case SATURDAY: case SUNDAY:
                        diaSemanaRequerido = "FINES_DE_SEMANA";
                        break;
                }
            } catch (Exception e) {
                // Ignorar si hay un error de parseo de fecha
            }
        }

        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfiles) {
            if (p.usuarioId() != null)
                perfilesMap.put(p.usuarioId(), p);
        }

        Map<Integer, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(u.id(), u);
        }

        String query = q.toLowerCase();
        List<Map<String, Object>> voluntariosEncontrados = new ArrayList<>();

        for (VoluntarioRecord v : voluntarios) {
            // Filtrar voluntarios no aprobados o que ya están asignados
            if (!"APROBADO".equals(v.estado())) continue;
            if (voluntarioIds != null && voluntarioIds.contains(v.id())) continue;

            // Filtrar por disponibilidad si hay un requerimiento
            if (diaSemanaRequerido != null && v.disponibilidad() != null) {
                String disp = v.disponibilidad().toUpperCase();
                if (!disp.contains(diaSemanaRequerido) && !disp.contains("FLEXIBLE") && !disp.contains("CUALQUIERA")) {
                    // Si requiere fin de semana pero solo tiene mañanas/tardes entre semana, lo saltamos
                    // Asumimos que FLEXIBLE o CUALQUIERA u opciones que contengan el dia requerido son válidas
                    if (diaSemanaRequerido.equals("FINES_DE_SEMANA") && (disp.contains("MAÑANAS") || disp.contains("TARDES"))) {
                        // En realidad "MAÑANAS" y "TARDES" puede aplicar a toda la semana, dependiendo de la BD. 
                        // Pero para ser estrictos, si el usuario explícitamente marcó FINES_DE_SEMANA, o ENTRE_SEMANA
                        // Y esta disponibilidad es un enum: MAÑANAS, TARDES, FINES_DE_SEMANA, FLEXIBLE.
                        // Si la disponibilidad de V no es compatible, lo saltamos.
                        if (!disp.equals("MAÑANAS") && !disp.equals("TARDES") && !disp.equals("FLEXIBLE")) {
                            continue;
                        }
                    } else if (diaSemanaRequerido.equals("ENTRE_SEMANA") && disp.equals("FINES_DE_SEMANA")) {
                        continue; // No está disponible entre semana
                    }
                }
            }

            if (v.usuarioId() != null) {
                int uId = v.usuarioId();
                PerfilLegalRecord perfil = perfilesMap.get(uId);
                UsuarioRecord user = usuariosMap.get(uId);

                String nombre = perfil != null && perfil.nombre() != null ? perfil.nombre() : "";
                String apellido = perfil != null && perfil.apellido() != null ? perfil.apellido() : "";
                String email = user != null && user.email() != null ? user.email() : "";
                String username = user != null && user.username() != null ? user.username() : "";

                if (nombre.toLowerCase().contains(query) || apellido.toLowerCase().contains(query) ||
                        email.toLowerCase().contains(query) || username.toLowerCase().contains(query)) {

                    Map<String, Object> suggestion = new HashMap<>();
                    suggestion.put("id", v.id());
                    suggestion.put("nombre", nombre);
                    suggestion.put("apellido", apellido);
                    suggestion.put("email", email);
                    suggestion.put("username", username);
                    voluntariosEncontrados.add(suggestion);
                }
            }
        }

        model.addAttribute("voluntariosEncontrados", voluntariosEncontrados);
        return FragmentoContenido.VOLUNTARIO_SUGERENCIAS.getPath() + " :: suggestions";
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLICO', 'VOLUNTARIO', 'ADOPTANTE') or isAnonymous()")
    public String formulario(@RequestParam(required = false) Integer usuarioId, Model model, HttpServletRequest request) {
        Map<String, Object> emptyVoluntario = new HashMap<>();
        emptyVoluntario.put("id", null);
        emptyVoluntario.put("usuarioId", usuarioId);
        emptyVoluntario.put("telefono", null);
        emptyVoluntario.put("especialidad", null);
        emptyVoluntario.put("disponibilidad", null);
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), emptyVoluntario);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_NUEVO);
        
        model.addAttribute("perfilLegalExists", false);
        model.addAttribute("perfilExistente", false);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                Integer targetUserId = null;
                
                if (isAdmin) {
                    // Si es admin, debe venir el usuarioId explícitamente
                    targetUserId = usuarioId;
                } else {
                    // Si es usuario normal, se preselecciona a sí mismo
                    Object currentUserIdObj = model.getAttribute("currentUserId");
                    if (currentUserIdObj instanceof Number) {
                        targetUserId = ((Number) currentUserIdObj).intValue();
                    }
                }
                
                model.addAttribute("targetUserId", targetUserId);
                
                if (targetUserId != null) {
                    try {
                        UsuarioRecord targetUser = voluntarioService.fetchUsuarioById(targetUserId);
                        if (targetUser != null) {
                            model.addAttribute("targetUserEmail", targetUser.email());
                            model.addAttribute("targetUserUsername", targetUser.username());
                        }
                    } catch (Exception e) {
                        logger.info("No se pudo obtener el usuario objetivo: " + e.getMessage());
                    }
                    
                    PerfilLegalRecord perfil = voluntarioService.fetchPerfilLegalByUsuarioId(targetUserId);
                    if (perfil != null) {
                        model.addAttribute("userPhone",           perfil.telefono());
                        model.addAttribute("userDni",             perfil.dni());
                        model.addAttribute("userDireccion",       perfil.direccion());
                        model.addAttribute("userFechaNacimiento", perfil.fechaNacimiento());
                        model.addAttribute("userNombre",          perfil.nombre());
                        model.addAttribute("userApellido",        perfil.apellido());
                        model.addAttribute("nombreCompleto",      perfil.nombre() + " " + perfil.apellido());
                        model.addAttribute("perfilLegalExists",   true);
                        model.addAttribute("perfilExistente",     true);
                    } else {
                        model.addAttribute("perfilLegalExists", false);
                        model.addAttribute("perfilExistente", false);
                    }

                    try {
                        VoluntarioRecord voluntarioExistente = voluntarioService.fetchVoluntarioByUsuarioId(targetUserId);
                        if (voluntarioExistente != null) {
                            model.addAttribute("voluntarioExistente", voluntarioExistente);
                        }
                    } catch (Exception e) {
                        logger.debug("El usuario no tiene registro de voluntario previo.");
                    }
                }
            } catch (Exception e) {
                logger.info("El usuario no tiene perfil legal aún: " + e.getMessage());
            }
        }

        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Voluntario_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        logger.info("DEBUG: Entrando en editarFormulario para ID: {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
        model.addAttribute("perfilLegalExists", false);
        model.addAttribute("perfilExistente", false);

        try {
            VoluntarioRecord voluntario = voluntarioService.fetchVoluntarioById(id);
            model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), voluntario);
            
            if (voluntario == null) {
                logger.warn("No se encontró el voluntario con ID: {}", id);
                return "redirect:/web/home";
            }

            Integer voluntarioUsuarioId = voluntario.usuarioId();

            if (!isAdmin && (voluntarioUsuarioId == null || !currentUser.getId().equals(voluntarioUsuarioId))) {
                logger.warn("Usuario {} intentó editar voluntario {} sin permiso", currentUser.getId(), id);
                return "redirect:/web/home";
            }

            if (voluntarioUsuarioId != null) {
                try {
                    UsuarioRecord user = voluntarioService.fetchUsuarioById(voluntarioUsuarioId);
                    if (user != null) {
                        model.addAttribute("userEmail", user.email());
                    }
                } catch (Exception e) {
                    logger.warn("No se pudo obtener datos de usuario {} para el voluntario {}", voluntarioUsuarioId, id);
                }

                try {
                    PerfilLegalRecord perfil = voluntarioService.fetchPerfilLegalByUsuarioId(voluntarioUsuarioId);
                    if (perfil != null) {
                        model.addAttribute("nombreCompleto", perfil.nombre() + " " + perfil.apellido());
                        model.addAttribute("userPhone",         perfil.telefono());
                        model.addAttribute("userDni",           perfil.dni());
                        model.addAttribute("userDireccion",     perfil.direccion());
                        model.addAttribute("userFechaNacimiento", perfil.fechaNacimiento());
                        model.addAttribute("userNombre",        perfil.nombre());
                        model.addAttribute("userApellido",      perfil.apellido());
                        model.addAttribute("perfilLegalExists", true);
                        model.addAttribute("perfilExistente", true);
                    } else {
                        model.addAttribute("perfilLegalExists", false);
                        model.addAttribute("perfilExistente", false);
                    }
                } catch (Exception e) {
                    logger.warn("No se encontró PerfilLegal para usuario: {}", voluntarioUsuarioId);
                    model.addAttribute("perfilLegalExists", false);
                    model.addAttribute("perfilExistente", false);
                }
            }
        } catch (Exception e) {
            logger.error("Error al cargar voluntario/usuario para editar: " + e.getMessage());
            return "redirect:/web/home";
        }

        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_EDITAR);

        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Voluntario_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Voluntario_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_NUEVO)
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLICO', 'VOLUNTARIO', 'ADOPTANTE') or isAnonymous()")
    public String crearVoluntario(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam String disponibilidad,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String contrasena,
            @RequestParam(required = false) String especialidad,
            RedirectAttributes redirectAttributes) {

        Integer finalUsuarioId = idUsuario;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());

        if (isAuthenticated) {
            try {
                UsuarioRecord me = voluntarioService.fetchMe();
                if (me != null) {
                    Integer realUserId = me.id();
                    String rol = me.rol();
                    boolean isAdmin = rol != null && rol.contains("ADMIN");

                    if (!isAdmin) {
                        if (finalUsuarioId != null && !finalUsuarioId.equals(realUserId)) {
                            logger.warn("Usuario {} intentó suplantar al ID {} en registro voluntario", realUserId, finalUsuarioId);
                        }
                        finalUsuarioId = realUserId;

                        try {
                            VoluntarioRecord existing = voluntarioService.fetchVoluntarioByUsuarioId(realUserId);
                            if (existing != null) {
                                logger.info("Bloqueada solicitud duplicada para usuario {}", realUserId);
                                redirectAttributes.addFlashAttribute("errorMessage", helper.getMessage("toast.error.voluntario_registrado"));
                                return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
                            }
                        } catch (Exception e) {
                            // Normal, no hay registro previo
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error al verificar identidad en creación de voluntario: " + e.getMessage());
            }
        }

        if (finalUsuarioId == null) {
            try {
                UsuarioRecord createdUser = voluntarioService.crearUsuario(email, contrasena, "ROLE_VOLUNTARIO");
                if (createdUser != null) {
                    finalUsuarioId = createdUser.id();
                }
            } catch (Exception e) {
                logger.error("Error al registrar usuario para voluntario: " + e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", helper.getMessage("toast.error.crear_cuenta"));
                return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
            }
        }

        try {
            voluntarioService.crearVoluntarioYPerfil(finalUsuarioId, nombre, apellido, dni, direccion, telefono, fechaNacimiento, especialidad, disponibilidad);
            if (isAuthenticated && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.voluntario_creado_admin"));
            } else {
                redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("voluntario.estado.pendiente.msg"));
            }
        } catch (Exception e) {
            String errorMsg = "Error al crear el perfil: " + ErrorMessageExtractor.extract(e);
            logger.error(errorMsg);
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
        }
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_EDITAR)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public String editarVoluntario(@PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String disponibilidad,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String especialidad,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin && (usuarioId == null || !currentUser.getId().equals(usuarioId))) {
            logger.warn("Usuario {} intentó procesar edición de voluntario {} (usuarioId {}) sin permiso",
                    currentUser.getId(), id, usuarioId);
            return "redirect:/web/home";
        }

        try {
            voluntarioService.editarVoluntarioYPerfil(id, usuarioId, nombre, apellido, email, dni, direccion, telefono, fechaNacimiento, especialidad, disponibilidad);
        } catch (Exception e) {
            logger.error("Error al actualizar voluntario: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.voluntario_actualizado"));

        if (isAdmin) {
            return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
        } else {
            return "redirect:/web/personas/" + usuarioId;
        }
    }

    @PostMapping(WebRoutes.VOLUNTARIOS_ELIMINAR)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            voluntarioService.eliminarVoluntario(id);
            if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request")))
                return ResponseEntity.ok("");
        } catch (Exception e) {
            if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                return ResponseEntity.unprocessableEntity()
                        .body("<div class='toast error'><span>No se puede eliminar: tiene animales asignados.</span></div>");
            }
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.VOLUNTARIOS_BASE).build();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_PDF)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<VoluntarioRecord> voluntarios = voluntarioService.fetchAllVoluntarios();
        List<PerfilLegalRecord> perfilesLegales = voluntarioService.fetchAllPerfilesLegales();
        List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) {
                perfilesMap.put(String.valueOf(p.usuarioId()), p);
            }
        }

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("voluntarios", voluntarios);
        context.setVariable("perfilesMap", perfilesMap);
        context.setVariable("usuariosMap", usuariosMap);
        String html = templateEngine.process(ThymTemplates.Voluntario_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=voluntarios.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_EXCEL)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        List<VoluntarioRecord> voluntarios = voluntarioService.fetchAllVoluntarios();
        List<PerfilLegalRecord> perfilesLegales = voluntarioService.fetchAllPerfilesLegales();
        List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) {
                perfilesMap.put(String.valueOf(p.usuarioId()), p);
            }
        }

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
            "Voluntarios",
            List.of("ID", "ID Usuario", "Username", "Email", "Nombre", "Apellido", "DNI", "Teléfono", "Dirección", "Fecha Nacimiento", "Especialidad", "Disponibilidad", "Estado"),
            voluntarios,
            List.of(
                VoluntarioRecord::id,
                VoluntarioRecord::usuarioId,
                v -> {
                    UsuarioRecord u = usuariosMap.get(String.valueOf(v.usuarioId()));
                    return u != null ? u.username() : "";
                },
                v -> {
                    UsuarioRecord u = usuariosMap.get(String.valueOf(v.usuarioId()));
                    return u != null ? u.email() : "";
                },
                v -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(v.usuarioId()));
                    return p != null ? p.nombre() : "";
                },
                v -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(v.usuarioId()));
                    return p != null ? p.apellido() : "";
                },
                v -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(v.usuarioId()));
                    return p != null ? p.dni() : "-";
                },
                v -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(v.usuarioId()));
                    return p != null ? p.telefono() : "-";
                },
                v -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(v.usuarioId()));
                    return p != null ? p.direccion() : "-";
                },
                v -> {
                    PerfilLegalRecord p = perfilesMap.get(String.valueOf(v.usuarioId()));
                    return p != null ? p.fechaNacimiento() : "-";
                },
                v -> v.especialidad() != null ? v.especialidad() : "-",
                v -> v.disponibilidad() != null ? v.disponibilidad() : "-",
                v -> v.estado() != null ? v.estado() : "-"
            )
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=voluntarios.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_DETALLE)
    @PreAuthorize("hasRole('ADMIN')")
    public String verDetalle(@PathVariable Integer id, Model model) {
        try {
            VoluntarioRecord voluntario = voluntarioService.fetchVoluntarioById(id);
            if (voluntario != null && voluntario.usuarioId() != null) {
                return "redirect:/web/personas/" + voluntario.usuarioId();
            }
        } catch (Exception ignored) {
        }

        return "redirect:" + WebRoutes.VOLUNTARIOS_BASE;
    }

    @GetMapping(WebRoutes.VOLUNTARIOS_BASE + "/{id}/modal-disponibilidad")
    @PreAuthorize("hasRole('ADMIN')")
    public String modalDisponibilidad(@PathVariable Integer id, Model model) {
        VoluntarioRecord voluntario = null;
        try {
            voluntario = voluntarioService.fetchVoluntarioById(id);
        } catch (Exception ignored) {}
        
        List<Map<String, Object>> disponibilidades = Collections.emptyList();
        try {
            disponibilidades = voluntarioService.fetchDisponibilidad(id);
        } catch (Exception ignored) {}

        String voluntarioNombre = "Voluntario #" + id;
        if (voluntario != null && voluntario.usuarioId() != null) {
            try {
                PerfilLegalRecord p = voluntarioService.fetchPerfilLegalByUsuarioId(voluntario.usuarioId());
                if (p != null) {
                    voluntarioNombre = p.nombre() + " " + p.apellido();
                }
            } catch (Exception ignored) {}
        }

        model.addAttribute("voluntarioId", id);
        model.addAttribute("voluntarioNombre", voluntarioNombre);
        model.addAttribute("disponibilidades", disponibilidades);
        return "fragments/content/voluntarios/voluntario-disponibilidad-modal :: modal";
    }

    @GetMapping("/web/voluntarios/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPendientes(Model model, HttpServletRequest request) {
        List<VoluntarioRecord> pendientes = voluntarioService.fetchVoluntariosPendientes();
        List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();
        List<PerfilLegalRecord> perfilesLegales = voluntarioService.fetchAllPerfilesLegales();

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) perfilesMap.put(String.valueOf(p.usuarioId()), p);
        }

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("perfilesMap", perfilesMap);
        model.addAttribute("currentUri", "/web/voluntarios/pendientes");

        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return "fragments/content/voluntarios/voluntarios-pendientes :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/voluntarios/voluntarios-pendientes");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/web/voluntarios/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> aprobar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            voluntarioService.aprobarSolicitudVoluntario(id);
            
            if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                return ResponseEntity.ok()
                    .header("HX-Trigger", "{\"showToast\": {\"message\": \"¡Solicitud aprobada! El nuevo voluntario ya está activo en el equipo.\", \"type\": \"success\"}, \"volunteerStatusChanged\": {}}")
                    .body("");
            }
        } catch (Exception e) {
            logger.error("Error al aprobar voluntario {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).body("Error al procesar la aprobación");
        }
        return ResponseEntity.status(302).header("Location", "/web/voluntarios/pendientes").build();
    }

    @PostMapping("/web/voluntarios/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> rechazar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            voluntarioService.rechazarSolicitudVoluntario(id);
            
            if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                return ResponseEntity.ok()
                    .header("HX-Trigger", "{\"showToast\": {\"message\": \"Solicitud rechazada. Se ha actualizado el estado del candidato correctamente.\", \"type\": \"warning\"}, \"volunteerStatusChanged\": {}}")
                    .body("");
            }
        } catch (Exception e) {
            logger.error("Error al rechazar voluntario {}: {}", id, e.getMessage());
        }
        return ResponseEntity.status(302).header("Location", "/web/voluntarios/pendientes").build();
    }
}
