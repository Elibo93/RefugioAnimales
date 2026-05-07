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
import java.util.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;


    @GetMapping(WebRoutes.PERSONAS_NUEVO)
    public String formulario(Model model, HttpServletRequest request) {
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), new HashMap<>());
        model.addAttribute("roles", List.of("ROLE_PUBLICO", "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE", "ROLE_ADMIN"));
        
        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Persona_FORM.getPath() + " :: content";
        }
        
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_NUEVO)
    public String crearPersona(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String telefono,
            @RequestParam String contrasena,
            @RequestParam(required = false, defaultValue = "ROLE_PUBLICO") String rol,
            Model model) {

        // 1. Crear usuario en Auth
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email",      email);
        userBody.put("username",   username);
        userBody.put("contrasena", contrasena);
        userBody.put("rol",        rol);

        Map<String, Object> createdUser = (Map<String, Object>) restTemplate.postForObject(authUrl + "/v1/usuarios", userBody, Map.class);
        
        // 2. Crear PerfilLegal en Backend (Solo si se han rellenado los datos mínimos)
        if (createdUser != null && createdUser.get("id") != null) {
            if ((nombre != null && !nombre.trim().isEmpty()) || (apellido != null && !apellido.trim().isEmpty())) {
                Map<String, Object> profileBody = new HashMap<>();
                profileBody.put("usuarioId", createdUser.get("id"));
                profileBody.put("nombre",    nombre);
                profileBody.put("apellido",  apellido);
                profileBody.put("telefono",  telefono);
                profileBody.put("direccion", ""); 
                restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", profileBody, Object.class);
            }
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_CREATED.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        Map<String, Object> user = (Map<String, Object>) restTemplate.getForObject(authUrl + "/v1/usuarios/" + id, Map.class);
        Map<String, Object> persona = new HashMap<>();
        if (user != null) persona.putAll(user);
        
        try {
            Map<String, Object> legal = (Map<String, Object>) restTemplate.getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + id, Map.class);
            if (legal != null) {
                persona.put("nombre",   legal.get("nombre"));
                persona.put("apellido", legal.get("apellido"));
                persona.put("telefono", legal.get("telefono"));
            }
        } catch (Exception ignored) {}

        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);
        model.addAttribute("roles", List.of("ROLE_PUBLICO", "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE", "ROLE_ADMIN"));

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Persona_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String telefono,
            @RequestParam(required = false) String rol,
            RedirectAttributes redirectAttributes) {

        // 1. Actualizar en Auth
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", email);
        userBody.put("username", username);
        if (rol != null) userBody.put("rol", rol);
        restTemplate.put(authUrl + "/v1/usuarios/" + id, userBody);

        // 2. Actualizar PerfilLegal en Backend
        Map<String, Object> profileBody = new HashMap<>();
        profileBody.put("usuarioId", id);
        profileBody.put("nombre",    nombre);
        profileBody.put("apellido",  apellido);
        profileBody.put("telefono",  telefono);
        restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", profileBody, Object.class);
        
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
        
        List<Object> todosAuth = fetchList(authUrl + "/v1/usuarios");
        List<Object> todosLegales = fetchList(apiUrl + "/v1/perfiles-legales");
        
        // Mergear datos
        Map<String, Map<String, Object>> legalesMap = new HashMap<>();
        for (Object l : todosLegales) {
            if (l instanceof Map) {
                Map<String, Object> lm = (Map<String, Object>) l;
                legalesMap.put(String.valueOf(lm.get("usuarioId")), lm);
            }
        }

        List<Object> todos = new ArrayList<>();
        for (Object a : todosAuth) {
            if (a instanceof Map) {
                Map<String, Object> user = new HashMap<>((Map<String, Object>) a);
                Map<String, Object> legal = legalesMap.get(String.valueOf(user.get("id")));
                if (legal != null) {
                    user.put("nombre",   legal.get("nombre"));
                    user.put("apellido", legal.get("apellido"));
                    user.put("telefono", legal.get("telefono"));
                }
                todos.add(user);
            }
        }
        
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
        Map<String, Object> userAuth = (Map<String, Object>) restTemplate.getForObject(authUrl + "/v1/usuarios/" + id, Map.class);
        Map<String, Object> persona = new HashMap<>();
        if (userAuth != null) persona.putAll(userAuth);
        
        try {
            Map<String, Object> legal = (Map<String, Object>) restTemplate.getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + id, Map.class);
            if (legal != null) {
                persona.put("nombre",   legal.get("nombre"));
                persona.put("apellido", legal.get("apellido"));
                persona.put("telefono", legal.get("telefono"));
                persona.put("dni",      legal.get("dni"));
                persona.put("direccion", legal.get("direccion"));
            }
        } catch (Exception ignored) {}

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
                    // Fetch Tareas del voluntario
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
        List<Object> personasAuth = fetchList(authUrl + "/v1/usuarios");
        
        if (rol != null && !rol.isEmpty() && !"ALL".equals(rol)) {
            personasAuth = personasAuth.stream()
                .filter(u -> u instanceof Map && String.valueOf(((Map<?, ?>) u).get("rol")).equals(rol))
                .toList();
        }

        Context context = new Context();
        context.setVariable("personas", personasAuth);
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

    @GetMapping(WebRoutes.PERSONAS_BUSCAR)
    public String buscarUsuarios(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String context,
            Model model) {
            
        if (q.trim().isEmpty()) {
            model.addAttribute("usuariosEncontrados", List.of());
            return FragmentoContenido.USUARIO_SUGERENCIAS.getPath() + " :: suggestions";
        }

        List<Object> todosAuth = fetchList(authUrl + "/v1/usuarios");
        List<Object> todosLegales = fetchList(apiUrl + "/v1/perfiles-legales");
        
        Map<String, Map<String, Object>> legalesMap = new HashMap<>();
        for (Object l : todosLegales) {
            if (l instanceof Map) {
                Map<String, Object> lm = (Map<String, Object>) l;
                legalesMap.put(String.valueOf(lm.get("usuarioId")), lm);
            }
        }

        List<Map<String, Object>> todos = new ArrayList<>();
        for (Object a : todosAuth) {
            if (a instanceof Map) {
                Map<String, Object> user = new HashMap<>((Map<String, Object>) a);
                Map<String, Object> legal = legalesMap.get(String.valueOf(user.get("id")));
                if (legal != null) {
                    user.put("nombre",   legal.get("nombre"));
                    user.put("apellido", legal.get("apellido"));
                    user.put("telefono", legal.get("telefono"));
                }
                todos.add(user);
            }
        }

        String query = q.toLowerCase();
        
        // Obtener lista de IDs ya registrados según el contexto
        List<Map<String, Object>> filtrados;
        
        if ("voluntario".equalsIgnoreCase(context)) {
            List<Object> registrados = fetchList("/v1/voluntarios");
            Set<Integer> registradosIds = new HashSet<>();
            for (Object r : registrados) {
                if (r instanceof Map) {
                    Integer uid = toInteger(((Map<?, ?>) r).get("usuarioId"));
                    if (uid != null) registradosIds.add(uid);
                }
            }
            filtrados = todos.stream()
                .filter(u -> u instanceof Map)
                .map(u -> (Map<String, Object>) u)
                .filter(user -> matchesQuery(user, query))
                .limit(8)
                .map(user -> {
                    Map<String, Object> copy = new HashMap<>(user);
                    Integer uId = toInteger(user.get("id"));
                    copy.put("yaRegistrado", uId != null && registradosIds.contains(uId));
                    copy.put("adoptanteId", null);
                    return copy;
                }).toList();
        } else if ("adoptante".equalsIgnoreCase(context) || "solicitud".equalsIgnoreCase(context) || "adopcion".equalsIgnoreCase(context)) {
            List<Object> registrados = fetchList("/v1/adoptantes");
            Map<Integer, Integer> userToAdoptante = new HashMap<>();
            for (Object r : registrados) {
                if (r instanceof Map) {
                    Integer uid = toInteger(((Map<?, ?>) r).get("usuarioId"));
                    Integer aid = toInteger(((Map<?, ?>) r).get("id"));
                    if (uid != null && aid != null) {
                        userToAdoptante.put(uid, aid);
                    }
                }
            }
            filtrados = todos.stream()
                .filter(u -> u instanceof Map)
                .map(u -> (Map<String, Object>) u)
                .filter(user -> {
                    boolean matches = matchesQuery(user, query);
                    if ("solicitud".equalsIgnoreCase(context)) {
                        Integer uId = toInteger(user.get("id"));
                        return matches && uId != null && userToAdoptante.containsKey(uId);
                    }
                    return matches;
                })
                .limit(8)
                .map(user -> {
                    Map<String, Object> copy = new HashMap<>(user);
                    Integer uId = toInteger(user.get("id"));
                    Integer aId = uId != null ? userToAdoptante.get(uId) : null;
                    
                    if ("solicitud".equalsIgnoreCase(context)) {
                        copy.put("adoptanteId", aId);
                        copy.put("yaRegistrado", false); 
                    } else if ("adopcion".equalsIgnoreCase(context)) {
                        copy.put("adoptanteId", aId);
                        copy.put("yaRegistrado", false); 
                    } else {
                        copy.put("adoptanteId", aId);
                        copy.put("yaRegistrado", aId != null);
                    }
                    return copy;
                }).toList();
        } else {
            filtrados = todos.stream()
                .filter(u -> u instanceof Map)
                .map(u -> (Map<String, Object>) u)
                .filter(user -> matchesQuery(user, query))
                .limit(8)
                .map(user -> {
                    Map<String, Object> copy = new HashMap<>(user);
                    copy.put("yaRegistrado", false);
                    copy.put("adoptanteId", null);
                    return copy;
                }).toList();
        }
            
        model.addAttribute("usuariosEncontrados", filtrados);
        model.addAttribute("contexto", context);
        return FragmentoContenido.USUARIO_SUGERENCIAS.getPath() + " :: suggestions";
    }

    private Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean matchesQuery(Map<String, Object> user, String query) {
        String q = query.trim().toLowerCase();
        String id = String.valueOf(user.get("id"));
        String username = String.valueOf(user.get("username")).toLowerCase();
        String nombre = String.valueOf(user.get("nombre")).toLowerCase();
        String apellido = String.valueOf(user.get("apellido")).toLowerCase();
        String email = String.valueOf(user.get("email")).toLowerCase();
        String telefono = String.valueOf(user.get("telefono")).toLowerCase();
        
        return id.contains(q) || 
               username.contains(q) ||
               nombre.contains(q) || 
               apellido.contains(q) || 
               email.contains(q) || 
               telefono.contains(q);
    }

    private List<Object> fetchList(String path) {
        String finalUrl = path.startsWith("http") ? path : apiUrl + path;
        try {
            Object[] arr = restTemplate.getForObject(finalUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) { 
            System.err.println("ERROR: Fallo al conectar con " + finalUrl + ": " + e.getMessage());
            return List.of(); 
        }
    }
}
