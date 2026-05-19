package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'VOLUNTARIO_ADOPTANTE')")
public class TareaViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.TAREAS_BASE)
    public String listar(Model model,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado,
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
                VoluntarioRecord v = helper.fetchObject(apiUrl + "/v1/voluntarios/" + voluntarioIdSeleccion, VoluntarioRecord.class);
                if (v != null && v.usuarioId() != null) {
                    PerfilLegalRecord p = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + v.usuarioId(), PerfilLegalRecord.class);
                    if (p != null) {
                        model.addAttribute("voluntarioNombreSeleccion", p.nombre() + " " + p.apellido());
                    }
                }
            } catch (Exception ignored) {}
        }

        Object isAdminObj = model.getAttribute("isAdmin");
        boolean isAdmin = isAdminObj != null && (Boolean) isAdminObj;
        Object currentUserId = model.getAttribute("currentUserId");

        Integer myVoluntarioId = null;
        if (!isAdmin && currentUserId != null) {
            try {
                VoluntarioRecord vObj = helper.fetchObject(apiUrl + "/v1/voluntarios/usuario/" + currentUserId, VoluntarioRecord.class);
                if (vObj != null) {
                    myVoluntarioId = vObj.id();
                }
            } catch (Exception ignored) {}
        }

        PaginatedResponse<TareaRecord> pagination = fetchPaginated(page, size, prioridad, estado, myVoluntarioId);
        List<TareaRecord> tareas = pagination.items();
        
        List<VoluntarioRecord> voluntarios = helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class);

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
        model.addAttribute("myVoluntarioId", myVoluntarioId);
        
        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);

        if ("true".equals(request.getHeader("HX-Request"))) {
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
                VoluntarioRecord v = helper.fetchObject(apiUrl + "/v1/voluntarios/" + voluntarioId, VoluntarioRecord.class);
                if (v != null && v.usuarioId() != null) {
                    UsuarioRecord u = helper.fetchObject(authUrl + "/v1/usuarios/" + v.usuarioId(), UsuarioRecord.class);
                    PerfilLegalRecord p = null;
                    try {
                        p = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + v.usuarioId(), PerfilLegalRecord.class);
                    } catch (Exception ignored) {}

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
            } catch (Exception ignored) {}
            model.addAttribute("returnUrl", WebRoutes.VOLUNTARIOS_BASE);
        } else {
            model.addAttribute("returnUrl", WebRoutes.TAREAS_BASE);
        }

        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), tarea);
        model.addAttribute("estados", List.of("PENDIENTE", "PROPUESTA", "ACEPTADA", "EN_PROCESO", "COMPLETADA", "FINALIZADA", "RECHAZADA", "CANCELADA"));
        model.addAttribute("voluntarioNombres", fetchVoluntarioNombres());

        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        Map<Integer, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(u.id(), u);
        }
        model.addAttribute("usuariosMap", usuariosMap);

        if ("true".equals(request.getHeader("HX-Request"))) {
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
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("descripcion", descripcion);
        body.put("estado", estado);
        body.put("fecha", LocalDateTime.now().toString());
        body.put("fechaLimite", fechaLimite != null && !fechaLimite.isEmpty() ? fechaLimite : null);
        body.put("instrucciones", instrucciones);
        body.put("voluntarioIds", voluntarioIds != null ? voluntarioIds : List.of());

        restTemplate.postForObject(apiUrl + "/v1/tareas", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Tarea creada correctamente");
        return "redirect:" + WebRoutes.TAREAS_BASE;
    }

    @GetMapping(WebRoutes.TAREAS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        TareaRecord tarea = helper.fetchObject(apiUrl + "/v1/tareas/" + id, TareaRecord.class);
        model.addAttribute(ModelAttribute.SINGLE_Tarea.getName(), tarea);
        model.addAttribute("estados", List.of("PENDIENTE", "PROPUESTA", "ACEPTADA", "EN_PROCESO", "COMPLETADA", "FINALIZADA", "RECHAZADA", "CANCELADA"));
        model.addAttribute("returnUrl", WebRoutes.TAREAS_BASE);
        model.addAttribute("voluntarioNombres", fetchVoluntarioNombres());

        if ("true".equals(request.getHeader("HX-Request"))) {
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

        restTemplate.put(apiUrl + "/v1/tareas/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Tarea editada correctamente");
        return "redirect:" + WebRoutes.TAREAS_BASE;
    }

    @PostMapping(WebRoutes.TAREAS_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/tareas/" + id);
        if ("true".equals(request.getHeader("HX-Request")))
            return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.TAREAS_BASE).build();
    }

    @SuppressWarnings("unchecked")
    @GetMapping(WebRoutes.TAREAS_HISTORIAL)
    public String verHistorial(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Vary", "HX-Request");
        try {
            TareaRecord tarea = helper.fetchObject(apiUrl + "/v1/tareas/" + id, TareaRecord.class);
            List<Map<String, Object>> historialRaw = (List<Map<String, Object>>) restTemplate.getForObject(apiUrl + "/v1/tareas/" + id + "/historial", List.class);
            
            List<Map<String, Object>> historial = new ArrayList<>();
            if (historialRaw != null) {
                for (Map<String, Object> h : historialRaw) {
                    Map<String, Object> hMod = new HashMap<>(h);
                    Object fechaRaw = h.get("fechaCambio");
                    if (fechaRaw instanceof String) {
                        try {
                            hMod.put("fechaCambio", LocalDateTime.parse((String) fechaRaw));
                        } catch (Exception ignored) {}
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

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Tarea_HISTORIAL.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Tarea_HISTORIAL.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.TAREAS_PDF)
    public void exportarPDF(
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String estado,
            HttpServletResponse response) throws Exception {
        String allUrl = apiUrl + "/v1/tareas?size=9999";
        List<TareaRecord> tareas = helper.fetchList(allUrl, TareaRecord.class);
        
        Integer myVoluntarioId = getMyVoluntarioId();

        List<TareaRecord> filtered = tareas.stream()
                .filter(t -> {
                    if (myVoluntarioId != null) {
                        if (t.voluntarioIds() == null || !t.voluntarioIds().contains(myVoluntarioId)) {
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

        Context context = new Context(org.springframework.context.i18n.LocaleContextHolder.getLocale());
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
            HttpServletResponse response) throws Exception {
        String allUrl = apiUrl + "/v1/tareas?size=9999";
        List<TareaRecord> tareas = helper.fetchList(allUrl, TareaRecord.class);
        
        Integer myVoluntarioId = getMyVoluntarioId();

        List<TareaRecord> filtered = tareas.stream()
                .filter(t -> {
                    if (myVoluntarioId != null) {
                        if (t.voluntarioIds() == null || !t.voluntarioIds().contains(myVoluntarioId)) {
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
            List.of("ID", "Descripción", "Prioridad", "Estado", "Voluntarios Asignados", "Fecha", "Fecha Límite", "Instrucciones"),
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
                        if (name != null) names.add(name);
                    }
                    return String.join(", ", names);
                },
                t -> t.fecha() != null ? t.fecha().toString() : "",
                t -> t.fechaLimite() != null ? t.fechaLimite().toString() : "",
                t -> t.instrucciones() != null ? t.instrucciones() : ""
            )
        );
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
        TareaRecord tarea = helper.fetchObject(apiUrl + "/v1/tareas/" + id, TareaRecord.class);
        
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

            restTemplate.put(apiUrl + "/v1/tareas/" + id, updateBody);
            
            String toastMsg = (message != null && !message.isEmpty()) ? message : "Voluntario asignado correctamente";
            response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + toastMsg + "\", \"type\": \"success\"}}");
        }
        
        return listar(model, prioridad, estado, false, null, 1, 10, null, request, response);
    }

    private Map<String, String> fetchVoluntarioNombres() {
        Map<String, String> nombres = new HashMap<>();
        try {
            List<VoluntarioRecord> voluntarios = helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class);
            List<PerfilLegalRecord> perfiles = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);
            List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);

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
        } catch (Exception ignored) {}
        return nombres;
    }

    @GetMapping("/web/tareas/{id}")
    public String redireccionDetalle(@PathVariable Integer id) {
        return "redirect:/web/tareas/" + id + "/editar";
    }

    @SuppressWarnings("unchecked")
    private Integer getMyVoluntarioId() {
        try {
            Map<String, Object> me = restTemplate.getForObject(authUrl + "/v1/me", Map.class);
            if (me != null) {
                String rol = me.get("rol") != null ? String.valueOf(me.get("rol")).toUpperCase() : "";
                boolean isAdmin = rol.contains("ADMIN");
                if (!isAdmin) {
                    Object idObj = me.get("id");
                    if (idObj instanceof Map)
                        idObj = ((Map<?, ?>) idObj).get("value");
                    Integer userId = (idObj instanceof Number) ? ((Number) idObj).intValue() : null;
                    if (userId != null) {
                        VoluntarioRecord vObj = helper.fetchObject(apiUrl + "/v1/voluntarios/usuario/" + userId, VoluntarioRecord.class);
                        if (vObj != null) {
                            return vObj.id();
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private PaginatedResponse<TareaRecord> fetchPaginated(int page, int size, String prioridad, String estado, Integer myVoluntarioId) {
        try {
            String allUrl = apiUrl + "/v1/tareas?size=9999";
            List<TareaRecord> allTareas = helper.fetchList(allUrl, TareaRecord.class);
            
            List<TareaRecord> filtered = allTareas.stream()
                    .filter(t -> {
                        if (myVoluntarioId != null) {
                            if (t.voluntarioIds() == null || !t.voluntarioIds().contains(myVoluntarioId)) {
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
        } catch (Exception e) {
            return new PaginatedResponse<>(Collections.emptyList(), 0, 0, page, size, false, false);
        }
    }
}
