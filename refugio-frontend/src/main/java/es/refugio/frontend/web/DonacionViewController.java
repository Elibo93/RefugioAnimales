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

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controlador para la gestión de donaciones en la capa de vista.
 * Maneja la visualización de listas, formularios y la integración con la
 * pasarela de pago.
 */
@Controller
@RequiredArgsConstructor
public class DonacionViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    /**
     * Lista todas las donaciones y prepara el modelo para la vista principal.
     */
    @GetMapping(WebRoutes.DONACIONES_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        List<Object> donaciones = fetchList("/v1/donaciones");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
        List<Object> objetivos = fetchList("/v1/objetivos-donacion");

        // Construir mapa de usuarios para acceso rápido por ID en la vista
        Map<Integer, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object idObj = ((Map<?, ?>) u).get("id");
                if (idObj instanceof Number) {
                    usuariosMap.put(((Number) idObj).intValue(), u);
                }
            }
        }

        double totalDinero = 0;
        try {
            Double total = restTemplate.getForObject(apiUrl + "/v1/donaciones/total", Double.class);
            totalDinero = total != null ? total : 0;
        } catch (Exception e) {
        }

        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }

        model.addAttribute(ModelAttribute.Donacion_LIST.getName(), donaciones);
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
        model.addAttribute("tipos", List.of("DINERO", "ALIMENTO", "MEDICAMENTO", "MATERIAL", "OTRO"));
        model.addAttribute("formActionUrl", "/web/donaciones/nueva");

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Muestra el formulario completo de nueva donación.
     */
    @GetMapping(WebRoutes.DONACIONES_NUEVA)
    public String formulario(Model model) {
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
        List<Object> objetivos = fetchList("/v1/objetivos-donacion");

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
        model.addAttribute("tipos", List.of("DINERO", "ALIMENTO", "MEDICAMENTO", "MATERIAL", "OTRO"));
        model.addAttribute("formActionUrl", "/web/donaciones/nueva");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Procesa el envío del formulario y redirige a la pasarela de pago simulada.
     */
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
            List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
            for (Object u : usuarios) {
                if (u instanceof Map && "anonimo@refugio.es".equals(((Map<?, ?>) u).get("email"))) {
                    usuarioId = (Integer) ((Map<?, ?>) u).get("id");
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
        donacionTemp.put("proximaFechaPago", null); // Asegurar que la clave existe para evitar errores en Thymeleaf

        if ("MENSUAL".equals(frecuencia)) {
            LocalDateTime next = LocalDateTime.now().plusMonths(1);
            String formattedDate = String.format("%02d/%02d/%d", next.getDayOfMonth(), next.getMonthValue(),
                    next.getYear());
            donacionTemp.put("proximaFechaPago", formattedDate);
        }

        model.addAttribute("donacion", donacionTemp);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                FragmentoContenido.Donacion_PASARELA.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    /**
     * Confirma el pago y persiste la donación en el backend.
     */
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
            List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
            for (Object u : usuarios) {
                if (u instanceof Map && "anonimo@refugio.es".equals(((Map<?, ?>) u).get("email"))) {
                    usuarioId = (Integer) ((Map<?, ?>) u).get("id");
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

        restTemplate.postForObject(apiUrl + "/v1/donaciones", body, Object.class);

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
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/objetivo-donacion-form");
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

        restTemplate.postForObject(apiUrl + "/v1/objetivos-donacion", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Nuevo objetivo de donación creado");
        return "redirect:" + WebRoutes.DONACIONES_BASE;
    }

    /**
     * Muestra el formulario de edición para una donación existente.
     */
    @GetMapping(WebRoutes.DONACIONES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object donacion = restTemplate.getForObject(apiUrl + "/v1/donaciones/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), donacion);
        model.addAttribute("usuarios", fetchList(authUrl + "/v1/usuarios"));
        model.addAttribute("objetivos", fetchList("/v1/objetivos-donacion"));
        model.addAttribute("tipos", List.of("DINERO", "ALIMENTO", "MEDICAMENTO", "MATERIAL", "OTRO"));
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

        restTemplate.put(apiUrl + "/v1/donaciones/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Donación editada correctamente");
        return "redirect:" + WebRoutes.DONACIONES_BASE;
    }

    /**
     * Elimina una donación.
     */
    @PostMapping(WebRoutes.DONACIONES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/donaciones/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) {
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
        List<Object> donaciones = fetchList("/v1/donaciones");
        Context context = new Context();
        context.setVariable("donaciones", donaciones);
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

    /**
     * Método auxiliar para obtener listas desde APIs externas o internas.
     */
    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
