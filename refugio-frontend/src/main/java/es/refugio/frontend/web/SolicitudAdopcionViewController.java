package es.refugio.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
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
import es.refugio.frontend.web.util.ViewControllerHelper;

import java.io.OutputStream;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class SolicitudAdopcionViewController {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudAdopcionViewController.class);

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.SOLICITUDES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String successMessage) {
        if (response != null) response.setHeader("Vary", "HX-Request");
        
        PaginatedResponse<SolicitudAdopcionRecord> pagination = helper.fetchPaginated(apiUrl + "/v1/solicitudes-adopcion", page, size, SolicitudAdopcionRecord.class);
        List<SolicitudAdopcionRecord> solicitudes = pagination.items();
        
        List<SolicitudAdopcionRecord> pendientes = solicitudes.stream()
                .filter(s -> "PENDIENTE".equals(s.estado()) || "EN_REVISION".equals(s.estado()))
                .toList();
        
        List<AnimalRecord> animales = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes?size=1000", AdoptanteRecord.class);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);

        Map<String, AnimalRecord> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a);
        }

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Map<String, String> adoptanteNombres = new HashMap<>();
        for (AdoptanteRecord a : adoptantes) {
            if (a.usuarioId() != null) {
                try {
                    PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + a.usuarioId(), PerfilLegalRecord.class);
                    if (perfil != null) {
                        String nombre = perfil.nombre();
                        String apellido = perfil.apellido();
                        adoptanteNombres.put(String.valueOf(a.id()),
                                ((nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "")).trim());
                    }
                } catch (Exception e) {
                    logger.warn("No se encontró PerfilLegal para adoptante con usuario " + a.usuarioId());
                }
            }
        }

        Map<String, String> adoptanteUsuarioIds = new HashMap<>();
        for (AdoptanteRecord a : adoptantes) {
            if (a.usuarioId() != null) {
                adoptanteUsuarioIds.put(String.valueOf(a.id()), a.usuarioId().toString());
            }
        }

        Map<String, String> solicitudToAdopcionMap = new HashMap<>();
        try {
            List<AdopcionRecord> allAdopciones = helper.fetchList(apiUrl + "/v1/adopciones", AdopcionRecord.class);
            if (allAdopciones != null && solicitudes != null) {
                for (SolicitudAdopcionRecord s : solicitudes) {
                    if ("APROBADA".equals(s.estado())) {
                        String key = s.adoptanteId() + "_" + s.animalId();
                        allAdopciones.stream()
                            .filter(ad -> key.equals(ad.adoptanteId() + "_" + ad.animalId()))
                            .findFirst()
                            .ifPresent(ad -> solicitudToAdopcionMap.put(String.valueOf(s.id()), String.valueOf(ad.id())));
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error al cargar mapa de adopciones para lista: " + e.getMessage());
        }

        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), solicitudes);
        model.addAttribute("pagination", pagination);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("adoptanteNombres", adoptanteNombres);
        model.addAttribute("adoptanteUsuarioIds", adoptanteUsuarioIds);
        model.addAttribute("solicitudToAdopcionMap", solicitudToAdopcionMap);
        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_BASE);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Solicitud_LIST.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ADOPTANTE')")
    @GetMapping(WebRoutes.SOLICITUDES_MIS_ADOPTADOS)
    public String misAdoptados(Model model, HttpServletRequest request, HttpServletResponse response) {
        if (response != null) response.setHeader("Vary", "HX-Request");
        
        Object userIdObj = model.getAttribute("currentUserId");
        if (userIdObj == null)
            return "redirect:/login";

        Integer currentUserId = (userIdObj instanceof Number) ? ((Number) userIdObj).intValue()
                : Integer.parseInt(userIdObj.toString());

        Integer adoptanteId = null;
        try {
            AdoptanteRecord adoptante = helper.fetchObject(apiUrl + "/v1/adoptantes/usuario/" + currentUserId, AdoptanteRecord.class);
            if (adoptante != null) {
                adoptanteId = adoptante.id();
            }
        } catch (Exception e) {
            logger.warn("El usuario " + currentUserId + " no tiene un perfil de adoptante activo.");
        }

        List<SolicitudAdopcionRecord> todas = helper.fetchList(apiUrl + "/v1/solicitudes-adopcion", SolicitudAdopcionRecord.class);
        List<SolicitudAdopcionRecord> misSolicitudes = new ArrayList<>();

        if (adoptanteId != null) {
            for (SolicitudAdopcionRecord s : todas) {
                if (s.adoptanteId() != null && s.adoptanteId().equals(adoptanteId)) {
                    misSolicitudes.add(s);
                }
            }
            
            try {
                List<AdopcionRecord> adopciones = helper.fetchList(apiUrl + "/v1/adopciones/adoptante/" + adoptanteId, AdopcionRecord.class);
                if (adopciones != null) {
                    for (AdopcionRecord adopcion : adopciones) {
                        if (adopcion.solicitudAdopcionId() == null) {
                            SolicitudAdopcionRecord fakeSolicitud = new SolicitudAdopcionRecord(
                                -adopcion.id(),
                                adopcion.animalId(),
                                adopcion.adoptanteId(),
                                adopcion.fechaAdopcion(),
                                "APROBADA",
                                "Adopción directa",
                                ""
                            );
                            misSolicitudes.add(fakeSolicitud);
                        }
                    }
                }
            } catch(Exception e) {
                logger.warn("No se pudieron obtener adopciones directas para adoptante " + adoptanteId);
            }
        }

        if (misSolicitudes.isEmpty()) {
            model.addAttribute("mensajeVacio", "Vaya, parece que aún no te has hecho con ninguno de nuestros amiguitos");
            model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.MIS_ADOPTADOS_VACIO.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        List<AnimalRecord> animales = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
        Map<String, AnimalRecord> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a);
        }

        model.addAttribute(ModelAttribute.Solicitud_LIST.getName(), misSolicitudes);
        model.addAttribute("animalesMap", animalesMap);
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_MIS_ADOPTADOS);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.MIS_ADOPTADOS_LISTA.getPath());

        if (request != null && "true".equals(request.getHeader("HX-Request"))) {
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

        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), solicitud);
        model.addAttribute("animales", helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class));
        model.addAttribute("adoptantes", helper.fetchList(apiUrl + "/v1/adoptantes?size=1000", AdoptanteRecord.class));
        model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
        model.addAttribute("currentUri", WebRoutes.SOLICITUDES_NUEVA);

        if ("true".equals(request.getHeader("HX-Request"))) {
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

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("comentario", comentario);
        body.put("comentarioAdmin", comentarioAdmin);
        body.put("fecha", LocalDateTime.now().toString());

        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion", body, Object.class);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return "fragments/content/solicitud-creada :: success-modal";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud de adopción registrada correctamente");
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping(WebRoutes.SOLICITUDES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        try {
            SolicitudAdopcionRecord sol = helper.fetchObject(apiUrl + "/v1/solicitudes-adopcion/" + id, SolicitudAdopcionRecord.class);
            model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), sol);

            if (sol != null && sol.adoptanteId() != null) {
                try {
                    AdoptanteRecord adoptante = helper.fetchObject(apiUrl + "/v1/adoptantes/" + sol.adoptanteId(), AdoptanteRecord.class);
                    if (adoptante != null && adoptante.usuarioId() != null) {
                        try {
                            PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + adoptante.usuarioId(), PerfilLegalRecord.class);
                            if (perfil != null) {
                                model.addAttribute("nombreAdoptante", perfil.nombre() + " " + perfil.apellido());
                            }
                        } catch (Exception e) {
                            logger.error("Error al cargar PerfilLegal para editar solicitud: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error al cargar datos del adoptante para editar solicitud: " + e.getMessage());
                }
            }

            if (sol != null && sol.animalId() != null) {
                try {
                    AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + sol.animalId(), AnimalRecord.class);
                    model.addAttribute("animalData", animal);
                } catch (Exception e) {
                    logger.error("Error al cargar datos del animal para editar solicitud: " + e.getMessage());
                }
            }

            model.addAttribute("animales", helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class));
            model.addAttribute("estados", List.of("PENDIENTE", "APROBADA", "RECHAZADA", "EN_REVISION"));
            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            if ("true".equals(request.getHeader("HX-Request"))) {
                return FragmentoContenido.Solicitud_FORM.getPath() + " :: content";
            }

            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_FORM.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        } catch (Throwable t) {
            logger.error("DIAGNOSTIC ERROR IN EDITAR FORMULARIO:", t);
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

        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("estado", estado);
        body.put("comentario", comentario);
        body.put("comentarioAdmin", comentarioAdmin);
        body.put("fecha", LocalDateTime.now().toString());

        restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud actualizada correctamente");
        
        if ("detalle".equals(redireccion)) {
            return "redirect:/web/solicitudes/" + id + "/detalle";
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/solicitudes-adopcion/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok()
                .header("HX-Trigger", "{\"showToast\": {\"message\": \"Solicitud eliminada correctamente\", \"type\": \"success\"}}")
                .body("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.SOLICITUDES_BASE).build();
    }

    @PostMapping(WebRoutes.SOLICITUDES_APROBAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String aprobarSolicitud(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "Solicitud aprobada y adopción registrada correctamente.";
        try {
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/" + id + "/aprobar", null, Object.class);
            redirectAttributes.addFlashAttribute("successMessage", msg);
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"success\"}, \"adoptionStatusChanged\": {}}");
            }
        } catch (Exception e) {
            logger.error("Error al aprobar solicitud: " + e.getMessage());
            String errorMsg = "Error al procesar la aprobación.";
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + errorMsg + "\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))) {
            return listar(model, request, response, 1, 10, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_RECHAZAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String rechazarSolicitud(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "La solicitud ha sido rechazada correctamente.";
        try {
            SolicitudAdopcionRecord existing = helper.fetchObject(apiUrl + "/v1/solicitudes-adopcion/" + id, SolicitudAdopcionRecord.class);
            if (existing != null) {
                Map<String, Object> body = new HashMap<>();
                body.put("animalId", existing.animalId());
                body.put("adoptanteId", existing.adoptanteId());
                body.put("comentario", existing.comentario());
                body.put("comentarioAdmin", existing.comentarioAdmin());
                body.put("fecha", existing.fecha() != null ? existing.fecha().toString() : LocalDateTime.now().toString());
                body.put("estado", "RECHAZADA");
                
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", msg);
                if ("true".equals(request.getHeader("HX-Request"))) {
                    response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"success\"}, \"adoptionStatusChanged\": {}}");
                }
            }
        } catch (Exception e) {
            logger.error("Error al rechazar solicitud: " + e.getMessage());
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"Error al rechazar la solicitud\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))) {
            return listar(model, request, response, 1, 10, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @PostMapping(WebRoutes.SOLICITUDES_REVISION)
    @PreAuthorize("hasRole('ADMIN')")
    public String ponerEnRevision(@PathVariable Integer id, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String msg = "Solicitud marcada 'En Revisión' correctamente.";
        try {
            SolicitudAdopcionRecord existing = helper.fetchObject(apiUrl + "/v1/solicitudes-adopcion/" + id, SolicitudAdopcionRecord.class);
            if (existing != null) {
                Map<String, Object> body = new HashMap<>();
                body.put("animalId", existing.animalId());
                body.put("adoptanteId", existing.adoptanteId());
                body.put("comentario", existing.comentario());
                body.put("comentarioAdmin", existing.comentarioAdmin());
                body.put("fecha", existing.fecha() != null ? existing.fecha().toString() : LocalDateTime.now().toString());
                body.put("estado", "EN_REVISION");
                
                restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
                redirectAttributes.addFlashAttribute("successMessage", msg);
                if ("true".equals(request.getHeader("HX-Request"))) {
                    response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"" + msg + "\", \"type\": \"success\"}}");
                }
            }
        } catch (Exception e) {
            logger.error("Error al poner en revisión: " + e.getMessage());
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Trigger", "{\"showToast\": {\"message\": \"Error al mover a revisión\", \"type\": \"error\"}}");
            }
        }
        if ("true".equals(request.getHeader("HX-Request"))) {
            return listar(model, request, response, 1, 10, null);
        }
        return "redirect:" + WebRoutes.SOLICITUDES_BASE;
    }

    @GetMapping(WebRoutes.SOLICITUDES_PDF)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<SolicitudAdopcionRecord> solicitudes = helper.fetchList(apiUrl + "/v1/solicitudes-adopcion", SolicitudAdopcionRecord.class);
        List<AnimalRecord> animales = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes?size=1000", AdoptanteRecord.class);
        List<PerfilLegalRecord> perfiles = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

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

        Context context = new Context(org.springframework.context.i18n.LocaleContextHolder.getLocale());
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
        List<SolicitudAdopcionRecord> solicitudes = helper.fetchList(apiUrl + "/v1/solicitudes-adopcion", SolicitudAdopcionRecord.class);
        List<AnimalRecord> animales = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes?size=1000", AdoptanteRecord.class);
        List<PerfilLegalRecord> perfiles = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

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
            List.of("ID", "ID Animal", "Animal", "ID Adoptante", "Adoptante", "Fecha", "Estado", "Comentario", "Comentario Admin"),
            solicitudes,
            List.of(
                SolicitudAdopcionRecord::id,
                SolicitudAdopcionRecord::animalId,
                s -> animalesMap.getOrDefault(String.valueOf(s.animalId()), "Animal #" + s.animalId()),
                SolicitudAdopcionRecord::adoptanteId,
                s -> adoptanteNombres.getOrDefault(String.valueOf(s.adoptanteId()), "Adoptante #" + s.adoptanteId()),
                s -> s.fecha() != null ? s.fecha().toString() : "",
                SolicitudAdopcionRecord::estado,
                s -> s.comentario() != null ? s.comentario() : "",
                s -> s.comentarioAdmin() != null ? s.comentarioAdmin() : ""
            )
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @GetMapping(WebRoutes.SOLICITUDES_PUBLICO_REGISTRO)
    public String formularioPublico(Model model, @RequestParam Integer animalId) {
        try {
            AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + animalId, AnimalRecord.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "", null, 0.0, 0, false, 0, 0));
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
            AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + animalId, AnimalRecord.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "", null, 0.0, 0, false, 0, 0));
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Solicitud_OPCIONES.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.SOLICITUDES_CONVERTIR)
    public String formularioConversion(Model model, @RequestParam Integer animalId) {
        try {
            AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + animalId, AnimalRecord.class);
            model.addAttribute("animal", animal);
        } catch (Exception e) {
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "", null, 0.0, 0, false, 0, 0));
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
                UsuarioRecord me = helper.fetchObject(authUrl + "/v1/me", UsuarioRecord.class);
                if (me != null) {
                    Integer currentUserId = me.id();
                    PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + currentUserId, PerfilLegalRecord.class);
                    if (perfil != null) {
                        model.addAttribute("nombre", perfil.nombre() != null ? perfil.nombre() : "");
                        model.addAttribute("apellido", perfil.apellido() != null ? perfil.apellido() : "");
                        model.addAttribute("telefono", perfil.telefono() != null ? perfil.telefono() : "");
                        model.addAttribute("dni", perfil.dni() != null ? perfil.dni() : "");
                        model.addAttribute("direccion", perfil.direccion() != null ? perfil.direccion() : "");
                        model.addAttribute("fechaNacimiento", perfil.fechaNacimiento() != null ? perfil.fechaNacimiento() : "");
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
            AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + animalId, AnimalRecord.class);
            model.addAttribute("animal", animal);

            Set<Integer> solicitados = (Set<Integer>) model.getAttribute("animalesSolicitadosIds");
            if (solicitados != null && solicitados.contains(animalId)) {
                return "redirect:" + WebRoutes.HOME + "?errorMessage=Ya tienes una solicitud pendiente para este animal.";
            }

            UsuarioRecord me = helper.fetchObject(authUrl + "/v1/me", UsuarioRecord.class);
            if (me != null) {
                Integer currentUserId = me.id();
                try {
                    PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + currentUserId, PerfilLegalRecord.class);
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
            model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "", null, 0.0, 0, false, 0, 0));
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

        UsuarioRecord me = helper.fetchObject(authUrl + "/v1/me", UsuarioRecord.class);
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
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/convertir-y-adopcion", bodySolicitud, Object.class);
        } catch (org.springframework.web.client.RestClientResponseException e) {
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
                AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + animalId, AnimalRecord.class);
                model.addAttribute("animal", animal);
            } catch (Exception ignored) {
                model.addAttribute("animal", new AnimalRecord(animalId, "Animal", "", "", "", "", "", "", 0, "", "", "", null, 0.0, 0, false, 0, 0));
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

                    var responseEntity = restTemplate.exchange(
                            authUrl + "/v1/usuarios/" + usuarioIdAuth + "/rol",
                            HttpMethod.PUT,
                            new HttpEntity<>(patchBody),
                            new ParameterizedTypeReference<Map<String, Object>>() {});

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
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/directa", body, Object.class);
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debe iniciar sesión para realizar esta acción.");
            return "redirect:" + WebRoutes.SOLICITUDES_OPCIONES + "?animalId=" + animalId;
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String errorMsg = "Error al procesar la solicitud.";
            try {
                Map<?, ?> errorMap = e.getResponseBodyAs(Map.class);
                if (errorMap != null && errorMap.containsKey("message")) {
                    errorMsg = (String) errorMap.get("message");
                }
            } catch (Exception ignored) {}
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:" + WebRoutes.SOLICITUDES_DIRECTA_FORM + "?animalId=" + animalId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Solicitud enviada con éxito");
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
            Map<?, ?> respUser = restTemplate.postForObject(authUrl + "/v1/usuarios/publico", userBody, Map.class);
            if (respUser != null && respUser.get("id") != null) {
                Object rawId = respUser.get("id");
                usuarioId = (rawId instanceof Number) ? ((Number) rawId).intValue()
                        : Integer.parseInt(rawId.toString());
            } else {
                throw new Exception("No se pudo obtener el ID del usuario tras el registro");
            }
        } catch (Exception e) {
            String errorMsg = "Error inesperado al contactar con el servicio de autenticación.";
            if (e instanceof org.springframework.web.client.RestClientResponseException) {
                var ex = (org.springframework.web.client.RestClientResponseException) e;
                try {
                    Map<?, ?> errorMap = ex.getResponseBodyAs(Map.class);
                    if (errorMap != null) {
                        if (errorMap.containsKey("message")) {
                            errorMsg = (String) errorMap.get("message");
                        } else if (errorMap.containsKey("username")) {
                            errorMsg = (String) errorMap.get("username");
                        } else if (errorMap.containsKey("error")) {
                            errorMsg = (String) errorMap.get("error");
                        }
                    } else {
                        errorMsg = ex.getStatusText();
                    }
                } catch (Exception ignored) {
                    errorMsg = ex.getMessage();
                }
            } else {
                errorMsg = e.getMessage();
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
            restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/publico/registro-y-adopcion", bodySolicitud, Object.class);
        } catch (Exception e) {
            String errorMsg = "Error inesperado al procesar la adopción en el backend.";
            if (e instanceof org.springframework.web.client.RestClientResponseException) {
                var ex = (org.springframework.web.client.RestClientResponseException) e;
                try {
                    Map<?, ?> errorMap = ex.getResponseBodyAs(Map.class);
                    if (errorMap != null && errorMap.containsKey("message")) {
                        errorMsg = (String) errorMap.get("message");
                    } else {
                        errorMsg = ex.getStatusText();
                    }
                } catch (Exception ignored) {
                    errorMsg = ex.getMessage();
                }
            } else {
                errorMsg = e.getMessage();
            }

            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:" + WebRoutes.SOLICITUDES_PUBLICO_REGISTRO + "?animalId=" + animalId;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

            String loginBody = "username=" + email + "&password=" + password;
            HttpEntity<String> entity = new HttpEntity<>(loginBody, headers);

            String authBaseUrl = authUrl.substring(0, authUrl.lastIndexOf("/api"));
            String loginUrl = authBaseUrl + "/login-post";

            ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);

            List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                for (String cookieStr : cookies) {
                    if (cookieStr.startsWith("JWT_TOKEN=")) {
                        String value = cookieStr.substring(10, cookieStr.indexOf(";"));
                        Cookie cookie = new Cookie("JWT_TOKEN", value);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(86400);
                        response.addCookie(cookie);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error en auto-login tras registro: " + e.getMessage());
        }

        logger.info("Registro exitoso para usuario ID: " + usuarioId);

        redirectAttributes.addFlashAttribute("successMessage", "¡Registro y solicitud completados!");
        return "redirect:" + WebRoutes.HOME;
    }

    @GetMapping(WebRoutes.SOLICITUDES_DETALLE)
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public String verDetalle(@PathVariable Integer id, Model model, HttpServletRequest request) {
        SolicitudAdopcionRecord sol = helper.fetchObject(apiUrl + "/v1/solicitudes-adopcion/" + id, SolicitudAdopcionRecord.class);
        model.addAttribute(ModelAttribute.SINGLE_Solicitud.getName(), sol);

        if (sol != null && sol.adoptanteId() != null) {
            try {
                AdoptanteRecord adoptante = helper.fetchObject(apiUrl + "/v1/adoptantes/" + sol.adoptanteId(), AdoptanteRecord.class);
                if (adoptante != null && adoptante.usuarioId() != null) {
                    model.addAttribute("adoptanteData", adoptante);
                    try {
                        PerfilLegalRecord perfil = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + adoptante.usuarioId(), PerfilLegalRecord.class);
                        model.addAttribute("perfilAdoptante", perfil);
                    } catch (Exception e) {
                        logger.error("Error al cargar PerfilLegal para detalle: " + e.getMessage());
                    }

                    if ("APROBADA".equals(sol.estado())) {
                        try {
                            List<AdopcionRecord> adopciones = helper.fetchList(apiUrl + "/v1/adopciones/adoptante/" + sol.adoptanteId(), AdopcionRecord.class);
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
                AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + sol.animalId(), AnimalRecord.class);
                model.addAttribute("animalData", animal);
            } catch (Exception e) {
                logger.error("Error al cargar animal para detalle: " + e.getMessage());
            }
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Solicitud_DETALLE.getPath());
        
        Object userIdObj = model.getAttribute("currentUserId");
        if (userIdObj != null) {
            model.addAttribute("currentUserId", userIdObj);
        }

        if (request != null && "true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Solicitud_DETALLE.getPath() + " :: content";
        }
        
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/web/solicitudes/{id}")
    public String redireccionDetalle(@PathVariable Integer id) {
        return "redirect:/web/solicitudes/" + id + "/detalle";
    }
}
