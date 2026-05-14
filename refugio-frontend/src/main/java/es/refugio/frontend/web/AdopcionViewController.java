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
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model,
            @RequestParam(required = false) Integer adoptanteId,
            @RequestParam(required = false) Integer animalId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String successMessage,
            HttpServletRequest request) {

        List<Object> adopciones = fetchList("/v1/adopciones");
        List<Object> adoptantes = fetchList("/v1/adoptantes");
        List<Object> animales   = fetchList("/v1/animales");
        List<Object> usuarios   = fetchList(authUrl + "/v1/usuarios");
        List<Object> perfiles   = fetchList("/v1/perfiles-legales"); // Real legal profiles

        Map<String, Map<String, Object>> usuariosMap = new HashMap<>();
        for (Object u : usuarios) {
            if (u instanceof Map) {
                Object id = ((Map<?, ?>) u).get("id");
                if (id instanceof Number) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> um = (Map<String, Object>) u;
                    usuariosMap.put(String.valueOf(((Number) id).intValue()), um);
                }
            }
        }

        Map<String, Map<String, Object>> perfilesMap = new HashMap<>();
        for (Object p : perfiles) {
            if (p instanceof Map) {
                Object uid = ((Map<?, ?>) p).get("usuarioId");
                if (uid instanceof Number) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pm = (Map<String, Object>) p;
                    perfilesMap.put(String.valueOf(((Number) uid).intValue()), pm);
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
                    String uidStr = String.valueOf(((Number) uid).intValue());
                    Map<String, Object> perfil = perfilesMap.get(uidStr);
                    Map<String, Object> user = usuariosMap.get(uidStr);
                    
                    String nombre = "";
                    String apellido = "";
                    
                    if (perfil != null) {
                        nombre = perfil.get("nombre") != null ? perfil.get("nombre").toString() : "";
                        apellido = perfil.get("apellido") != null ? perfil.get("apellido").toString() : "";
                    } else if (user != null) {
                        // Fallback to username if no profile found
                        nombre = user.get("username") != null ? user.get("username").toString() : "Adoptante";
                    }
                    
                    String fullName = (nombre + " " + apellido).trim();
                    adoptanteNombres.put(String.valueOf(((Number) id).intValue()), fullName);
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> am = (Map<String, Object>) a;
                    am.put("nombre", nombre);
                    am.put("apellido", apellido);
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

        // --- FILTERING LOGIC ---
        if (q != null && !q.trim().isEmpty()) {
            String query = q.toLowerCase().trim();
            adopciones = adopciones.stream()
                .filter(a -> {
                    if (!(a instanceof Map)) return false;
                    @SuppressWarnings("unchecked")
                    Map<String, Object> am = (Map<String, Object>) a;
                    
                    // Filter by Animal Name
                    Object animId = am.get("animalId");
                    if (animId != null) {
                        String animIdStr = String.valueOf(animId);
                        // Try both raw ID and integer conversion string for map lookup
                        @SuppressWarnings("unchecked")
                        Map<String, Object> animal = (Map<String, Object>) animalesMap.get(animIdStr);
                        if (animal == null && animId instanceof Number) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> animalAlt = (Map<String, Object>) animalesMap.get(String.valueOf(((Number) animId).intValue()));
                            animal = animalAlt;
                        }
                        
                        if (animal != null && animal.get("nombre") != null) {
                            if (animal.get("nombre").toString().toLowerCase().contains(query)) return true;
                        }
                    }
                    
                    // Filter by Adoptante Name
                    Object adpId = am.get("adoptanteId");
                    if (adpId != null) {
                        String adpIdStr = String.valueOf(adpId);
                        String fullName = adoptanteNombres.get(adpIdStr);
                        if (fullName == null && adpId instanceof Number) {
                            fullName = adoptanteNombres.get(String.valueOf(((Number) adpId).intValue()));
                        }
                        
                        if (fullName != null && fullName.toLowerCase().contains(query)) return true;
                    }
                    
                    // Filter by Adopcion ID
                    Object adopId = am.get("id");
                    if (adopId != null && String.valueOf(adopId).contains(query)) return true;

                    return false;
                })
                .toList();
        }

        model.addAttribute(ModelAttribute.Adopcion_LIST.getName(),   adopciones);
        model.addAttribute(ModelAttribute.Persona_LIST.getName(),    usuarios);
        model.addAttribute("listaadoptantes",                        adoptantes);
        model.addAttribute("listaanimales",                          animales);
        model.addAttribute("adoptanteNombres",                       adoptanteNombres);
        model.addAttribute("adoptanteUsuarioIds",                    adoptanteUsuarioIds);
        model.addAttribute("animalesMap",                            animalesMap);
        model.addAttribute("usuariosMap",                            usuariosMap);
        model.addAttribute("perfilesMap",                            perfilesMap);
        model.addAttribute("selectedAdoptanteId", adoptanteId);
        model.addAttribute("selectedanimalId",    animalId);
        model.addAttribute("q", q);

        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        
        if (request != null && "true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Adopcion_LIST.getPath();
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adopcion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ADOPCIONES_NUEVA)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), new HashMap<>());
        model.addAttribute("estadosAdopcion", List.of(
            Map.of("value", "PENDIENTE_FIRMA",       "label", "Pendiente de firma"),
            Map.of("value", "EN_PERIODO_ADAPTACION", "label", "En periodo de adaptación"),
            Map.of("value", "COMPLETADA",            "label", "Completada"),
            Map.of("value", "CANCELADA",             "label", "Cancelada")
        ));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adopcion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ADOPCIONES_NUEVA)
    public String crearAdopcion(@RequestParam Integer idPersona,
            @RequestParam Integer idAnimal,
            @RequestParam String estado,
            @RequestParam(required = false) String fechaAdopcion,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("adoptanteId", idPersona);
        body.put("animalId",    idAnimal);
        body.put("estado",      estado);
        body.put("fechaAdopcion", fechaAdopcion);
        body.put("contrato",    "Contrato formalizado");

        restTemplate.postForObject(apiUrl + "/v1/adopciones", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Adopción registrada correctamente");
        return "redirect:" + WebRoutes.ADOPCIONES_BASE;
    }

    @GetMapping(WebRoutes.ADOPCIONES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object adopcionObj = restTemplate.getForObject(apiUrl + "/v1/adopciones/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), adopcionObj);
        
        if (adopcionObj instanceof Map) {
            Map<String, Object> adopcion = (Map<String, Object>) adopcionObj;
            Object animalId = adopcion.get("animalId");
            Object adoptanteId = adopcion.get("adoptanteId");
            
            if (animalId != null) {
                Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Object.class);
                model.addAttribute("animalData", animal);
            }
            
            if (adoptanteId != null) {
                try {
                    Object adoptante = restTemplate.getForObject(apiUrl + "/v1/adoptantes/" + adoptanteId, Object.class);
                    if (adoptante instanceof Map) {
                        Object usuarioId = ((Map<?,?>)adoptante).get("usuarioId");
                        if (usuarioId != null) {
                            Object perfilObj = restTemplate.getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + usuarioId, Object.class);
                            if (perfilObj instanceof Map) {
                                String nombre = ((Map<?,?>)perfilObj).get("nombre") + " " + ((Map<?,?>)perfilObj).get("apellido");
                                model.addAttribute("nombreAdoptante", nombre.trim());
                            }
                        }
                    }
                } catch (Exception e) {}
            }
        }
        
        model.addAttribute("estadosAdopcion", List.of(
            Map.of("value", "PENDIENTE_FIRMA",       "label", "Pendiente de firma"),
            Map.of("value", "EN_PERIODO_ADAPTACION", "label", "En periodo de adaptación"),
            Map.of("value", "COMPLETADA",            "label", "Completada"),
            Map.of("value", "CANCELADA",             "label", "Cancelada")
        ));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adopcion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ADOPCIONES_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam Integer idPersona,
            @RequestParam Integer idAnimal,
            @RequestParam String estado,
            @RequestParam(required = false) String fechaAdopcion,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("adoptanteId", idPersona);
        body.put("animalId",    idAnimal);
        body.put("estado",      estado);
        body.put("fechaAdopcion", fechaAdopcion);

        restTemplate.put(apiUrl + "/v1/adopciones/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Adopción actualizada correctamente");
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

    @GetMapping(WebRoutes.ADOPCIONES_CONTRATO)
    public ResponseEntity<byte[]> descargarContrato(@PathVariable Integer id) {
        return restTemplate.getForEntity(apiUrl + "/v1/reports/adopcion/" + id + "/contrato", byte[].class);
    }

    private List<Object> fetchList(String path) {
        try {
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { return List.of(); }
    }
}
