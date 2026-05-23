package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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

@Controller
@RequiredArgsConstructor
public class VoluntarioViewController {

    private static final Logger logger = LoggerFactory.getLogger(VoluntarioViewController.class);

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

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

        if (modoSeleccion && tareaIdSeleccion != null) {
            try {
                TareaRecord tarea = helper.fetchObject(apiUrl + "/v1/tareas/" + tareaIdSeleccion, TareaRecord.class);
                if (tarea != null) {
                    model.addAttribute("tareaNombreSeleccion", tarea.descripcion());

                    List<Integer> assignedIds = new ArrayList<>();
                    List<Integer> vIds = tarea.voluntarioIds();
                    if (vIds != null) {
                        assignedIds.addAll(vIds);
                    }
                    model.addAttribute("assignedVoluntarioIds", assignedIds);
                }
            } catch (Exception e) {
                logger.warn("No se pudo obtener la información de la tarea para el modo selección: " + e.getMessage());
            }
        }

        String url = "/v1/voluntarios";
        boolean firstQuery = true;

        if (q != null && !q.trim().isEmpty()) {
            url += "?q=" + q;
            firstQuery = false;
        }

        if (modoSeleccion && tareaIdSeleccion != null) {
            url += (firstQuery ? "?" : "&") + "excludeTareaId=" + tareaIdSeleccion;
            firstQuery = false;
            
            try {
                TareaRecord tarea = helper.fetchObject(apiUrl + "/v1/tareas/" + tareaIdSeleccion, TareaRecord.class);
                if (tarea != null && tarea.fechaLimite() != null) {
                    url += "&excludeDate=" + tarea.fechaLimite().toLocalDate().toString();
                }
            } catch (Exception e) {
                logger.warn("No se pudo obtener la información de la tarea para el filtrado de fechas: " + e.getMessage());
            }
        }
        
        PaginatedResponse<VoluntarioRecord> pagination = helper.fetchPaginated(apiUrl + url, page, size, VoluntarioRecord.class);
        List<VoluntarioRecord> voluntarios = pagination.items();
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

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
    public String sugerencias(@RequestParam(required = false) String q, Model model) {
        if (q == null || q.trim().isEmpty()) {
            return FragmentoContenido.VOLUNTARIO_SUGERENCIAS.getPath() + " :: suggestions";
        }

        List<VoluntarioRecord> voluntarios = helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class);
        List<PerfilLegalRecord> perfiles = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);

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
    public String formulario(Model model, HttpServletRequest request) {
        Map<String, Object> emptyVoluntario = new HashMap<>();
        emptyVoluntario.put("id", null);
        emptyVoluntario.put("usuarioId", null);
        emptyVoluntario.put("telefono", null);
        emptyVoluntario.put("especialidad", null);
        emptyVoluntario.put("disponibilidad", null);
        model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), emptyVoluntario);
        model.addAttribute("currentUri", WebRoutes.VOLUNTARIOS_NUEVO);
        
        model.addAttribute("perfilLegalExists", false);
        model.addAttribute("perfilExistente", false);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                Object currentUserIdObj = model.getAttribute("currentUserId");
                if (currentUserIdObj instanceof Number) {
                    Integer currentUserId = ((Number) currentUserIdObj).intValue();
                    PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + currentUserId, PerfilLegalRecord.class);
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
                        VoluntarioRecord voluntarioExistente = helper.fetchObject(apiUrl + "/v1/voluntarios/usuario/" + currentUserId, VoluntarioRecord.class);
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
            VoluntarioRecord voluntario = helper.fetchObject(apiUrl + "/v1/voluntarios/" + id, VoluntarioRecord.class);

            if (voluntario == null) {
                logger.warn("No se encontró el voluntario con ID: {}", id);
                return "redirect:/web/home";
            }

            model.addAttribute(ModelAttribute.SINGLE_Voluntario.getName(), voluntario);

            Integer voluntarioUsuarioId = voluntario.usuarioId();

            if (!isAdmin && (voluntarioUsuarioId == null || !currentUser.getId().equals(voluntarioUsuarioId))) {
                logger.warn("Usuario {} intentó editar voluntario {} sin permiso", currentUser.getId(), id);
                return "redirect:/web/home";
            }

            if (voluntarioUsuarioId != null) {
                try {
                    UsuarioRecord user = helper.fetchObject(authUrl + "/v1/usuarios/" + voluntarioUsuarioId, UsuarioRecord.class);
                    if (user != null) {
                        model.addAttribute("userEmail", user.email());
                    }
                } catch (Exception e) {
                    logger.warn("No se pudo obtener datos de usuario {} para el voluntario {}", voluntarioUsuarioId, id);
                }

                try {
                    PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + voluntarioUsuarioId, PerfilLegalRecord.class);
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
                UsuarioRecord me = helper.fetchObject(authUrl + "/v1/me", UsuarioRecord.class);
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
                            VoluntarioRecord existing = helper.fetchObject(apiUrl + "/v1/voluntarios/usuario/" + realUserId, VoluntarioRecord.class);
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
            Map<String, Object> userBody = new HashMap<>();
            userBody.put("email", email);
            userBody.put("contrasena", contrasena);
            userBody.put("rol", "ROLE_VOLUNTARIO");

            try {
                String targetUrl = authUrl + "/v1/usuarios";
                logger.info("Registrando usuario en Auth: " + targetUrl);
                UsuarioRecord createdUser = restTemplate.postForObject(targetUrl, userBody, UsuarioRecord.class);
                if (createdUser != null) {
                    finalUsuarioId = createdUser.id();
                }
            } catch (Exception e) {
                logger.error("Error al registrar usuario para voluntario: " + e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", helper.getMessage("toast.error.crear_cuenta"));
                return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
            }
        }

        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", finalUsuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("direccion", (direccion != null) ? direccion : "");
        bodyPerfil.put("fechaNacimiento", (fechaNacimiento != null) ? fechaNacimiento : "2000-01-01");

        try {
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);
        } catch (Exception e) {
            String errorMsg = ErrorMessageExtractor.extract(e);
            logger.error("Error al sincronizar PerfilLegal en creación: " + errorMsg);
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:" + WebRoutes.VOLUNTARIOS_NUEVO;
        }

        Map<String, Object> bodyVol = new HashMap<>();
        bodyVol.put("usuarioId", finalUsuarioId);
        bodyVol.put("disponibilidad", disponibilidad);
        bodyVol.put("especialidad", especialidad);

        try {
            restTemplate.postForObject(apiUrl + "/v1/voluntarios", bodyVol, Object.class);
            redirectAttributes.addFlashAttribute("successMessage", "toast.success.solicitud_enviada");
        } catch (Exception e) {
            String errorMsg = "Error al crear el perfil: " + ErrorMessageExtractor.extract(e);
            if (e.getCause() != null)
                errorMsg += " (Causa: " + ErrorMessageExtractor.extract((Exception) e.getCause()) + ")";
            logger.error(errorMsg);
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
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

        // SEGURIDAD: Solo admin o el propio voluntario pueden editar
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin && (usuarioId == null || !currentUser.getId().equals(usuarioId))) {
            logger.warn("Usuario {} intentó procesar edición de voluntario {} (usuarioId {}) sin permiso",
                    currentUser.getId(), id, usuarioId);
            return "redirect:/web/home";
        }

        // 1. Actualización de disponibilidad en el servicio de backend
        Map<String, Object> bodyVol = new HashMap<>();
        bodyVol.put("disponibilidad", disponibilidad);
        bodyVol.put("especialidad", especialidad);
        restTemplate.put(apiUrl + "/v1/voluntarios/" + id, bodyVol);

        // 2. Actualización de PerfilLegal
        try {
            Map<String, Object> bodyPerfil = new HashMap<>();
            bodyPerfil.put("usuarioId", usuarioId);
            bodyPerfil.put("nombre", nombre);
            bodyPerfil.put("apellido", apellido);
            bodyPerfil.put("dni", dni);
            bodyPerfil.put("telefono", telefono);
            bodyPerfil.put("direccion", direccion);
            bodyPerfil.put("fechaNacimiento", fechaNacimiento);
            restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);
        } catch (Exception e) {
            logger.error("Error al actualizar PerfilLegal: " + e.getMessage());
        }

        // 3. Actualización de datos de usuario en Auth
        try {
            UsuarioRecord user = helper.fetchObject(authUrl + "/v1/usuarios/" + usuarioId, UsuarioRecord.class);
            if (user != null) {
                Map<String, Object> bodyUser = new HashMap<>();
                bodyUser.put("id", user.id());
                bodyUser.put("username", user.username());
                bodyUser.put("email", email);
                bodyUser.put("rol", user.rol());
                bodyUser.put("contrasena", "secret_placeholder");
                restTemplate.put(authUrl + "/v1/usuarios/" + usuarioId, bodyUser);
            }
        } catch (Exception e) {
            logger.error("Error al actualizar usuario en Auth: " + e.getMessage());
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
            restTemplate.delete(apiUrl + "/v1/voluntarios/" + id);
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
        List<VoluntarioRecord> voluntarios = helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);

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

        Context context = new Context(org.springframework.context.i18n.LocaleContextHolder.getLocale());
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
        List<VoluntarioRecord> voluntarios = helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);

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
            VoluntarioRecord voluntario = helper.fetchObject(apiUrl + "/v1/voluntarios/" + id, VoluntarioRecord.class);
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
            voluntario = helper.fetchObject(apiUrl + "/v1/voluntarios/" + id, VoluntarioRecord.class);
        } catch (Exception ignored) {}
        
        List<DisponibilidadRecord> disponibilidades = Collections.emptyList();
        try {
            disponibilidades = helper.fetchList(apiUrl + "/v1/voluntarios/" + id + "/disponibilidad", DisponibilidadRecord.class);
        } catch (Exception ignored) {}

        String voluntarioNombre = "Voluntario #" + id;
        if (voluntario != null && voluntario.usuarioId() != null) {
            try {
                PerfilLegalRecord p = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + voluntario.usuarioId(), PerfilLegalRecord.class);
                if (p != null) {
                    voluntarioNombre = p.nombre() + " " + p.apellido();
                }
            } catch (Exception ignored) {}
        }

        model.addAttribute("voluntarioId", id);
        model.addAttribute("voluntarioNombre", voluntarioNombre);
        model.addAttribute("disponibilidades", disponibilidades);
        return "fragments/content/voluntario-disponibilidad-modal :: modal";
    }

    @GetMapping("/web/voluntarios/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public String listarPendientes(Model model, HttpServletRequest request) {
        List<VoluntarioRecord> pendientes = helper.fetchList(apiUrl + "/v1/voluntarios/pendientes", VoluntarioRecord.class);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

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
            return "fragments/content/voluntarios-pendientes :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/voluntarios-pendientes");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/web/voluntarios/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> aprobar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/aprobar", null, Void.class);
            
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
            restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/rechazar", null, Void.class);
            
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
