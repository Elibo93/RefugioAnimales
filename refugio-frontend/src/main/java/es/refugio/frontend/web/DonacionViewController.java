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
import es.refugio.common.util.ExcelExportHelper;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import es.refugio.frontend.web.util.ErrorMessageExtractor;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.web.client.RestClientResponseException;

/**
 * Controlador para la gestión de donaciones en la capa de vista.
 * Maneja la visualización de listas, formularios y la integración con la pasarela de pago.
 */
import es.refugio.frontend.service.DonacionService;

@Controller
@RequiredArgsConstructor
/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DonacionViewController {

    private final DonacionService donacionService;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    /**
     * Lista todas las donaciones y prepara el modelo para la vista principal.
     */
    @SuppressWarnings("rawtypes")
    @GetMapping(WebRoutes.DONACIONES_BASE)
    public String listar(Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String successMessage) {

        PaginatedResponse<DonacionRecord> pagination = donacionService.fetchPaginated(page, size);
        List<DonacionRecord> donaciones = pagination.items();
        List<UsuarioRecord> usuarios = donacionService.fetchUsuarios();
        List<Map> objetivos = donacionService.fetchObjetivos();

        // Construir mapa de usuarios para acceso rápido por ID en la vista
        Map<Integer, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(u.id(), u);
        }

        double totalDinero = donacionService.fetchTotalDinero();

        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }

        model.addAttribute(ModelAttribute.Donacion_LIST.getName(), donaciones);
        model.addAttribute("pagination", pagination);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuariosMap", usuariosMap);
        model.addAttribute("objetivos", objetivos);
        model.addAttribute("totalDinero", totalDinero);

        // Preparar objeto para el formulario de nueva donación rápida
        Map<String, Object> nuevaDonacion = new HashMap<>();
        nuevaDonacion.put("fecha", LocalDateTime.now().toString());
        nuevaDonacion.put("id", null);
        nuevaDonacion.put("frecuencia", "UNICA");
        nuevaDonacion.put("tipo", "DINERO");
        nuevaDonacion.put("cantidad", null);
        nuevaDonacion.put("descripcion", "");
        nuevaDonacion.put("objetivoId", null);

        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), nuevaDonacion);
        model.addAttribute("tipos", List.of("DINERO", "COMIDA", "MEDICINAS", "OTRO"));
        model.addAttribute("formActionUrl", "/web/donaciones/nueva");

        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Muestra el formulario completo de nueva donación.
     */
    @SuppressWarnings("rawtypes")
    @GetMapping(WebRoutes.DONACIONES_NUEVA)
    public String formulario(Model model) {
        List<UsuarioRecord> usuarios = donacionService.fetchUsuarios();
        List<Map> objetivos = donacionService.fetchObjetivos();

        Map<String, Object> nuevaDonacion = new HashMap<>();
        nuevaDonacion.put("fecha", LocalDateTime.now().toString());
        nuevaDonacion.put("id", null);
        nuevaDonacion.put("frecuencia", "UNICA");
        nuevaDonacion.put("tipo", "DINERO");
        nuevaDonacion.put("cantidad", null);
        nuevaDonacion.put("descripcion", "");
        nuevaDonacion.put("objetivoId", null);

        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), nuevaDonacion);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("objetivos", objetivos);
        model.addAttribute("tipos", List.of("DINERO", "COMIDA", "MEDICINAS", "OTRO"));
        model.addAttribute("formActionUrl", "/web/donaciones/nueva");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Procesa el envío del formulario y redirige a la pasarela de pago simulada.
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(WebRoutes.DONACIONES_NUEVA)
    public String crear(@RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer objetivoId,
            @RequestParam String tipo,
            @RequestParam Double cantidad,
            @RequestParam(defaultValue = "UNICA") String frecuencia,
            @RequestParam(required = false) String descripcion,
            Model model) {

        // Si no hay usuarioId, buscamos el usuario anónimo del sistema
        if (usuarioId == null) {
            List<UsuarioRecord> usuarios = donacionService.fetchUsuarios();
            for (UsuarioRecord u : usuarios) {
                if ("anonimo@refugio.es".equals(u.email())) {
                    usuarioId = u.id();
                    break;
                }
            }
        }

        Map<String, Object> donacionTemp = new HashMap<>();
        donacionTemp.put("usuarioId", usuarioId);
        donacionTemp.put("objetivoId", objetivoId);
        donacionTemp.put("tipo", tipo);
        donacionTemp.put("cantidad", cantidad);
        donacionTemp.put("frecuencia", frecuencia);
        donacionTemp.put("descripcion", (descripcion != null) ? descripcion : "");
        donacionTemp.put("proximaFechaPago", null);

        if ("MENSUAL".equals(frecuencia)) {
            LocalDateTime next = LocalDateTime.now().plusMonths(1);
            String formattedDate = String.format("%02d/%02d/%d", next.getDayOfMonth(), next.getMonthValue(),
                    next.getYear());
            donacionTemp.put("proximaFechaPago", formattedDate);
        }

        if (!"DINERO".equals(tipo)) {
            // Si no es dinero (comida, material, etc.), registramos directamente sin
            // pasarela
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            body.put("objetivoId", objetivoId);
            body.put("tipo", tipo);
            body.put("cantidad", cantidad);
            body.put("frecuencia", frecuencia);
            body.put("descripcion", (descripcion != null) ? descripcion : "");
            body.put("fecha", LocalDateTime.now().toString());

            try {
                donacionService.crearDonacion(body);
                model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                        FragmentoContenido.Donacion_GRACIAS.getPath());
                return ThymTemplates.MAIN_LAYOUT.getPath();
            } catch (Exception e) {
                String errorMsg = "Error al registrar la donación física.";
                if (e instanceof RestClientResponseException) {
                    RestClientResponseException re = (RestClientResponseException) e;
                    try {
                        Map<?, ?> errorMap = re.getResponseBodyAs(Map.class);
                        if (errorMap != null && errorMap.containsKey("message")) {
                            errorMsg = (String) errorMap.get("message");
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    errorMsg = e.getMessage();
                }
                model.addAttribute("errorMessage", errorMsg);
                // Volver al formulario con los datos
                model.addAttribute("donacion", donacionTemp);
                model.addAttribute("tipos", List.of("DINERO", "COMIDA", "MEDICINAS", "OTRO"));
                model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                        FragmentoContenido.Donacion_FORM.getPath());
                return ThymTemplates.MAIN_LAYOUT.getPath();
            }
        }

        model.addAttribute("donacion", donacionTemp);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Donacion_PASARELA.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Confirma el pago y persiste la donación en el backend.
     */
    @SuppressWarnings("rawtypes")
    @PostMapping("/web/donaciones/confirmar")
    public String confirmarPago(@RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer objetivoId,
            @RequestParam String tipo,
            @RequestParam Double cantidad,
            @RequestParam String frecuencia,
            @RequestParam String descripcion,
            Model model) {

        // Asegurar que tenemos un usuarioId (si sigue siendo null, buscamos el anónimo)
        if (usuarioId == null) {
            List<UsuarioRecord> usuarios = donacionService.fetchUsuarios();
            for (UsuarioRecord u : usuarios) {
                if ("anonimo@refugio.es".equals(u.email())) {
                    usuarioId = u.id();
                    break;
                }
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        body.put("objetivoId", objetivoId);
        body.put("tipo", tipo);
        body.put("cantidad", cantidad);
        body.put("frecuencia", frecuencia);
        body.put("descripcion", descripcion);
        body.put("fecha", LocalDateTime.now().toString());

        try {
            donacionService.crearDonacion(body);
        } catch (RestClientResponseException e) {
            String errorMsg = "Error al procesar la donación.";
            try {
                Map<?, ?> errorMap = e.getResponseBodyAs(Map.class);
                if (errorMap != null && errorMap.containsKey("message")) {
                    errorMsg = (String) errorMap.get("message");
                } else {
                    errorMsg = ErrorMessageExtractor.extract(e);
                }
            } catch (Exception ignored) {
                errorMsg = ErrorMessageExtractor.extract(e);
            }
            model.addAttribute("errorMessage", "No se pudo procesar el pago. " + errorMsg);
            model.addAttribute("donacion", body);
            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                    FragmentoContenido.Donacion_PASARELA.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error inesperado: " + e.getMessage());
            model.addAttribute("donacion", body);
            model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                    FragmentoContenido.Donacion_PASARELA.getPath());
            return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_GRACIAS.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Muestra el formulario para crear un nuevo objetivo de donación (Solo Admin).
     */
    @GetMapping("/web/donaciones/objetivos/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioObjetivo(Model model) {
        Map<String, Object> nuevoObjetivo = new HashMap<>();
        nuevoObjetivo.put("titulo", "");
        nuevoObjetivo.put("descripcion", "");
        nuevoObjetivo.put("montoObjetivo", 0.0);
        nuevoObjetivo.put("prioridad", "MEDIA");
        nuevoObjetivo.put("estado", "ACTIVO");
        nuevoObjetivo.put("icono", "heart");

        model.addAttribute("objetivo", nuevoObjetivo);
        model.addAttribute("prioridades", List.of("BAJA", "MEDIA", "ALTA", "CRITICA"));
        model.addAttribute("formActionUrl", "/web/donaciones/objetivos/nuevo");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/donaciones/objetivo-donacion-form");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Procesa la creación de un nuevo objetivo de donación.
     */
    @PostMapping("/web/donaciones/objetivos/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String crearObjetivo(@RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam Double montoObjetivo,
            @RequestParam String prioridad,
            @RequestParam String icono,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("titulo", titulo);
        body.put("descripcion", descripcion);
        body.put("montoObjetivo", montoObjetivo);
        body.put("prioridad", prioridad);
        body.put("estado", "ACTIVO");
        body.put("icono", icono);

        donacionService.crearObjetivo(body);
        redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.objetivo_creado"));
        return "redirect:" + WebRoutes.DONACIONES_BASE;
    }

    /**
     * Muestra el formulario de edición para una donación existente.
     */
    @SuppressWarnings("rawtypes")
    @GetMapping(WebRoutes.DONACIONES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarFormulario(@PathVariable Integer id, Model model) {
        DonacionRecord donacion = donacionService.fetchDonacionById(id);
        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), donacion);
        model.addAttribute("usuarios", donacionService.fetchUsuarios());
        model.addAttribute("objetivos", donacionService.fetchObjetivos());
        model.addAttribute("tipos", List.of("DINERO", "COMIDA", "MEDICINAS", "OTRO"));
        model.addAttribute("formActionUrl", "/web/donaciones/" + id + "/editar");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Procesa la edición de una donación.
     */
    @PostMapping(WebRoutes.DONACIONES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam(required = false) Integer objetivoId,
            @RequestParam String tipo,
            @RequestParam Double cantidad,
            @RequestParam(defaultValue = "UNICA") String frecuencia,
            @RequestParam String descripcion,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        body.put("objetivoId", objetivoId);
        body.put("tipo", tipo);
        body.put("cantidad", cantidad);
        body.put("frecuencia", frecuencia);
        body.put("descripcion", descripcion);
        body.put("fecha", LocalDateTime.now().toString());

        donacionService.editarDonacion(id, body);
        redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.donacion_editada"));
        return "redirect:" + WebRoutes.DONACIONES_BASE;
    }

    /**
     * Elimina una donación.
     */
    @PostMapping(WebRoutes.DONACIONES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        donacionService.eliminarDonacion(id);
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.DONACIONES_BASE).build();
    }

    /**
     * Genera un PDF con el listado de donaciones.
     */
    @GetMapping(WebRoutes.DONACIONES_PDF)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<DonacionRecord> donaciones = donacionService.fetchAllDonaciones();
        List<UsuarioRecord> usuarios = donacionService.fetchUsuarios();

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("donaciones", donaciones);
        context.setVariable("usuariosMap", usuariosMap);
        String html = templateEngine.process(ThymTemplates.Donacion_LIST_PDF.getPath(), context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=donaciones.pdf");

        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    @GetMapping(WebRoutes.DONACIONES_EXCEL)
    @PreAuthorize("hasRole('ADMIN')")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        List<DonacionRecord> donaciones = donacionService.fetchAllDonaciones();
        List<UsuarioRecord> usuarios = donacionService.fetchUsuarios();

        // Construir mapa de usuarios
        Map<Integer, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(u.id(), u);
        }

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
                "Donaciones",
                List.of("ID", "ID Usuario", "Donante (Email)", "ID Objetivo", "Cantidad", "Tipo", "Frecuencia",
                        "Descripción", "Fecha", "Próximo Pago"),
                donaciones,
                List.of(
                        DonacionRecord::id,
                        DonacionRecord::usuarioId,
                        d -> {
                            UsuarioRecord u = usuariosMap.get(d.usuarioId());
                            return u != null ? u.email() : "Anónimo";
                        },
                        d -> d.objetivoId() != null ? d.objetivoId() : "-",
                        DonacionRecord::cantidad,
                        DonacionRecord::tipo,
                        DonacionRecord::frecuencia,
                        d -> d.descripcion() != null ? d.descripcion() : "",
                        d -> d.fecha() != null ? d.fecha().toString() : "",
                        d -> d.proximaFechaPago() != null ? d.proximaFechaPago().toString() : "-"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=donaciones.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }
}
