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

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;

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
            @RequestParam(required = false) String successMessage,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        // Indicar al navegador que la respuesta varía según si es HTMX o no para evitar problemas de caché al dar atrás
        response.setHeader("Vary", "HX-Request");

        // Si estamos en modo selección, obtenemos el nombre del voluntario para el banner
        if (modoSeleccion && voluntarioIdSeleccion != null) {
            try {
                Map<String, Object> v = (Map<String, Object>) restTemplate.getForObject(apiUrl + "/v1/voluntarios/" + voluntarioIdSeleccion, Map.class);
                if (v != null) {
                    Object uId = v.get("usuarioId");
                    if (uId instanceof Map) uId = ((Map<?,?>)uId).get("value");
                    
                    if (uId != null) {
                        Map<String, Object> p = (Map<String, Object>) restTemplate.getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + uId, Map.class);
                        if (p != null) {
                            model.addAttribute("voluntarioNombreSeleccion", p.get("nombre") + " " + p.get("apellido"));
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        List<Object> allTareas = fetchList("/v1/tareas");
        
        // Identificar tareas ya asignadas al voluntario seleccionado
        Set<Integer> assignedTaskIds = new java.util.HashSet<>();
        if (modoSeleccion && voluntarioIdSeleccion != null) {
            for (Object t : allTareas) {
                if (t instanceof Map) {
                    Map<?, ?> tm = (Map<?, ?>) t;
                    Object vIds = tm.get("voluntarioIds");
                    if (vIds instanceof List) {
                        for (Object vid : (List<?>) vIds) {
                            Integer vidInt = null;
                            if (vid instanceof Number) vidInt = ((Number) vid).intValue();
                            else if (vid instanceof Map) vidInt = ((Number)((Map<?,?>)vid).get("value")).intValue();
                            
                            if (voluntarioIdSeleccion.equals(vidInt)) {
                                Object tId = tm.get("id");
                                if (tId instanceof Number) assignedTaskIds.add(((Number)tId).intValue());
                                break;
                            }
                        }
                    }
                }
            }
        }
        model.addAttribute("alreadyAssignedTaskIds", assignedTaskIds);

        List<Object> tareas = new ArrayList<>(allTareas);
        List<Object> voluntarios = fetchList("/v1/voluntarios");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        // Determinar rol y ID del usuario actual desde el modelo
        Object isAdminObj = model.getAttribute("isAdmin");
        boolean isAdmin = isAdminObj != null && (Boolean) isAdminObj;
        Object currentUserId = model.getAttribute("currentUserId");

        Integer myVoluntarioId = null;
        if (!isAdmin && currentUserId != null) {
            try {
                Map<?, ?> vMap = (Map<?, ?>) restTemplate
                        .getForObject(apiUrl + "/v1/voluntarios/usuario/" + currentUserId, Map.class);
                if (vMap != null) {
                    Object vId = vMap.get("id");
                    if (vId instanceof Map)
                        vId = ((Map<?, ?>) vId).get("value");
                    if (vId instanceof Number) {
                        myVoluntarioId = ((Number) vId).intValue();
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Filtrar tareas si el usuario no es admin
        if (!isAdmin) {
            List<Object> misTareas = new ArrayList<>();
            if (myVoluntarioId != null) {
                for (Object t : tareas) {
                    if (t instanceof Map) {
                        Object vIdsObj = ((Map<?, ?>) t).get("voluntarioIds");
                        if (vIdsObj instanceof List) {
                            for (Object vid : (List<?>) vIdsObj) {
                                Object vidRaw = vid;
                                if (vidRaw instanceof Map)
                                    vidRaw = ((Map<?, ?>) vidRaw).get("value");

                                if (vidRaw instanceof Number
                                        && ((Number) vidRaw).intValue() == myVoluntarioId.intValue()) {
                                    misTareas.add(t);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            tareas = misTareas;
        }

        // Aplicar filtros si existen (para el mensaje de "no resultados")
        boolean hasFilters = (prioridad != null && !"ALL".equals(prioridad))
                || (estado != null && !"ALL".equals(estado));
        if (hasFilters) {
            tareas = tareas.stream().filter(t -> {
                Map<?, ?> tm = (Map<?, ?>) t;
                boolean matchP = prioridad == null || "ALL".equals(prioridad) || prioridad.equals(tm.get("prioridad"));
                boolean matchE = estado == null || "ALL".equals(estado) || estado.equals(tm.get("estado"));
                return matchP && matchE;
            }).toList();
        }

        model.addAttribute("selectedPrioridad", prioridad != null ? prioridad : "ALL");
        model.addAttribute("selectedEstado", estado != null ? estado : "ALL");
        model.addAttribute("hasFilters", hasFilters);
        model.addAttribute("totalTareasCount", isAdmin ? allTareas.size() : tareas.size()); // Aproximación

        Map<Integer, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object idObj = ((Map<?, ?>) u).get("id");
                if (idObj instanceof Map)
                    idObj = ((Map<?, ?>) idObj).get("value");
                if (idObj instanceof Number)
                    usuariosMap.put(((Number) idObj).intValue(), (Map<String, Object>) u);
            }
        }

        Map<String, String> voluntarioUsuarioIds = new HashMap<>();
        for (Object v : voluntarios) {
            if (v instanceof Map) {
                Object vId = ((Map<?, ?>) v).get("id");
                if (vId instanceof Map)
                    vId = ((Map<?, ?>) vId).get("value");
                Object uId = ((Map<?, ?>) v).get("usuarioId");
                if (uId instanceof Map)
                    uId = ((Map<?, ?>) uId).get("value");

                if (vId instanceof Number && uId instanceof Number) {
                    voluntarioUsuarioIds.put(vId.toString(), uId.toString());
                }
            }
        }

        Map<String, String> voluntarioNombres = fetchVoluntarioNombres();
        model.addAttribute(ModelAttribute.Tarea_LIST.getName(), tareas);
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
        tarea.put("fecha", LocalDateTime.now().toString());

        if (voluntarioId != null) {
            tarea.put("voluntarioIds", List.of(voluntarioId));
            try {
                Map<String, Object> v = (Map<String, Object>) restTemplate
                        .getForObject(apiUrl + "/v1/voluntarios/" + voluntarioId, Object.class);
                if (v != null && v.get("usuarioId") != null) {
                    Map<String, Object> u = (Map<String, Object>) restTemplate
                            .getForObject(authUrl + "/v1/usuarios/" + v.get("usuarioId"), Object.class);
                    Map<String, Object> p = null;
                    try {
                        p = (Map<String, Object>) restTemplate
                                .getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + v.get("usuarioId"), Object.class);
                    } catch (Exception ignored) {}

                    if (u != null) {
                        Map<String, Object> displayVol = new HashMap<>(u);
                        displayVol.put("id", voluntarioId); // Importante: usar el ID del voluntario
                        if (p != null) {
                            displayVol.put("nombre", p.get("nombre"));
                            displayVol.put("apellido", p.get("apellido"));
                        } else {
                            displayVol.put("nombre", u.get("username"));
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
        model.addAttribute("estados", List.of("PENDIENTE", "PROPUESTA", "ACEPTADA", "EN_PROCESO", "COMPLETADA", "FINALIZADA", "RECHAZADA", "CANCELADA"));
        model.addAttribute("voluntarioNombres", fetchVoluntarioNombres());

        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
        Map<Integer, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object idObj = ((Map<?, ?>) u).get("id");
                if (idObj instanceof Map)
                    idObj = ((Map<?, ?>) idObj).get("value");
                if (idObj instanceof Number)
                    usuariosMap.put(((Number) idObj).intValue(), (Map<String, Object>) u);
            }
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
        Map<String, Object> tarea = restTemplate.getForObject(apiUrl + "/v1/tareas/" + id, Map.class);
        if (tarea != null) {
            // Asegurarnos de que voluntarioIds existe y es una lista
            Object vIds = tarea.get("voluntarioIds");
            if (vIds == null) {
                tarea.put("voluntarioIds", new ArrayList<>());
            } else if (vIds instanceof List) {
                // Normalizar si vienen como objetos complejos
                List<Object> normalized = new ArrayList<>();
                for (Object o : (List<?>) vIds) {
                    if (o instanceof Map) {
                        normalized.add(((Map<?,?>)o).get("value"));
                    } else {
                        normalized.add(o);
                    }
                }
                tarea.put("voluntarioIds", normalized);
            }
        }
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

    @GetMapping(WebRoutes.TAREAS_HISTORIAL)
    public String verHistorial(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response) {
        // Indicar al navegador que la respuesta varía según si es HTMX o no para evitar problemas de caché al dar atrás
        response.setHeader("Vary", "HX-Request");
        try {
            Map<String, Object> tarea = restTemplate.getForObject(apiUrl + "/v1/tareas/" + id, Map.class);
            List<Map<String, Object>> historialRaw = (List<Map<String, Object>>) restTemplate.getForObject(apiUrl + "/v1/tareas/" + id + "/historial", List.class);
            
            List<Map<String, Object>> historial = new ArrayList<>();
            if (historialRaw != null) {
                for (Map<String, Object> h : historialRaw) {
                    Map<String, Object> hMod = new HashMap<>(h);
                    Object fechaRaw = h.get("fechaCambio");
                    if (fechaRaw instanceof String) {
                        try {
                            hMod.put("fechaCambio", java.time.LocalDateTime.parse((String) fechaRaw));
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
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> tareas = fetchList("/v1/tareas");
        Context context = new Context();
        context.setVariable("tareas", tareas);
        String html = templateEngine.process(ThymTemplates.Tarea_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=tareas.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        out.close();
    }

    // --- MODO SELECCIÓN Y VINCULACIÓN ---

    /**
     * Vincula un voluntario a una tarea existente desde el modo selección.
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/web/tareas/{id}/vincular-seleccion")
    public String vincularSeleccion(@PathVariable Integer id, 
                                     @RequestParam Integer voluntarioId, 
                                     @RequestParam(required = false) String prioridad,
                                     @RequestParam(required = false) String estado,
                                     @RequestParam(required = false) String message,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        // 1. Obtener la tarea actual
        Map<String, Object> tarea = (Map<String, Object>) restTemplate.getForObject(apiUrl + "/v1/tareas/" + id, Map.class);
        
        if (tarea != null) {
            // 2. Añadir el nuevo voluntario a la lista existente
            List<Integer> vIds = new ArrayList<>();
            Object existingIds = tarea.get("voluntarioIds");
            if (existingIds instanceof List) {
                for (Object o : (List<?>) existingIds) {
                    if (o instanceof Number) vIds.add(((Number) o).intValue());
                    else if (o instanceof Map) vIds.add(((Number)((Map<?,?>)o).get("value")).intValue());
                }
            }
            
            if (!vIds.contains(voluntarioId)) {
                vIds.add(voluntarioId);
            }
            
            // 3. Reconstruir el cuerpo de la petición limpiamente para evitar errores de parseo (ej: fechas como arrays)
            Map<String, Object> updateBody = new HashMap<>();
            updateBody.put("descripcion", tarea.get("descripcion"));
            updateBody.put("estado", "PROPUESTA"); // Forzar estado de propuesta
            updateBody.put("instrucciones", tarea.get("instrucciones"));
            updateBody.put("voluntarioIds", vIds);
            updateBody.put("voluntarioActorId", model.getAttribute("currentUserId"));
            
            // Formatear fechas si vienen como arrays [año, mes, dia, hora, min] o Strings
            updateBody.put("fecha", formatFechaFromMap(tarea.get("fecha")));
            updateBody.put("fechaLimite", formatFechaFromMap(tarea.get("fechaLimite")));

            restTemplate.put(apiUrl + "/v1/tareas/" + id, updateBody);
            
            // 4. Preparar el Toast profesional vía HTMX Trigger
            String toastMsg = (message != null && !message.isEmpty()) ? message : "Voluntario asignado correctamente";
            response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + toastMsg + "\", \"type\": \"success\"}}");
        }
        
        // 5. Salir del modo selección y volver a la lista de tareas
        // Llamamos a listar para obtener los datos frescos y devolver el fragmento directamente
        return listar(model, prioridad, estado, false, null, null, request, response);
    }

    private String formatFechaFromMap(Object fechaObj) {
        if (fechaObj == null) return null;
        if (fechaObj instanceof String) return (String) fechaObj;
        if (fechaObj instanceof List) {
            List<?> list = (List<?>) fechaObj;
            if (list.size() >= 5) {
                try {
                    int y = ((Number) list.get(0)).intValue();
                    int m = ((Number) list.get(1)).intValue();
                    int d = ((Number) list.get(2)).intValue();
                    int h = ((Number) list.get(3)).intValue();
                    int min = ((Number) list.get(4)).intValue();
                    return String.format("%04d-%02d-%02dT%02d:%02d:00", y, m, d, h, min);
                } catch (Exception e) {
                    System.err.println("Failed to format date array 5: " + list);
                }
            } else if (list.size() == 3) {
                try {
                    int y = ((Number) list.get(0)).intValue();
                    int m = ((Number) list.get(1)).intValue();
                    int d = ((Number) list.get(2)).intValue();
                    return String.format("%04d-%02d-%02dT00:00:00", y, m, d);
                } catch (Exception e) {
                    System.err.println("Failed to format date array 3: " + list);
                }
            }
        }
        return LocalDateTime.now().toString(); // Fallback
    }

    private Map<String, String> fetchVoluntarioNombres() {
        Map<String, String> nombres = new HashMap<>();
        try {
            List<Object> voluntarios = fetchList("/v1/voluntarios");
            List<Object> perfiles = fetchList("/v1/perfiles-legales");
            List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

            Map<Integer, String> perfilMap = new HashMap<>();
            for (Object p : perfiles) {
                if (p instanceof Map) {
                    Map<?, ?> pm = (Map<?, ?>) p;
                    Object uId = pm.get("usuarioId");
                    if (uId instanceof Number) {
                        perfilMap.put(((Number) uId).intValue(), pm.get("nombre") + " " + pm.get("apellido"));
                    }
                }
            }

            Map<Integer, String> userMap = new HashMap<>();
            for (Object u : usuarios) {
                if (u instanceof Map) {
                    Map<?, ?> um = (Map<?, ?>) u;
                    Object id = um.get("id");
                    if (id instanceof Number) {
                        userMap.put(((Number) id).intValue(), String.valueOf(um.get("username")));
                    }
                }
            }

            for (Object v : voluntarios) {
                if (v instanceof Map) {
                    Map<?, ?> vm = (Map<?, ?>) v;
                    Object vIdRaw = vm.get("id");
                    if (vIdRaw instanceof Map)
                        vIdRaw = ((Map<?, ?>) vIdRaw).get("value");
                    Object uIdRaw = vm.get("usuarioId");
                    if (uIdRaw instanceof Map)
                        uIdRaw = ((Map<?, ?>) uIdRaw).get("value");

                    if (vIdRaw instanceof Number && uIdRaw instanceof Number) {
                        String nombre = perfilMap.get(((Number) uIdRaw).intValue());
                        if (nombre == null)
                            nombre = userMap.get(((Number) uIdRaw).intValue());
                        if (nombre == null)
                            nombre = "Voluntario " + vIdRaw;
                        nombres.put(vIdRaw.toString(), nombre.trim());
                    }
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

    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            org.springframework.core.ParameterizedTypeReference<List<Object>> typeRef = 
                new org.springframework.core.ParameterizedTypeReference<List<Object>>() {};
            org.springframework.http.ResponseEntity<List<Object>> response = 
                restTemplate.exchange(finalUrl, org.springframework.http.HttpMethod.GET, null, typeRef);
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
