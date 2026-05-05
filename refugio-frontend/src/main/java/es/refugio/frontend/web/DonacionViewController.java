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

@Controller
@RequiredArgsConstructor
public class DonacionViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.DONACIONES_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        List<Object> donaciones = fetchList("/v1/donaciones");
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        Map<Integer, Object> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    usuariosMap.put(((Number) id).intValue(), u);
                }
            }
        }

        Double totalDinero = 0.0;
        try {
            Double callRes = restTemplate.getForObject(apiUrl + "/v1/donaciones/total", Double.class);
            if (callRes != null) totalDinero = callRes;
        } catch (Exception e) {
            totalDinero = 0.0;
        }

        model.addAttribute(ModelAttribute.Donacion_LIST.getName(), donaciones);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuariosMap", usuariosMap);
        
        Map<String, Object> nuevaDonacion = new HashMap<>();
        nuevaDonacion.put("fecha", LocalDateTime.now().toString());
        nuevaDonacion.put("id", null);
        nuevaDonacion.put("frecuencia", "UNICA");
        nuevaDonacion.put("tipo", "DINERO");
        nuevaDonacion.put("cantidad", null);
        nuevaDonacion.put("descripcion", "");

        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), nuevaDonacion);
        model.addAttribute("tipos", List.of("DINERO", "ALIMENTO", "MEDICAMENTO", "MATERIAL", "OTRO"));
        model.addAttribute("metaDinero", 1000.0);
        model.addAttribute("totalDinero", totalDinero);
        model.addAttribute("formActionUrl", "/web/donaciones/nueva");

        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.DONACIONES_NUEVA)
    public String formulario(Model model) {
        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");

        Map<String, Object> nuevaDonacion = new HashMap<>();
        nuevaDonacion.put("fecha", LocalDateTime.now().toString());
        nuevaDonacion.put("id", null);
        nuevaDonacion.put("frecuencia", "UNICA");
        nuevaDonacion.put("tipo", "DINERO");
        nuevaDonacion.put("cantidad", null);
        nuevaDonacion.put("descripcion", "");
        
        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), nuevaDonacion);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("tipos", List.of("DINERO", "ALIMENTO", "MEDICAMENTO", "MATERIAL", "OTRO"));
        model.addAttribute("formActionUrl", "/web/donaciones/nueva");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.DONACIONES_NUEVA)
    public String crear(@RequestParam(required = false) Integer usuarioId,
            @RequestParam String tipo,
            @RequestParam Double cantidad,
            @RequestParam(defaultValue = "UNICA") String frecuencia,
            @RequestParam(required = false) String descripcion,
            Model model) {
        
        System.out.println("DEBUG: Entrando en DonacionViewController.crear con cantidad: " + cantidad);

        // En lugar de guardar, enviamos a la pasarela de pago (SIMULACIÓN)
        Map<String, Object> donacionTemp = new HashMap<>();
        donacionTemp.put("usuarioId", usuarioId);
        donacionTemp.put("tipo", tipo);
        donacionTemp.put("cantidad", cantidad);
        donacionTemp.put("frecuencia", frecuencia);
        donacionTemp.put("descripcion", (descripcion != null) ? descripcion : "");

        model.addAttribute("donacion", donacionTemp);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_PASARELA.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping("/web/donaciones/confirmar")
    public String confirmarPago(@RequestParam(required = false) Integer usuarioId,
            @RequestParam String tipo,
            @RequestParam Double cantidad,
            @RequestParam String frecuencia,
            @RequestParam String descripcion,
            Model model) {

        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        body.put("tipo", tipo);
        body.put("cantidad", cantidad);
        body.put("frecuencia", frecuencia);
        body.put("descripcion", descripcion);
        body.put("fecha", LocalDateTime.now().toString());

        // Ahora sí persistimos en el backend tras el "pago exitoso"
        restTemplate.postForObject(apiUrl + "/v1/donaciones", body, Object.class);
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_GRACIAS.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.DONACIONES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object donacion = restTemplate.getForObject(apiUrl + "/v1/donaciones/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), donacion);
        model.addAttribute("usuarios", fetchList(authUrl + "/v1/usuarios"));
        model.addAttribute("tipos", List.of("DINERO", "ALIMENTO", "MEDICAMENTO", "MATERIAL", "OTRO"));
        model.addAttribute("formActionUrl", "/web/donaciones/" + id + "/editar");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.DONACIONES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer usuarioId,
            @RequestParam String tipo,
            @RequestParam Double cantidad,
            @RequestParam(defaultValue = "UNICA") String frecuencia,
            @RequestParam String descripcion,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        body.put("tipo", tipo);
        body.put("cantidad", cantidad);
        body.put("frecuencia", frecuencia);
        body.put("descripcion", descripcion);
        body.put("fecha", LocalDateTime.now().toString());

        restTemplate.put(apiUrl + "/v1/donaciones/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Donación editada correctamente");
        return "redirect:" + WebRoutes.DONACIONES_BASE;
    }

    @PostMapping(WebRoutes.DONACIONES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/donaciones/" + id);
        if ("true".equals(request.getHeader("HX-Request")))
            return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.DONACIONES_BASE).build();
    }

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
