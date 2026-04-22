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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdopcionViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.ADOPCIONES_BASE)
    public String listar(Model model,
            @RequestParam(required = false) Integer adoptanteId,
            @RequestParam(required = false) Integer animalId,
            @RequestParam(required = false) String successMessage) {

        List<Object> adopciones = fetchList("/v1/adopciones");
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        List<Object> animales   = fetchList("/v1/animales");
        List<Object> usuarios   = fetchList(authUrl + "/v1/usuarios");

        Map<String, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), (Map<String, Object>) u);
                }
            }
        }

        // Build adoptanteNombres: Map<adoptanteId, nombreCompleto>
        Map<String, String> adoptanteNombres = new HashMap<>();
        for (Object a : adoptantes) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                Object uid = ((Map<?, ?>) a).get("usuarioId");
                if (id instanceof Number && uid instanceof Number) {
                    Map<String, Object> user = usuariosMap.get(String.valueOf(((Number) uid).intValue()));
                    if (user != null) {
                        Object nombre = user.get("nombre");
                        Object apellido = user.get("apellido");
                        String fullName = (nombre != null ? nombre.toString() : "")
                                + (apellido != null ? " " + apellido.toString() : "");
                        adoptanteNombres.put(String.valueOf(((Number) id).intValue()), fullName.trim());
                        // Add temporary name/apellido inside 'a' map so filters can see it
                        ((Map<String, Object>) a).put("nombre", nombre);
                        ((Map<String, Object>) a).put("apellido", apellido);
                    }
                }
            }
        }

        // Build animalesMap: Map<animalId, animalObject>
        Map<String, Object> animalesMap = new HashMap<>();
        for (Object a : animales) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                if (id instanceof Number) {
                    animalesMap.put(String.valueOf(((Number) id).intValue()), a);
                }
            }
        }

        // Build adoptanteUsuarioIds: Map<adoptanteId, usuarioId>
        Map<String, String> adoptanteUsuarioIds = new HashMap<>();
        for (Object a : adoptantes) {
            if (a instanceof Map) {
                Object id = ((Map<?, ?>) a).get("id");
                Object uid = ((Map<?, ?>) a).get("usuarioId");
                if (id instanceof Number && uid instanceof Number) {
                    adoptanteUsuarioIds.put(id.toString(), uid.toString());
                }
            }
        }

        model.addAttribute(ModelAttribute.Adopcion_LIST.getName(),   adopciones);
        model.addAttribute(ModelAttribute.Persona_LIST.getName(),    usuarios);
        model.addAttribute("listaadoptantes",                        adoptantes);
        model.addAttribute("listaanimales",                          animales);
        model.addAttribute("adoptanteNombres",                       adoptanteNombres);
        model.addAttribute("adoptanteUsuarioIds",                    adoptanteUsuarioIds);
        model.addAttribute("animalesMap",                            animalesMap);
        model.addAttribute("selectedAdoptanteId", adoptanteId);
        model.addAttribute("selectedanimalId",    animalId);   // lowercase 'a' to match template

        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adopcion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ADOPCIONES_NUEVA)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), Map.of());
        model.addAttribute(ModelAttribute.Persona_LIST.getName(),    fetchList(authUrl + "/v1/usuarios"));
        model.addAttribute(ModelAttribute.Animal_LIST.getName(),     fetchList("/v1/animales"));
        model.addAttribute("estadosAdopcion", List.of("COMPLETADA", "CANCELADA", "EN_PROCESO"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adopcion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ADOPCIONES_NUEVA)
    public String crearAdopcion(@RequestParam Integer idPersona,
            @RequestParam Integer idAnimal,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("adoptanteId", idPersona);
        body.put("animalId",    idAnimal);
        body.put("estado",      estado);
        body.put("contrato",    "Contrato pendiente");

        restTemplate.postForObject(apiUrl + "/v1/adopciones", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Adopción creada correctamente");
        return "redirect:" + WebRoutes.ADOPCIONES_BASE;
    }

    @GetMapping(WebRoutes.ADOPCIONES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object adopcion = restTemplate.getForObject(apiUrl + "/v1/adopciones/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), adopcion);
        model.addAttribute(ModelAttribute.Persona_LIST.getName(),    fetchList(authUrl + "/v1/usuarios"));
        model.addAttribute(ModelAttribute.Animal_LIST.getName(),     fetchList("/v1/animales"));
        model.addAttribute("estadosAdopcion", List.of("COMPLETADA", "CANCELADA", "EN_PROCESO"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adopcion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ADOPCIONES_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer idPersona,
            @RequestParam Integer idAnimal,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("adoptanteId", idPersona);
        body.put("animalId",    idAnimal);
        body.put("estado",      estado);

        restTemplate.put(apiUrl + "/v1/adopciones/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Adopción editada correctamente");
        return "redirect:" + WebRoutes.ADOPCIONES_BASE;
    }

    @PostMapping(WebRoutes.ADOPCIONES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/adopciones/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.ADOPCIONES_BASE).build();
    }

    @GetMapping(WebRoutes.ADOPCIONES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> adopciones = fetchList("/v1/adopciones");
        Context context = new Context();
        context.setVariable("adopciones", adopciones);
        String html = templateEngine.process(ThymTemplates.Adopcion_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=adopciones.pdf");
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
        } catch (Exception e) { return List.of(); }
    }
}
