package es.refugio.frontend.web;
import org.springframework.context.i18n.LocaleContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import es.refugio.common.util.ExcelExportHelper;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import es.refugio.frontend.service.TareaService;
import es.refugio.frontend.service.VoluntarioService;

import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'VOLUNTARIO_ADOPTANTE')")
/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Tarea.
 *
 * @author Elisabeth
 * @author Diego
 */
public class TareaViewController {

    private final TareaService tareaService;
    private final VoluntarioService voluntarioService;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @GetMapping(WebRoutes.TAREAS_BASE)
    public String listar(Model model,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer voluntarioIdFiltro,
            @RequestParam(required = false, defaultValue = "false") boolean modoSeleccion,
            @RequestParam(required = false) Integer voluntarioIdSeleccion,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String successMessage,
            HttpServletRequest request,
            HttpServletResponse response) {

        response.setHeader("Vary", "HX-Request");

        if (modoSeleccion && voluntarioIdSeleccion != null) {
            try {
                VoluntarioRecord v = voluntarioService.fetchVoluntarioById(voluntarioIdSeleccion);
                if (v != null && v.usuarioId() != null) {
                    PerfilLegalRecord p = voluntarioService.fetchPerfilLegalByUsuarioId(v.usuarioId());
                    if (p != null) {
                        model.addAttribute("voluntarioNombreSeleccion", p.nombre() + " " + p.apellido());
                    }
                }
            } catch (Exception ignored) {
            }
        }

        Object isAdminObj = model.getAttribute("isAdmin");
        boolean isAdmin = isAdminObj != null && (Boolean) isAdminObj;
        Object currentUserId = model.getAttribute("currentUserId");

        Integer myVoluntarioId = null;
        if (!isAdmin && currentUserId != null) {
            try {
                VoluntarioRecord vObj = voluntarioService.fetchVoluntarioByUsuarioId((Integer) currentUserId);
                if (vObj != null) {
                    myVoluntarioId = vObj.id();
                }
            } catch (Exception ignored) {
            }
        }

        List<DisponibilidadRecord> disponibilidades = Collections.emptyList();
        if (modoSeleccion && voluntarioIdSeleccion != null) {
            try {
                List<Map<String, Object>> listDisponibilidad = voluntarioService
                        .fetchDisponibilidad(voluntarioIdSeleccion);
                if (listDisponibilidad != null) {
                    List<DisponibilidadRecord> mapped = new ArrayList<>();
                    for (Map<String, Object> map : listDisponibilidad) {
                        try {
                            String f = map.get("fecha") != null ? map.get("fecha").toString() : null;
                            LocalDate date = f != null ? LocalDate.parse(f) : null;
                            String st = map.get("estado") != null ? map.get("estado").toString() : null;
                            mapped.add(new DisponibilidadRecord(null, date, null, st));
                        } catch (Exception ignored) {
                        }
                    }
                    disponibilidades = mapped;
                }
            } catch (Exception ignored) {
            }
        }

        List<TareaRecord> todasTareasFiltradas = fetchFiltered(prioridad, estado, myVoluntarioId, voluntarioIdFiltro, voluntarioIdSeleccion,
                disponibilidades);
        PaginatedResponse<TareaRecord> pagination = paginateList(todasTareasFiltradas, page, size);
        List<TareaRecord> tareas = pagination.items();
        model.addAttribute("todasTareasFiltradas", todasTareasFiltradas);

        List<VoluntarioRecord> voluntarios = voluntarioService.fetchAllVoluntarios();

        Set<Integer> assignedTaskIds = new HashSet<>();
        if (modoSeleccion && voluntarioIdSeleccion != null) {
            for (TareaRecord t : tareas) {
                if (t.voluntarioIds() != null && t.voluntarioIds().contains(voluntarioIdSeleccion)) {
                    if (t.id() != null) {
                        assignedTaskIds.add(t.id());
                    }
                }
            }
        }
        model.addAttribute("alreadyAssignedTaskIds", assignedTaskIds);

        boolean hasFilters = (prioridad != null && !"ALL".equals(prioridad))
                || (estado != null && !"ALL".equals(estado));

        model.addAttribute("selectedPrioridad", prioridad != null ? prioridad : "ALL");
        model.addAttribute("selectedEstado", estado != null ? estado : "ALL");
        model.addAttribute("hasFilters", hasFilters);

        Map<String, String> voluntarioUsuarioIds = new HashMap<>();
        for (VoluntarioRecord v : voluntarios) {
            if (v.id() != null && v.usuarioId() != null) {
                voluntarioUsuarioIds.put(v.id().toString(), v.usuarioId().toString());
            }
        }

        Map<String, String> voluntarioNombres = fetchVoluntarioNombres();
        model.addAttribute(ModelAttribute.Tarea_LIST.getName(), tareas);
        model.addAttribute("pagination", pagination);
        model.addAttribute("voluntarioNombres", voluntarioNombres);
        model.addAttribute("voluntarioUsuarioIds", voluntarioUsuarioIds);
        model.addAttribute("modoSeleccion", modoSeleccion);
        model.addAttribute("voluntarioIdSeleccion", voluntarioIdSeleccion);
        model.addAttribute("voluntarioIdFiltro", voluntarioIdFiltro);
        model.addAttribute("myVoluntarioId", myVoluntarioId);

        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Tarea_LIST.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.TAREAS_NUEVA)
    public String formulario(Model model, @RequestParam(required = false) Integer voluntarioId,
            HttpServletRequest request) {
        Map<String, Object> tarea = new HashMap<>();
        tarea.put("id", null);
        tarea.put("descripcion", null);
        tarea.put("estado", "PENDIENTE");
        tarea.put("fechaLimite", null);
        tarea.put("instrucciones", null);
        tarea.put("voluntarioIds", List.of());
        tarea.put("fecha", LocalDateTime.now().toString());

        if (voluntarioId != null) {
            tarea.put("voluntarioIds", List.of(voluntarioId));
            try {
                VoluntarioRecord v = voluntarioService.fetchVoluntarioById(voluntarioId);
                if (v != null && v.usuarioId() != null) {
                    UsuarioRecord u = voluntarioService.fetchUsuarioById(v.usuarioId());
                    PerfilLegalRecord p = null;
                    try {
                        p = voluntarioService.fetchPerfilLegalByUsuarioId(v.usuarioId());
                    } catch (Exception ignored) {
                    }

                    if (u != null) {
                        Map<String, Object> displayVol = new HashMap<>();
                        displayVol.put("id", voluntarioId);
                        displayVol.put("email", u.email());
                        displayVol.put("username", u.username());
                        if (p != null) {
                            displayVol.put("nombre", p.nombre());
                            displayVol.put("apellido", p.apellido());
                        } else {
                            displayVol.put("nombre", u.username());
                            displayVol.put("apellido", "");
                        }
                        model.addAttribute("voluntarioPreseleccionado", displayVol);
                    }
                }
            } catch (Exception ignored) {
            }
            model.addAttribute("returnUrl", WebRoutes.VOLUNTARIOS_BASE);
        } else {
            model.addAttribute("returnUrl", WebRoutes.TAREAS_BASE);
        }

        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), tarea);
        model.addAttribute("estados", List.of("PENDIENTE", "PROPUESTA", "ACEPTADA", "EN_PROCESO", "COMPLETADA",
                "FINALIZADA", "RECHAZADA", "CANCELADA"));
        model.addAttribute("voluntarioNombres", fetchVoluntarioNombres());

        List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();
        Map<Integer, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(u.id(), u);
        }
        model.addAttribute("usuariosMap", usuariosMap);

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Tarea_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.TAREAS_NUEVA)
    public String crear(@RequestParam String descripcion,
            @RequestParam String estado,
            @RequestParam(required = false) String fechaLimite,
            @RequestParam(required = false) String instrucciones,
            @RequestParam(required = false) List<Integer> voluntarioIds,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("descripcion", descripcion);
        body.put("estado", estado);
        body.put("fecha", LocalDateTime.now().toString());
        body.put("fechaLimite", fechaLimite != null && !fechaLimite.isEmpty() ? fechaLimite : null);
        body.put("instrucciones", instrucciones);
        body.put("voluntarioIds", voluntarioIds != null ? voluntarioIds : List.of());
        body.put("voluntarioActorId", model.getAttribute("currentUserId"));

        try {
            tareaService.crearTarea(body);
            redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.tarea_creada"));
            return "redirect:" + WebRoutes.TAREAS_BASE;
        } catch (org.springframework.web.client.RestClientException e) {
            String msg = e.getMessage();
            if (e instanceof org.springframework.web.client.HttpStatusCodeException httpException) {
                String responseBody = httpException.getResponseBodyAsString();
                try {
                    JsonNode root = new ObjectMapper().readTree(responseBody);
                    if (root.has("message")) {
                        msg = root.get("message").asText();
                    } else {
                        msg = responseBody;
                    }
                } catch (Exception parseEx) {
                    msg = responseBody;
                }
            }
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:" + WebRoutes.TAREAS_NUEVA;
        }
    }

    @GetMapping("/web/tareas/{id}/imprimir")
    public void imprimir(@PathVariable Integer id, HttpServletResponse response) throws Exception {
        byte[] pdfBytes = tareaService.descargarPdfTarea(id).getBody();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"tarea_" + id + ".pdf\"");
        response.getOutputStream().write(pdfBytes);
    }

    @GetMapping(WebRoutes.TAREAS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        TareaRecord tarea = tareaService.fetchTareaById(id);
        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), tarea);
        model.addAttribute("estados", List.of("PENDIENTE", "PROPUESTA", "ACEPTADA", "EN_PROCESO", "COMPLETADA",
                "FINALIZADA", "RECHAZADA", "CANCELADA"));
        model.addAttribute("returnUrl", WebRoutes.TAREAS_BASE);
        model.addAttribute("voluntarioNombres", fetchVoluntarioNombres());

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Tarea_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.TAREAS_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String descripcion,
            @RequestParam String estado,
            @RequestParam(required = false) String fechaLimite,
            @RequestParam(required = false) String instrucciones,
            @RequestParam(required = false) List<Integer> voluntarioIds,
            @RequestParam(required = false) Integer voluntarioActorId,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("descripcion", descripcion);
        body.put("estado", estado);
        body.put("fecha", LocalDateTime.now().toString());
        body.put("fechaLimite", fechaLimite != null && !fechaLimite.isEmpty() ? fechaLimite : null);
        body.put("instrucciones", instrucciones);
        body.put("voluntarioIds", voluntarioIds != null ? voluntarioIds : List.of());
        body.put("voluntarioActorId", voluntarioActorId);

        try {
            tareaService.editarTarea(id, body);
            redirectAttributes.addFlashAttribute("successMessage",
                    helper.getMessage("toast.success.tarea_editada"));
            return "redirect:" + WebRoutes.TAREAS_BASE;
        } catch (org.springframework.web.client.RestClientException e) {
            String msg = e.getMessage();
            if (e instanceof org.springframework.web.client.HttpStatusCodeException httpException) {
                String responseBody = httpException.getResponseBodyAsString();
                try {
                    JsonNode root = new ObjectMapper().readTree(responseBody);
                    if (root.has("message")) {
                        msg = root.get("message").asText();
                    } else {
                        msg = responseBody;
                    }
                } catch (Exception parseEx) {
                    msg = responseBody;
                }
            }
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:" + WebRoutes.TAREAS_EDITAR.replace("{id}", id.toString());
        }
    }

    @PostMapping(WebRoutes.TAREAS_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        tareaService.eliminarTarea(id);
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request")))
            return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.TAREAS_BASE).build();
    }

    @SuppressWarnings("unchecked")
    @GetMapping(WebRoutes.TAREAS_HISTORIAL)
    public String verHistorial(@PathVariable Integer id, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        response.setHeader("Vary", "HX-Request");
        TareaRecord tarea = null;
        try {
            tarea = tareaService.fetchTareaById(id);
        } catch (Exception ignored) {
        }
        try {
            List<Map<String, Object>> historialRaw = tareaService.fetchHistorial(id);

            List<Map<String, Object>> historial = new ArrayList<>();
            if (historialRaw != null) {
                for (Map<String, Object> h : historialRaw) {
                    Map<String, Object> hMod = new HashMap<>(h);
                    Object fechaRaw = h.get("fechaCambio");
                    if (fechaRaw instanceof String) {
                        try {
                            hMod.put("fechaCambio", LocalDateTime.parse((String) fechaRaw));
                        } catch (Exception ignored) {
                        }
                    }
                    historial.add(hMod);
                }
            }

            model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), tarea);
            model.addAttribute("historial", historial);
            model.addAttribute("returnUrl", WebRoutes.TAREAS_BASE);
        } catch (Exception e) {
            return "redirect:" + WebRoutes.TAREAS_BASE;
        }

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Tarea_HISTORIAL.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_HISTORIAL.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.TAREAS_PDF)
    public void exportarPDF(
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer voluntarioIdFiltro,
            HttpServletResponse response) throws Exception {
        List<TareaRecord> tareas = tareaService.fetchAllTareas();

        Integer myVoluntarioId = getMyVoluntarioId();

        List<TareaRecord> filtered = tareas.stream()
                .filter(t -> {
                    if (myVoluntarioId != null) {
                        if (t.voluntarioIds() == null || !t.voluntarioIds().contains(myVoluntarioId)) {
                            return false;
                        }
                    }
                    if (voluntarioIdFiltro != null) {
                        if (t.voluntarioIds() == null || !t.voluntarioIds().contains(voluntarioIdFiltro)) {
                            return false;
                        }
                    }
                    if (prioridad != null && !"ALL".equalsIgnoreCase(prioridad)) {
                        if (t.prioridad() == null || !prioridad.equalsIgnoreCase(t.prioridad())) {
                            return false;
                        }
                    }
                    if (estado != null && !"ALL".equalsIgnoreCase(estado)) {
                        if (t.estado() == null || !estado.equalsIgnoreCase(t.estado())) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();

        Map<String, String> voluntarioNombres = fetchVoluntarioNombres();

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("tareas", filtered);
        context.setVariable("voluntarioNombres", voluntarioNombres);
        String html = templateEngine.process(ThymTemplates.Tarea_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=tareas.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    @GetMapping(WebRoutes.TAREAS_EXCEL)
    public void exportarExcel(
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer voluntarioIdFiltro,
            HttpServletResponse response) throws Exception {
        List<TareaRecord> tareas = tareaService.fetchAllTareas();

        Integer myVoluntarioId = getMyVoluntarioId();

        List<TareaRecord> filtered = tareas.stream()
                .filter(t -> {
                    if (myVoluntarioId != null) {
                        if (t.voluntarioIds() == null || !t.voluntarioIds().contains(myVoluntarioId)) {
                            return false;
                        }
                    }
                    if (voluntarioIdFiltro != null) {
                        if (t.voluntarioIds() == null || !t.voluntarioIds().contains(voluntarioIdFiltro)) {
                            return false;
                        }
                    }
                    if (prioridad != null && !"ALL".equalsIgnoreCase(prioridad)) {
                        if (t.prioridad() == null || !prioridad.equalsIgnoreCase(t.prioridad())) {
                            return false;
                        }
                    }
                    if (estado != null && !"ALL".equalsIgnoreCase(estado)) {
                        if (t.estado() == null || !estado.equalsIgnoreCase(t.estado())) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();

        Map<String, String> voluntarioNombres = fetchVoluntarioNombres();

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
                "Tareas",
                List.of("ID", "Descripción", "Prioridad", "Estado", "Voluntarios Asignados", "Fecha", "Fecha Límite",
                        "Instrucciones"),
                filtered,
                List.of(
                        TareaRecord::id,
                        TareaRecord::descripcion,
                        TareaRecord::prioridad,
                        TareaRecord::estado,
                        t -> {
                            if (t.voluntarioIds() == null || t.voluntarioIds().isEmpty()) {
                                return "-";
                            }
                            List<String> names = new ArrayList<>();
                            for (Integer vId : t.voluntarioIds()) {
                                String name = voluntarioNombres.get(String.valueOf(vId));
                                if (name != null)
                                    names.add(name);
                            }
                            return String.join(", ", names);
                        },
                        t -> t.fecha() != null ? t.fecha().toString() : "",
                        t -> t.fechaLimite() != null ? t.fechaLimite().toString() : "",
                        t -> t.instrucciones() != null ? t.instrucciones() : ""));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=tareas.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @PostMapping("/web/tareas/{id}/vincular-seleccion")
    public String vincularSeleccion(@PathVariable Integer id,
            @RequestParam Integer voluntarioId,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String message,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        TareaRecord tarea = tareaService.fetchTareaById(id);

        if (tarea != null) {
            List<Integer> vIds = new ArrayList<>();
            if (tarea.voluntarioIds() != null) {
                vIds.addAll(tarea.voluntarioIds());
            }

            if (!vIds.contains(voluntarioId)) {
                vIds.add(voluntarioId);
            }

            Map<String, Object> updateBody = new HashMap<>();
            updateBody.put("descripcion", tarea.descripcion());
            updateBody.put("estado", "PROPUESTA");
            updateBody.put("instrucciones", tarea.instrucciones());
            updateBody.put("voluntarioIds", vIds);
            updateBody.put("voluntarioActorId", model.getAttribute("currentUserId"));
            updateBody.put("fecha", tarea.fecha() != null ? tarea.fecha().toString() : null);
            updateBody.put("fechaLimite", tarea.fechaLimite() != null ? tarea.fechaLimite().toString() : null);

            try {
                tareaService.editarTarea(id, updateBody);
                String toastMsg = (message != null && !message.isEmpty()) ? message
                        : "Voluntario asignado correctamente";
                response.setHeader("HX-Trigger",
                        "{\"showToast\": {\"message\": \"" + toastMsg + "\", \"type\": \"success\"}}");
            } catch (org.springframework.web.client.RestClientException e) {
                String msg = e.getMessage();
                if (e instanceof org.springframework.web.client.HttpStatusCodeException httpException) {
                    String responseBody = httpException.getResponseBodyAsString();
                    try {
                        JsonNode root = new ObjectMapper().readTree(responseBody);
                        if (root.has("message")) {
                            msg = root.get("message").asText();
                            msg = helper.getMessage(msg); // Intentar traducir si es una clave i18n
                        } else if (root.has("error")) {
                            msg = root.get("error").asText();
                        } else {
                            msg = responseBody;
                        }
                    } catch (Exception parseEx) {
                        msg = responseBody;
                    }
                }

                // Escape comillas para el JSON del header
                msg = msg.replace("\"", "\\\"");
                response.setHeader("HX-Trigger",
                        "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"error\"}}");
            }
        }

        return listar(model, prioridad, estado, null, false, null, 1, 10, null, request, response);
    }

    private Map<String, String> fetchVoluntarioNombres() {
        Map<String, String> nombres = new HashMap<>();
        try {
            List<VoluntarioRecord> voluntarios = voluntarioService.fetchAllVoluntarios();
            List<PerfilLegalRecord> perfiles = voluntarioService.fetchAllPerfilesLegales();
            List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();

            Map<Integer, String> perfilMap = new HashMap<>();
            for (PerfilLegalRecord p : perfiles) {
                if (p.usuarioId() != null) {
                    perfilMap.put(p.usuarioId(), p.nombre() + " " + p.apellido());
                }
            }

            Map<Integer, String> userMap = new HashMap<>();
            for (UsuarioRecord u : usuarios) {
                userMap.put(u.id(), u.username());
            }

            for (VoluntarioRecord v : voluntarios) {
                if (v.id() != null && v.usuarioId() != null) {
                    String nombre = perfilMap.get(v.usuarioId());
                    if (nombre == null)
                        nombre = userMap.get(v.usuarioId());
                    if (nombre == null)
                        nombre = "Voluntario " + v.id();
                    nombres.put(v.id().toString(), nombre.trim());
                }
            }
        } catch (Exception ignored) {
        }
        return nombres;
    }

    @GetMapping("/web/tareas/{id}")
    public String redireccionDetalle(@PathVariable Integer id) {
        return "redirect:/web/tareas/" + id + "/editar";
    }

    @SuppressWarnings("unchecked")
    private Integer getMyVoluntarioId() {
        try {
            UsuarioRecord me = voluntarioService.fetchMe();
            if (me != null) {
                String rol = me.rol() != null ? me.rol().toUpperCase() : "";
                boolean isAdmin = rol.contains("ADMIN");
                if (!isAdmin) {
                    Integer userId = me.id();
                    if (userId != null) {
                        VoluntarioRecord vObj = voluntarioService.fetchVoluntarioByUsuarioId(userId);
                        if (vObj != null) {
                            return vObj.id();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private List<TareaRecord> fetchFiltered(String prioridad, String estado, Integer myVoluntarioId, Integer voluntarioIdFiltro,
            Integer voluntarioIdSeleccion, List<DisponibilidadRecord> disponibilidades) {
        try {
            List<TareaRecord> allTareas = tareaService.fetchAllTareas();

            return allTareas.stream()
                    .filter(t -> {
                        if (myVoluntarioId != null) {
                            if (t.voluntarioIds() == null || !t.voluntarioIds().contains(myVoluntarioId)) {
                                return false;
                            }
                        }
                        if (voluntarioIdFiltro != null) {
                            if (t.voluntarioIds() == null || !t.voluntarioIds().contains(voluntarioIdFiltro)) {
                                return false;
                            }
                        }
                        if (voluntarioIdSeleccion != null) {
                            if (t.voluntarioIds() != null && t.voluntarioIds().contains(voluntarioIdSeleccion)) {
                                return false;
                            }
                            if (t.fechaLimite() != null) {
                                LocalDate taskDate = t.fechaLimite().toLocalDate();
                                boolean isUnavailable = disponibilidades.stream()
                                        .anyMatch(d -> d.fecha() != null && d.fecha().equals(taskDate)
                                                && "NO_DISPONIBLE".equals(d.estado()));
                                if (isUnavailable) {
                                    return false;
                                }
                            }
                        }
                        if (prioridad != null && !"ALL".equalsIgnoreCase(prioridad)) {
                            if (t.prioridad() == null || !prioridad.equalsIgnoreCase(t.prioridad())) {
                                return false;
                            }
                        }
                        if (estado != null && !"ALL".equalsIgnoreCase(estado)) {
                            if (t.estado() == null || !estado.equalsIgnoreCase(t.estado())) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private PaginatedResponse<TareaRecord> paginateList(List<TareaRecord> filtered, int page, int size) {
        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<TareaRecord> paginatedItems = Collections.emptyList();
        if (fromIndex < totalElements && fromIndex >= 0) {
            paginatedItems = filtered.subList(fromIndex, toIndex);
        }

        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;

        return new PaginatedResponse<>(paginatedItems, totalPages, totalElements, page, size, hasNext, hasPrevious);
    }
}
