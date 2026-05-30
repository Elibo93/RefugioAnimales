package es.refugio.frontend.web;

import org.springframework.context.i18n.LocaleContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.springframework.security.core.Authentication;
import es.refugio.common.util.ExcelExportHelper;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.service.MessageService;
import java.time.LocalDateTime;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import es.refugio.frontend.service.SolicitudAdopcionService;

/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
@Controller
@RequiredArgsConstructor
public class SolicitudAdopcionViewController {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudAdopcionViewController.class);

    private final SolicitudAdopcionService solicitudService;
    private final TemplateEngine templateEngine;
    private final MessageService messageService;

    @GetMapping(WebRoutes.SOLICITUDES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String successMessage) {
        if (response != null)
            response.setHeader("Vary", "HX-Request");

        Map<String, Object> modelData = solicitudService.buildListarModelData(page, size);
        model.addAllAttributes(modelData);
        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), modelData.get("solicitudList"));
        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_BASE);

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Solicitud_LIST.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    @GetMapping(WebRoutes.SOLICITUDES_MIS_ADOPTADOS)
    public String misAdoptados(Model model, HttpServletRequest request, HttpServletResponse response) {
        if (response != null)
            response.setHeader("Vary", "HX-Request");

        Object userIdObj = model.getAttribute("currentUserId");
        if (userIdObj == null)
            return "redirect:/login";

        Integer currentUserId = (userIdObj instanceof Number) ? ((Number) userIdObj).intValue()
                : Integer.parseInt(userIdObj.toString());

        Map<String, Object> modelData = solicitudService.buildMisAdoptadosModelData(currentUserId);
        
        @SuppressWarnings("unchecked")
        List<SolicitudAdopcionRecord> misSolicitudes = (List<SolicitudAdopcionRecord>) modelData.get("solicitudList");

        if (misSolicitudes == null || misSolicitudes.isEmpty()) {
            model.addAttribute("mensajeVacio",
                    "Vaya, parece que aún no te has hecho con ninguno de nuestros amiguitos");
            model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                    FragmentoContenido.MIS_ADOPTADOS_VACIO.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        model.addAllAttributes(modelData);
        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), misSolicitudes);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.MIS_ADOPTADOS_LISTA.getPath());

        if (request != null && "true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.MIS_ADOPTADOS_LISTA.getPath() + " :: content";
        }

        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_NUEVA)
    public String formulario(Model model, @RequestParam(required = false) Integer animalId,
            HttpServletRequest request) {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("id", null);
        solicitud.put("animalId", animalId);
        solicitud.put("adoptanteId", null);
        solicitud.put("estado", "PENDIENTE");
        solicitud.put("comentario", null);
        solicitud.put("comentarioAdmin", null);
        solicitud.put("fecha", LocalDateTime.now().toString());

        if (animalId != null) {
            try {
                var animal = solicitudService.fetchAnimalById(animalId);
                if (animal != null) {
                    model.addAttribute("animalData", animal);
                }
            } catch (Exception e) {
                logger.error("Error al cargar animal para nueva solicitud: " + e.getMessage());
            }
        }

        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales", solicitudService.fetchAllAnimales());
        model.addAttribute("adoptantes", solicitudService.fetchAllAdoptantes());
        model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_NUEVA);

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Solicitud_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
        model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.SOLICITUDES_NUEVA)
    @PreAuthorize("hasRole('ADMIN')")
    public String crear(@RequestParam Integer animalId,
            @RequestParam Integer adoptanteId,
            @RequestParam String comentario,
            @RequestParam(required = false) String comentarioAdmin,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        solicitudService.crearSolicitud(animalId, adoptanteId, "PENDIENTE", comentario, comentarioAdmin,
                LocalDateTime.now());

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return "fragments/content/solicitudes_adopcion/solicitud-creada :: success-modal";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud creada correctamente");
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping(WebRoutes.SOLICITUDES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        try {
            SolicitudAdopcionRecord solicitud = solicitudService.fetchSolicitudById(id);
            if (solicitud == null) {
                return "redirect:" + WebRoutes.SOLICITUDES_BASE;
            }
            model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);

            if (solicitud.adoptanteId() != null) {
                AdoptanteRecord adoptante = solicitudService.fetchAdoptanteById(solicitud.adoptanteId());
                if (adoptante != null) {
                    model.addAttribute("adoptanteActual", adoptante);

                    if (adoptante.usuarioId() != null) {
                        try {
                            PerfilLegalRecord perfil = solicitudService
                                    .fetchPerfilLegalByUsuarioId(adoptante.usuarioId());
                            if (perfil != null) {
                                model.addAttribute("nombreAdoptante", perfil.nombre() + " " + perfil.apellido());
                            }
                        } catch (Exception e) {
                            logger.error("Error al cargar PerfilLegal para editar solicitud: " + e.getMessage());
                        }
                    }
                }
            }

            if (solicitud.animalId() != null) {
                AnimalRecord animal = solicitudService.fetchAnimalById(solicitud.animalId());
                if (animal != null) {
                    model.addAttribute("animalData", animal);
                }
            }

            model.addAttribute("animales", solicitudService.fetchAllAnimales());
            model.addAttribute("adoptantes", solicitudService.fetchAllAdoptantes());
            model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            if ("true".equals(request.getHeader("HX-Request"))
                    && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                return FragmentoContenido.Solicitud_FORM.getPath() + " :: content";
            }

            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                    FragmentoContenido.Solicitud_FORM.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        } catch (Throwable t) {
            logger.error("ERROR DIAGNÓSTICO EN EDITAR FORMULARIO:", t);
            throw new RuntimeException(t);
        }
    }

    @PostMapping(WebRoutes.SOLICITUDES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer animalId,
            @RequestParam Integer adoptanteId,
            @RequestParam String estado,
            @RequestParam String comentario,
            @RequestParam(required = false) String comentarioAdmin,
            @RequestParam(required = false) String redireccion,
            RedirectAttributes redirectAttributes) {

        try {
            solicitudService.editarSolicitud(id, animalId, adoptanteId, estado, comentario, comentarioAdmin,
                    LocalDateTime.now());
            redirectAttributes.addFlashAttribute("successMessage", "Solicitud actualizada correctamente");
        } catch (HttpStatusCodeException e) {
            String errorMsg = "Error al actualizar la solicitud.";
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(e.getResponseBodyAsString());
                if (root.has("message")) {
                    String backendMsg = root.get("message").asText();
                    if (backendMsg.contains("ya tiene una adopción activa o en proceso")) {
                        String nombreAnimal = "El animal";
                        try {
                            AnimalRecord animal = solicitudService.fetchAnimalById(animalId);
                            if (animal != null && animal.nombre() != null) {
                                nombreAnimal = animal.nombre();
                            }
                        } catch (Exception ignore) {}
                        errorMsg = messageService.getMessage("error.animal.ya.adoptado", nombreAnimal);
                    } else {
                        errorMsg = backendMsg;
                    }
                }
            } catch (Exception ex) {
                // Ignorar error de parseo JSON y usar mensaje genérico
            }
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:/web/solicitudes/" + id + "/editar";
        } catch (Exception e) {
            logger.error("Error inesperado en procesarEdicion: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado al actualizar la solicitud.");
            return "redirect:/web/solicitudes/" + id + "/editar";
        }

        if ("detalle".equals(redireccion)) {
            return "redirect:/web/solicitudes/" + id + "/detalle";
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        solicitudService.eliminarSolicitud(id);
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return ResponseEntity.ok()
                    .header("HX-Trigger",
                            "{\"showToast\": {\"message\": \"Solicitud eliminada correctamente\", \"type\": \"success\"}}")
                    .body("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.SOLICITUDES_BASE).build();
    }

    @PostMapping(WebRoutes.SOLICITUDES_APROBAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String aprobarSolicitud(@PathVariable Integer id, Model model, HttpServletRequest request,
            HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "Solicitud aprobada y adopción registrada correctamente.";
        try {
            solicitudService.aprobarSolicitud(id);
            redirectAttributes.addFlashAttribute("successMessage", msg);
            if ("true".equals(request.getHeader("HX-Request"))
                    && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg
                        + "\", \"type\": \"success\"}, \"adoptionStatusChanged\": {}}");
            }
        } catch (Exception e) {
            logger.error("Error al aprobar solicitud: " + e.getMessage());
            String errorMsg = "Error al procesar la aprobación.";
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            if ("true".equals(request.getHeader("HX-Request"))
                    && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                response.setHeader("HX-Trigger",
                        "{\"showToast\": {\"message\": \"" + errorMsg + "\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return listar(model, request, response, 1, 10, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_RECHAZAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String rechazarSolicitud(@PathVariable Integer id, Model model, HttpServletRequest request,
            HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "La solicitud ha sido rechazada correctamente.";
        try {
            solicitudService.actualizarEstadoSolicitud(id, "RECHAZADA");
            redirectAttributes.addFlashAttribute("successMessage", msg);
            if ("true".equals(request.getHeader("HX-Request"))
                    && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg
                        + "\", \"type\": \"success\"}, \"adoptionStatusChanged\": {}}");
            }
        } catch (Exception e) {
            logger.error("Error al rechazar solicitud: " + e.getMessage());
            if ("true".equals(request.getHeader("HX-Request"))
                    && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                response.setHeader("HX-Trigger",
                        "{\"showToast\": {\"message\": \"Error al rechazar la solicitud\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return listar(model, request, response, 1, 10, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_REVISION)
    @PreAuthorize("hasRole('ADMIN')")
    public String ponerEnRevision(@PathVariable Integer id, Model model, HttpServletRequest request,
            HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "La solicitud ha sido movida a revisión.";
        try {
            solicitudService.actualizarEstadoSolicitud(id, "EN_REVISION");
            redirectAttributes.addFlashAttribute("successMessage", msg);
            if ("true".equals(request.getHeader("HX-Request"))
                    && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                response.setHeader("HX-Trigger",
                        "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"success\"}}");
            }
        } catch (Exception e) {
            logger.error("Error al poner en revisión: " + e.getMessage());
            if ("true".equals(request.getHeader("HX-Request"))
                    && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
                response.setHeader("HX-Trigger",
                        "{\"showToast\": {\"message\": \"Error al mover a revisión\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return listar(model, request, response, 1, 10, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping(WebRoutes.SOLICITUDES_PDF)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<SolicitudAdopcionRecord> solicitudes = solicitudService.fetchAllSolicitudes();
        List<AnimalRecord> animales = solicitudService.fetchAllAnimales();
        List<AdoptanteRecord> adoptantes = solicitudService.fetchAllAdoptantes();
        List<PerfilLegalRecord> perfiles = solicitudService.fetchAllPerfilesLegales();

        Map<String, String> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a.nombre());
        }

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfiles) {
            if (p.usuarioId() != null) {
                perfilesMap.put(String.valueOf(p.usuarioId()), p);
            }
        }

        Map<String, String> adoptanteNombres = new HashMap<>();
        for (AdoptanteRecord a : adoptantes) {
            if (a.usuarioId() != null) {
                PerfilLegalRecord perfil = perfilesMap.get(String.valueOf(a.usuarioId()));
                if (perfil != null) {
                    String nombre = perfil.nombre() != null ? perfil.nombre() : "";
                    String apellido = perfil.apellido() != null ? perfil.apellido() : "";
                    adoptanteNombres.put(String.valueOf(a.id()), (nombre + " " + apellido).trim());
                } else {
                    adoptanteNombres.put(String.valueOf(a.id()), "Adoptante #" + a.id());
                }
            }
        }

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("solicitudes", solicitudes);
        context.setVariable("animalesMap", animalesMap);
        context.setVariable("adoptanteNombres", adoptanteNombres);

        String html = templateEngine.process(ThymTemplates.Solicitud_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    @GetMapping(WebRoutes.SOLICITUDES_EXCEL)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        List<SolicitudAdopcionRecord> solicitudes = solicitudService.fetchAllSolicitudes();
        List<AnimalRecord> animales = solicitudService.fetchAllAnimales();
        List<AdoptanteRecord> adoptantes = solicitudService.fetchAllAdoptantes();
        List<PerfilLegalRecord> perfiles = solicitudService.fetchAllPerfilesLegales();

        Map<String, String> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a.nombre());
        }

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfiles) {
            if (p.usuarioId() != null) {
                perfilesMap.put(String.valueOf(p.usuarioId()), p);
            }
        }

        Map<String, String> adoptanteNombres = new HashMap<>();
        for (AdoptanteRecord a : adoptantes) {
            if (a.usuarioId() != null) {
                PerfilLegalRecord perfil = perfilesMap.get(String.valueOf(a.usuarioId()));
                if (perfil != null) {
                    String nombre = perfil.nombre() != null ? perfil.nombre() : "";
                    String apellido = perfil.apellido() != null ? perfil.apellido() : "";
                    adoptanteNombres.put(String.valueOf(a.id()), (nombre + " " + apellido).trim());
                } else {
                    adoptanteNombres.put(String.valueOf(a.id()), "Adoptante #" + a.id());
                }
            }
        }

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
                "Solicitudes de Adopción",
                List.of("ID", "ID Animal", "Animal", "ID Adoptante", "Adoptante", "Fecha", "Estado", "Comentario",
                        "Comentario Admin"),
                solicitudes,
                List.of(
                        SolicitudAdopcionRecord::id,
                        SolicitudAdopcionRecord::animalId,
                        s -> animalesMap.getOrDefault(String.valueOf(s.animalId()), "Animal #" + s.animalId()),
                        SolicitudAdopcionRecord::adoptanteId,
                        s -> adoptanteNombres.getOrDefault(String.valueOf(s.adoptanteId()),
                                "Adoptante #" + s.adoptanteId()),
                        s -> s.fecha() != null ? s.fecha().toString() : "",
                        SolicitudAdopcionRecord::estado,
                        s -> s.comentario() != null ? s.comentario() : "",
                        s -> s.comentarioAdmin() != null ? s.comentarioAdmin() : ""));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @GetMapping(WebRoutes.SOLICITUDES_PUBLICO_REGISTRO)
    public String formularioPublico(Model model, @RequestParam Integer animalId) {
        try {
            AnimalRecord animal = solicitudService.fetchAnimalById(animalId);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "",
                    null, 0.0, 0, false, 0, 0));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_REGISTRO.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_OPCIONES)
    public String formularioOpciones(Model model, @RequestParam Integer animalId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean isAdoptante = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADOPTANTE"));

            if (isAdoptante) {
                return "redirect:" + WebRoutes.SOLICITUDES_DIRECTA_FORM + "?animalId=" + animalId;
            } else {
                return "redirect:" + WebRoutes.SOLICITUDES_CONVERTIR + "?animalId=" + animalId;
            }
        }

        try {
            AnimalRecord animal = solicitudService.fetchAnimalById(animalId);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "",
                    null, 0.0, 0, false, 0, 0));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_OPCIONES.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_CONVERTIR)
    public String formularioConversion(Model model, @RequestParam Integer animalId) {
        try {
            AnimalRecord animal = solicitudService.fetchAnimalById(animalId);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "",
                    null, 0.0, 0, false, 0, 0));
        }

        model.addAttribute("perfilExistente", false);
        model.addAttribute("nombre", "");
        model.addAttribute("apellido", "");
        model.addAttribute("telefono", "");
        model.addAttribute("dni", "");
        model.addAttribute("direccion", "");
        model.addAttribute("fechaNacimiento", "");
        model.addAttribute("comentario", "");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                UsuarioRecord me = solicitudService.fetchMe();
                if (me != null) {
                    Integer currentUserId = me.id();
                    PerfilLegalRecord perfil = solicitudService.fetchPerfilLegalByUsuarioId(currentUserId);
                    if (perfil != null) {
                        model.addAttribute("nombre", perfil.nombre() != null ? perfil.nombre() : "");
                        model.addAttribute("apellido", perfil.apellido() != null ? perfil.apellido() : "");
                        model.addAttribute("telefono", perfil.telefono() != null ? perfil.telefono() : "");
                        model.addAttribute("dni", perfil.dni() != null ? perfil.dni() : "");
                        model.addAttribute("direccion", perfil.direccion() != null ? perfil.direccion() : "");
                        model.addAttribute("fechaNacimiento",
                                perfil.fechaNacimiento() != null ? perfil.fechaNacimiento() : "");
                        model.addAttribute("perfilExistente", true);
                    }
                }
            } catch (Exception e) {
                logger.warn("No se pudo pre-cargar el perfil legal: " + e.getMessage());
            }
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_CONVERSION.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @SuppressWarnings("unchecked")
    @GetMapping(WebRoutes.SOLICITUDES_DIRECTA_FORM)
    public String formularioDirecta(Model model, @RequestParam Integer animalId) {
        model.addAttribute("perfilExistente", false);
        model.addAttribute("nombre", "");
        model.addAttribute("apellido", "");

        try {
            AnimalRecord animal = solicitudService.fetchAnimalById(animalId);
            model.addAttribute("animal", animal);

            Set<Integer> solicitados = (Set<Integer>) model.getAttribute("animalesSolicitadosIds");
            if (solicitados != null && solicitados.contains(animalId)) {
                return "redirect:" + WebRoutes.HOME
                        + "?errorMessage=Ya tienes una solicitud pendiente para este animal.";
            }

            UsuarioRecord me = solicitudService.fetchMe();
            if (me != null) {
                Integer currentUserId = me.id();
                try {
                    PerfilLegalRecord perfil = solicitudService.fetchPerfilLegalByUsuarioId(currentUserId);
                    if (perfil != null) {
                        model.addAttribute("nombre", perfil.nombre() != null ? perfil.nombre() : "");
                        model.addAttribute("apellido", perfil.apellido() != null ? perfil.apellido() : "");
                        model.addAttribute("perfilExistente", true);
                    }
                } catch (Exception e) {
                    logger.warn("No se encontró perfil legal para la ficha de adopción directa");
                }
            }
        } catch (Exception e) {
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "",
                    null, 0.0, 0, false, 0, 0));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_DIRECTA_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @SuppressWarnings("unchecked")
    @PostMapping(WebRoutes.SOLICITUDES_CONVERTIR)
    public String procesarConversionYAdopcion(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response,
            Model model) {

        UsuarioRecord me = solicitudService.fetchMe();
        if (me == null) {
            return "redirect:/login";
        }
        Integer usuarioId = me.id();

        Map<String, Object> bodySolicitud = new HashMap<>();
        bodySolicitud.put("animalId", animalId);
        bodySolicitud.put("comentario", comentario);
        bodySolicitud.put("nombre", nombre);
        bodySolicitud.put("apellido", apellido);
        bodySolicitud.put("dni", dni);
        bodySolicitud.put("direccion", direccion);
        bodySolicitud.put("telefono", telefono);
        bodySolicitud.put("fechaNacimiento", fechaNacimiento);

        logger.info("Enviando solicitud de conversión y adopción para usuario: " + usuarioId);
        try {
            solicitudService.convertirYAdopcion(bodySolicitud);
        } catch (RestClientResponseException e) {
            Map<?, ?> responseBody = e.getResponseBodyAs(Map.class);
            String errorMsg = "Error al procesar la adopción. Es posible que el DNI ya esté en uso.";

            if (responseBody != null) {
                if (responseBody.containsKey("message")) {
                    errorMsg = (String) responseBody.get("message");
                } else if (responseBody.containsKey("dni")) {
                    errorMsg = (String) responseBody.get("dni");
                } else if (responseBody.containsKey("fechaNacimiento")) {
                    errorMsg = (String) responseBody.get("fechaNacimiento");
                } else if (!responseBody.isEmpty()) {
                    errorMsg = responseBody.values().iterator().next().toString();
                }
            }

            try {
                AnimalRecord animal = solicitudService.fetchAnimalById(animalId);
                model.addAttribute("animal", animal);
            } catch (Exception ignored) {
                model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "",
                        null, 0.0, 0, false, 0, 0));
            }

            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("telefono", telefono);
            model.addAttribute("dni", dni);
            model.addAttribute("direccion", direccion);
            model.addAttribute("fechaNacimiento", fechaNacimiento);
            model.addAttribute("comentario", comentario);
            model.addAttribute("errorMessage", errorMsg);

            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                    FragmentoContenido.Solicitud_CONVERSION.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        try {
            if (me.rol() != null) {
                String currentRol = me.rol();
                Integer usuarioIdAuth = me.id();

                String newRol = null;
                if ("ROLE_PUBLICO".equals(currentRol)) {
                    newRol = "ROLE_ADOPTANTE";
                } else if ("ROLE_VOLUNTARIO".equals(currentRol)) {
                    newRol = "ROLE_VOLUNTARIO_ADOPTANTE";
                }

                if (newRol != null) {
                    Map<String, String> patchBody = new HashMap<>();
                    patchBody.put("rol", newRol);

                    var responseEntity = solicitudService.actualizarRolUsuario(usuarioIdAuth, newRol);

                    if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                        String newToken = (String) responseEntity.getBody().get("token");
                        if (newToken != null) {
                            Cookie authCookie = new Cookie("JWT_TOKEN", newToken);
                            authCookie.setHttpOnly(true);
                            authCookie.setPath("/");
                            authCookie.setMaxAge(86400);
                            response.addCookie(authCookie);
                            logger.info("Cookie JWT_TOKEN actualizada en el frontend con el nuevo rol " + newRol);

                            List<SimpleGrantedAuthority> authoritiesList = new ArrayList<>();
                            authoritiesList.add(new SimpleGrantedAuthority(newRol));
                            if ("ROLE_VOLUNTARIO_ADOPTANTE".equals(newRol)) {
                                authoritiesList.add(new SimpleGrantedAuthority("ROLE_VOLUNTARIO"));
                                authoritiesList.add(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
                            }

                            var auth = new UsernamePasswordAuthenticationToken(me.email(), null, authoritiesList);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error al actualizar el rol del usuario: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Solicitud enviada con éxito. En breve nos pondremos en contacto contigo.");
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.SOLICITUDES_DIRECTA)
    public String procesarAdopcionDirecta(
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("comentario", comentario);

        try {
            solicitudService.crearAdopcionDirecta(body);
        } catch (HttpClientErrorException.Unauthorized e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageService.getMessage("toast.error.iniciar_sesion"));
            return "redirect:" + WebRoutes.SOLICITUDES_OPCIONES + "?animalId=" + animalId;
        } catch (HttpStatusCodeException e) {
            String errorMsg = "Error al procesar la solicitud.";
            try {
                Map<?, ?> errorMap = e.getResponseBodyAs(Map.class);
                if (errorMap != null && errorMap.containsKey("message")) {
                    errorMsg = (String) errorMap.get("message");
                }
            } catch (Exception ignored) {
            }
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:" + WebRoutes.SOLICITUDES_DIRECTA_FORM + "?animalId=" + animalId;
        }

        redirectAttributes.addFlashAttribute("successMessage",
                messageService.getMessage("toast.success.solicitud_enviada_exito"));
        return "redirect:" + WebRoutes.HOME;
    }

    @PostMapping(WebRoutes.SOLICITUDES_PUBLICO_REGISTRO)
    public String procesarRegistroYAdopcion(
            @RequestParam("userName") String userName,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam("password") String password,
            @RequestParam String telefono,
            @RequestParam String dni,
            @RequestParam String direccion,
            @RequestParam String fechaNacimiento,
            @RequestParam Integer animalId,
            @RequestParam(required = false) String comentario,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userBody = new LinkedHashMap<>();
        userBody.put("email", email);
        userBody.put("username", userName);
        userBody.put("contrasena", password);
        userBody.put("rol", "ROLE_ADOPTANTE");

        Integer usuarioId = null;
        try {
            Map<?, ?> respUser = solicitudService.registrarUsuarioPublico(userBody);
            if (respUser != null && respUser.get("id") != null) {
                Object rawId = respUser.get("id");
                usuarioId = (rawId instanceof Number) ? ((Number) rawId).intValue()
                        : Integer.parseInt(rawId.toString());
            } else {
                throw new Exception("No se pudo obtener el ID del usuario tras el registro");
            }
        } catch (Exception e) {
            String errorMsg = es.refugio.frontend.web.util.ErrorMessageExtractor.extract(e);
            if (errorMsg == null || errorMsg.contains("Exception") || errorMsg.contains("Error desconocido")) {
                errorMsg = "Error inesperado al contactar con el servicio de autenticación.";
            }

            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("userName", userName);
            redirectAttributes.addFlashAttribute("nombre", nombre);
            redirectAttributes.addFlashAttribute("apellido", apellido);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("telefono", telefono);
            redirectAttributes.addFlashAttribute("dni", dni);
            redirectAttributes.addFlashAttribute("direccion", direccion);
            redirectAttributes.addFlashAttribute("fechaNacimiento", fechaNacimiento);
            redirectAttributes.addFlashAttribute("comentario", comentario);

            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        Map<String, Object> bodySolicitud = new HashMap<>();
        bodySolicitud.put("usuarioId", usuarioId);
        bodySolicitud.put("animalId", animalId);
        bodySolicitud.put("comentario", comentario);
        bodySolicitud.put("nombre", nombre);
        bodySolicitud.put("apellido", apellido);
        bodySolicitud.put("dni", dni);
        bodySolicitud.put("direccion", direccion);
        bodySolicitud.put("telefono", telefono);
        bodySolicitud.put("fechaNacimiento", fechaNacimiento);

        try {
            solicitudService.registrarYAdopcionPublico(bodySolicitud);
        } catch (Exception e) {
            try {
                if (usuarioId != null) {
                    solicitudService.eliminarUsuarioAuth(usuarioId);
                    logger.warn("Usuario " + usuarioId + " eliminado por rollback de adopción fallida.");
                }
            } catch (Exception ex) {
                logger.error("Error al intentar hacer rollback (eliminar) el usuario auth: " + ex.getMessage());
            }
            
            String errorMsg = es.refugio.frontend.web.util.ErrorMessageExtractor.extract(e);
            if (errorMsg == null || errorMsg.contains("Exception") || errorMsg.contains("Error desconocido")) {
                errorMsg = "Error inesperado al procesar la adopción en el backend.";
            }

            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("userName", userName);
            redirectAttributes.addFlashAttribute("nombre", nombre);
            redirectAttributes.addFlashAttribute("apellido", apellido);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("telefono", telefono);
            redirectAttributes.addFlashAttribute("dni", dni);
            redirectAttributes.addFlashAttribute("direccion", direccion);
            redirectAttributes.addFlashAttribute("fechaNacimiento", fechaNacimiento);
            redirectAttributes.addFlashAttribute("comentario", comentario);
            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        try {
            ResponseEntity<String> loginResponse = solicitudService.loginPost(email, password);

            List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                for (String cookieStr : cookies) {
                    if (cookieStr.startsWith("JWT_TOKEN=")) {
                        int semiIndex = cookieStr.indexOf(";");
                        String value = semiIndex > -1 ? cookieStr.substring(10, semiIndex) : cookieStr.substring(10);
                        Cookie cookie = new Cookie("JWT_TOKEN", value);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(86400);
                        response.addCookie(cookie);

                        List<SimpleGrantedAuthority> authoritiesList = new ArrayList<>();
                        authoritiesList.add(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
                        var auth = new UsernamePasswordAuthenticationToken(email, null, authoritiesList);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error en auto-login tras registro: " + e.getMessage());
        }

        logger.info("Registro exitoso para usuario ID: " + usuarioId);

        redirectAttributes.addFlashAttribute("successMessage", messageService.getMessage("toast.success.registro_completado"));
        return "redirect:" + WebRoutes.HOME;
    }

    @GetMapping(WebRoutes.SOLICITUDES_DETALLE)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public String verDetalle(@PathVariable Integer id, Model model, HttpServletRequest request) {
        SolicitudAdopcionRecord sol = solicitudService.fetchSolicitudById(id);
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), sol);

        if (sol != null && sol.adoptanteId() != null) {
            try {
                AdoptanteRecord adoptante = solicitudService.fetchAdoptanteById(sol.adoptanteId());
                if (adoptante != null && adoptante.usuarioId() != null) {
                    model.addAttribute("adoptanteData", adoptante);
                    try {
                        PerfilLegalRecord perfil = solicitudService.fetchPerfilLegalByUsuarioId(adoptante.usuarioId());
                        model.addAttribute("perfilAdoptante", perfil);
                    } catch (Exception e) {
                        logger.error("Error al cargar PerfilLegal para detalle: " + e.getMessage());
                    }

                    if ("APROBADA".equals(sol.estado())) {
                        try {
                            List<AdopcionRecord> adopciones = solicitudService
                                    .fetchAdopcionesByAdoptanteId(sol.adoptanteId());
                            if (adopciones != null) {
                                String animalIdStr = String.valueOf(sol.animalId());
                                adopciones.stream()
                                        .filter(a -> animalIdStr.equals(String.valueOf(a.animalId())))
                                        .findFirst()
                                        .ifPresent(a -> model.addAttribute("adopcionId", a.id()));
                            }
                        } catch (Exception e) {
                            logger.error("Error al buscar adopción para contrato: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error al cargar adoptante para detalle: " + e.getMessage());
            }
        }

        if (sol != null && sol.animalId() != null) {
            try {
                AnimalRecord animal = solicitudService.fetchAnimalById(sol.animalId());
                model.addAttribute("animalData", animal);
            } catch (Exception e) {
                logger.error("Error al cargar animal para detalle: " + e.getMessage());
            }
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_DETALLE.getPath());

        Object userIdObj = model.getAttribute("currentUserId");
        if (userIdObj != null) {
            model.addAttribute("currentUserId", userIdObj);
        }

        if (request != null && "true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Solicitud_DETALLE.getPath() + " :: content";
        }

        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/web/solicitudes/{id}")
    public String redireccionDetalle(@PathVariable Integer id) {
        return "redirect:/web/solicitudes/" + id + "/detalle";
    }
}
