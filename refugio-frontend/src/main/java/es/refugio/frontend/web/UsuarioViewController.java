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
import java.util.*;

@Controller
@RequiredArgsConstructor
public class UsuarioViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;


    @GetMapping(WebRoutes.PERSONAS_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), Map.of());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_NUEVO)
    public String crearPersona(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            Model model) {

        Map<String, Object> body = new HashMap<>();
        body.put("nombre",    nombre);    body.put("apellido",   apellido);
        body.put("email",     email);     body.put("telefono",   telefono);
        body.put("contrasena", contrasena); body.put("rol", "ROLE_PUBLICO");

        restTemplate.postForObject(authUrl + "/v1/usuarios", body, Object.class);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_CREATED.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object persona = restTemplate.getForObject(authUrl + "/v1/usuarios/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("nombre",   nombre);   body.put("apellido", apellido);
        body.put("email",    email);    body.put("telefono", telefono);

        restTemplate.put(authUrl + "/v1/usuarios/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Persona editada correctamente");
        return "redirect:" + WebRoutes.PERSONAS_BASE;
    }

    @PostMapping(WebRoutes.PERSONAS_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(authUrl + "/v1/usuarios/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.PERSONAS_BASE).build();
    }

    @GetMapping(WebRoutes.PERSONAS_BASE)
    public String listar(Model model, 
                        @RequestParam(required = false) String rol,
                        @RequestParam(required = false) String successMessage) {
        
        List<Object> todos = fetchList(authUrl + "/v1/usuarios");
        
        // Por defecto, si no hay parámetro, mostramos TODOS los usuarios
        String activeRol = (rol == null || rol.trim().isEmpty()) ? "ALL" : rol.trim();
        List<Object> filtrados = todos;

        if (!"ALL".equalsIgnoreCase(activeRol)) {
            filtrados = todos.stream()
                .filter(u -> {
                    if (u instanceof Map) {
                        Object r = ((Map<?, ?>) u).get("rol");
                        if (r == null) return false;
                        String userRol = String.valueOf(r).toUpperCase();
                        String target = activeRol.toUpperCase();
                        // Coincidencia exacta o contenida (ej: ROLE_PUBLICO contiene PUBLICO)
                        return userRol.equals(target) || userRol.contains(target.replace("ROLE_", "")) || target.contains(userRol.replace("ROLE_", ""));
                    }
                    return false;
                })
                .toList();
        }

        model.addAttribute(ModelAttribute.Persona_LIST.getName(), filtrados);
        model.addAttribute("selectedRol", activeRol);
        
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_DETALLE)
    public String verDetalle(@PathVariable Integer id, Model model) {
        Object persona = restTemplate.getForObject(authUrl + "/v1/usuarios/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);

        // Fetch Adoptante info
        try {
            Object adoptante = restTemplate.getForObject(apiUrl + "/v1/adoptantes/usuario/" + id, Object.class);
            model.addAttribute("adoptante", adoptante);
            
            if (adoptante instanceof Map) {
                Object aId = ((Map<?, ?>) adoptante).get("id");
                if (aId != null) {
                    // Fetch Adopciones
                    model.addAttribute("adopciones", fetchList("/v1/adopciones/adoptante/" + aId));
                }
            }
        } catch (Exception ignored) {}

        // Fetch Voluntario info
        try {
            Object voluntario = restTemplate.getForObject(apiUrl + "/v1/voluntarios/usuario/" + id, Object.class);
            model.addAttribute("voluntario", voluntario);
            
            if (voluntario instanceof Map) {
                Object vId = ((Map<?, ?>) voluntario).get("id");
                if (vId != null) {
                    // Fetch Tareas del voluntario (necesitamos filtrar en frontend o backend)
                    List<Object> todasTareas = fetchList("/v1/tareas");
                    List<Object> misTareas = todasTareas.stream()
                        .filter(t -> {
                            if (t instanceof Map) {
                                Object vIds = ((Map<?, ?>) t).get("voluntarioIds");
                                if (vIds instanceof List) return ((List<?>) vIds).contains(vId);
                            }
                            return false;
                        }).toList();
                    model.addAttribute("tareas", misTareas);
                }
            }
        } catch (Exception ignored) {}

        // 1. Cargar nombres de animales para referencia rápida
        Map<String, String> animalNames = new HashMap<>();
        try {
            List<Object> animalesArr = fetchList("/v1/animales");
            if (animalesArr != null) {
                for (Object a : animalesArr) {
                    if (a instanceof Map) {
                        Map<?, ?> am = (Map<?, ?>) a;
                        animalNames.put(String.valueOf(am.get("id")), String.valueOf(am.get("nombre")));
                    }
                }
            }
        } catch (Exception ignored) {}
        model.addAttribute("animalNames", animalNames);

        // 2. Fetch Adoptante and its links (Adopciones/Solicitudes)
        List<Map<String, Object>> vinculos = new ArrayList<>();
        try {
            Object adoptante = restTemplate.getForObject(apiUrl + "/v1/adoptantes/usuario/" + id, Object.class);
            model.addAttribute("adoptante", adoptante);
            
            if (adoptante instanceof Map) {
                Map<?, ?> adoptanteMap = (Map<?, ?>) adoptante;
                Object aId = adoptanteMap.get("id");
                
                if (aId != null) {
                    // Cargar Adopciones
                    List<Object> adps = fetchList("/v1/adopciones/adoptante/" + aId);
                    if (adps != null) {
                        for (Object o : adps) {
                            if (o instanceof Map) {
                                Map<String, Object> v = new HashMap<>((Map<String, Object>) o);
                                v.put("tipoVinculo", "ADOPCIÓN");
                                v.put("estadoVinculo", "ADOPTADO");
                                v.put("fechaVinculo", v.get("fechaAdopcion"));
                                vinculos.add(v);
                            }
                        }
                    }
                    
                    // Cargar Solicitudes
                    List<Object> sols = fetchList("/v1/solicitudes-adopcion/adoptante/" + aId);
                    if (sols != null) {
                        for (Object o : sols) {
                            if (o instanceof Map) {
                                Map<String, Object> v = new HashMap<>((Map<String, Object>) o);
                                v.put("tipoVinculo", "SOLICITUD");
                                v.put("estadoVinculo", v.get("estado"));
                                v.put("fechaVinculo", v.get("fecha"));
                                
                                boolean yaExiste = vinculos.stream().anyMatch(existing -> 
                                    String.valueOf(existing.get("animalId")).equals(String.valueOf(v.get("animalId"))) &&
                                    "ADOPTADO".equals(existing.get("estadoVinculo"))
                                );
                                if (!yaExiste) vinculos.add(v);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        // Ordenar y añadir al modelo
        vinculos.sort((v1, v2) -> {
            String f1 = String.valueOf(v1.get("fechaVinculo") != null ? v1.get("fechaVinculo") : "");
            String f2 = String.valueOf(v2.get("fechaVinculo") != null ? v2.get("fechaVinculo") : "");
            return f2.compareTo(f1);
        });
        model.addAttribute("vinculosAnimales", vinculos);

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_DETALLE.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_PDF)
    public void exportarPDF(HttpServletResponse response, @RequestParam(required = false) String rol) throws Exception {
        List<Object> personas = fetchList(authUrl + "/v1/usuarios");
        
        if (rol != null && !rol.isEmpty() && !"ALL".equals(rol)) {
            personas = personas.stream()
                .filter(u -> u instanceof Map && String.valueOf(((Map<?, ?>) u).get("rol")).equals(rol))
                .toList();
        }

        Context context = new Context();
        context.setVariable("personas", personas);
        String html = templateEngine.process(ThymTemplates.Persona_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");
        OutputStream out = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

    private List<Object> fetchList(String path) {
        try {
            // Si el path ya es absoluto (empieza por http), no prependemos apiUrl.
            String finalUrl = path.startsWith("http") ? path : apiUrl + path;
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { return List.of(); }
    }
}
