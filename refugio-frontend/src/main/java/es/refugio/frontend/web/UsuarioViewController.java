package es.refugio.frontend.web;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioViewController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioViewController.class);

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${auth.api.url}")
    private String authUrl;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping(WebRoutes.PERSONAS_BASE)
    public String listarPersonas(@RequestParam(required = false) String q,
            @RequestParam(required = false) String rol,
            Model model, HttpServletRequest request) {

        List<Object> personasAuth = fetchList(authUrl + "/v1/usuarios");
        List<Object> perfilesLegales = fetchList(apiUrl + "/v1/perfiles-legales");

        Map<Integer, Map<String, Object>> perfilesMap = new HashMap<>();
        for (Object p : perfilesLegales) {
            if (p instanceof Map) {
                Map<String, Object> pm = (Map<String, Object>) p;
                Object uId = pm.get("usuarioId");
                if (uId instanceof Number)
                    perfilesMap.put(((Number) uId).intValue(), pm);
            }
        }

        List<Map<String, Object>> personasCompletas = new ArrayList<>();
        String query = q != null ? q.toLowerCase() : null;

        for (Object u : personasAuth) {
            if (u instanceof Map) {
                Map<String, Object> um = (Map<String, Object>) u;
                Integer id = ((Number) um.get("id")).intValue();

                Map<String, Object> persona = new HashMap<>(um);
                Map<String, Object> perfil = perfilesMap.get(id);

                if (perfil != null) {
                    persona.put("nombre", perfil.get("nombre"));
                    persona.put("apellido", perfil.get("apellido"));
                    persona.put("dni", perfil.get("dni"));
                    persona.put("telefono", perfil.get("telefono"));
                    persona.put("direccion", perfil.get("direccion"));
                }

                // Filtrado por rol
                if (rol != null && !rol.isEmpty() && !"ALL".equals(rol)) {
                    if (!String.valueOf(um.get("rol")).equals(rol))
                        continue;
                }

                // Filtrado por búsqueda
                if (query != null && !query.isEmpty()) {
                    String nombre = String.valueOf(persona.get("nombre") != null ? persona.get("nombre") : "")
                            .toLowerCase();
                    String apellido = String.valueOf(persona.get("apellido") != null ? persona.get("apellido") : "")
                            .toLowerCase();
                    String email = String.valueOf(persona.get("email")).toLowerCase();
                    String username = String.valueOf(persona.get("username")).toLowerCase();

                    if (!nombre.contains(query) && !apellido.contains(query) &&
                            !email.contains(query) && !username.contains(query)) {
                        continue;
                    }
                }

                personasCompletas.add(persona);
            }
        }

        model.addAttribute(ModelAttribute.Persona_LIST.getName(), personasCompletas);
        model.addAttribute("roles", List.of("ROLE_PUBLICO", "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE", "ROLE_ADMIN"));
        model.addAttribute("selectedRol", rol);
        model.addAttribute("query", q);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Persona_LIST.getPath() + " :: list-body";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.PERSONAS_NUEVO)
    public String crearPersonaForm(Model model, HttpServletRequest request) {
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), new HashMap<String, Object>());
        model.addAttribute("roles", List.of("ROLE_PUBLICO", "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE", "ROLE_ADMIN"));

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Persona_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.PERSONAS_NUEVO)
    public String procesarCreacion(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String contrasena,
            @RequestParam String rol,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fechaNacimiento,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userBody = new HashMap<>();
        userBody.put("username", username);
        userBody.put("email", email);
        userBody.put("contrasena", contrasena);
        userBody.put("rol", rol);

        try {
            Map<String, Object> createdUser = restTemplate.postForObject(authUrl + "/v1/usuarios", userBody, Map.class);
            if (createdUser != null && createdUser.get("id") != null) {
                Integer usuarioId = ((Number) createdUser.get("id")).intValue();

                // Crear PerfilLegal
                Map<String, Object> legalBody = new HashMap<>();
                legalBody.put("usuarioId", usuarioId);
                legalBody.put("nombre", nombre);
                legalBody.put("apellido", apellido);
                legalBody.put("dni", dni);
                legalBody.put("telefono", telefono);
                legalBody.put("direccion", direccion);
                legalBody.put("fechaNacimiento",
                        (fechaNacimiento != null && !fechaNacimiento.isEmpty()) ? fechaNacimiento : "2000-01-01");

                restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", legalBody, Object.class);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear usuario: " + e.getMessage());
        }

        return "redirect:" + WebRoutes.PERSONAS_BASE;
    }

    @GetMapping(WebRoutes.PERSONAS_EDITAR)
    public String editarPersonaForm(@PathVariable Integer id, Model model, HttpServletRequest request) {
        Map<String, Object> user = restTemplate.getForObject(authUrl + "/v1/usuarios/" + id, Map.class);
        Map<String, Object> persona = new HashMap<>();
        if (user != null)
            persona.putAll(user);

        try {
            Map<String, Object> legal = (Map<String, Object>) restTemplate
                    .getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + id, Map.class);
            if (legal != null) {
                persona.put("nombre", legal.get("nombre"));
                persona.put("apellido", legal.get("apellido"));
                persona.put("telefono", legal.get("telefono"));
                persona.put("dni", legal.get("dni"));
                persona.put("direccion", legal.get("direccion"));
                persona.put("fechaNacimiento", legal.get("fechaNacimiento"));
            }
        } catch (Exception ignored) {
        }

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
            @RequestParam String dni,
            @RequestParam String fechaNacimiento,
            @RequestParam(required = false) String rol,
            RedirectAttributes redirectAttributes) {

        // 1. Actualizar en Auth
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", email);
        userBody.put("username", username);
        if (rol != null)
            userBody.put("rol", rol);
        restTemplate.put(authUrl + "/v1/usuarios/" + id, userBody);

        // 2. Actualizar PerfilLegal
        Map<String, Object> legalBody = new HashMap<>();
        legalBody.put("usuarioId", id);
        legalBody.put("nombre", nombre);
        legalBody.put("apellido", apellido);
        legalBody.put("dni", dni);
        legalBody.put("telefono", telefono);
        legalBody.put("fechaNacimiento", fechaNacimiento);
        restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", legalBody, Object.class);

        redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado con éxito");
        return "redirect:" + WebRoutes.PERSONAS_BASE;
    }

    @PostMapping(WebRoutes.PERSONAS_ELIMINAR)
    public String borrar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Iniciando borrado coordinado para usuario ID: {}", id);

            // 1. Borrar PerfilLegal en el Backend primero (integridad)
            try {
                restTemplate.delete(apiUrl + "/v1/perfiles-legales/usuario/" + id);
                logger.info("PerfilLegal eliminado con éxito para usuario {}", id);
            } catch (Exception e) {
                logger.warn("No se pudo eliminar el PerfilLegal del usuario {} o no existía. Continuando...", id);
            }

            // 2. Borrar Usuario en Auth
            restTemplate.delete(authUrl + "/v1/usuarios/" + id);
            logger.info("Usuario ID {} eliminado con éxito de Auth", id);

            redirectAttributes.addFlashAttribute("successMessage", "Usuario y datos legales eliminados con éxito");
        } catch (Exception e) {
            logger.error("Error crítico al borrar usuario {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al borrar usuario: " + e.getMessage());
        }
        return "redirect:" + WebRoutes.PERSONAS_BASE;
    }

    @GetMapping(WebRoutes.PERSONAS_DETALLE)
    public String verDetalle(@PathVariable Integer id, Model model) {
        Map<String, Object> userAuth = (Map<String, Object>) restTemplate.getForObject(authUrl + "/v1/usuarios/" + id,
                Map.class);
        Map<String, Object> persona = new HashMap<>();
        if (userAuth != null)
            persona.putAll(userAuth);

        try {
            Map<String, Object> legal = (Map<String, Object>) restTemplate
                    .getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + id, Map.class);
            if (legal != null) {
                persona.put("nombre", legal.get("nombre"));
                persona.put("apellido", legal.get("apellido"));
                persona.put("telefono", legal.get("telefono"));
                persona.put("dni", legal.get("dni"));
                persona.put("direccion", legal.get("direccion"));
                persona.put("fechaNacimiento", legal.get("fechaNacimiento"));
            }
        } catch (Exception ignored) {
        }

        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);

        // Fetch Adoptante info & Animal Links
        try {
            Object adoptante = restTemplate.getForObject(apiUrl + "/v1/adoptantes/usuario/" + id, Object.class);
            model.addAttribute("adoptante", adoptante);
            if (adoptante instanceof Map) {
                Map<String, Object> am = (Map<String, Object>) adoptante;
                Object aIdRaw = am.get("id");
                Integer aId = (aIdRaw instanceof Map) ? (Integer) ((Map) aIdRaw).get("value") : (Integer) aIdRaw;

                if (aId != null) {
                    List<Object> solicitudes = fetchList(apiUrl + "/v1/solicitudes-adopcion/adoptante/" + aId);
                    List<Object> adopciones = fetchList(apiUrl + "/v1/adopciones/adoptante/" + aId);

                    List<Map<String, Object>> vinculos = new ArrayList<>();
                    Map<String, String> animalNames = new HashMap<>();

                    for (Object s : solicitudes) {
                        if (s instanceof Map) {
                            Map<String, Object> sm = (Map<String, Object>) s;
                            Map<String, Object> v = new HashMap<>();
                            v.put("id", sm.get("id"));
                            v.put("animalId", sm.get("animalId"));
                            v.put("tipoVinculo", "SOLICITUD");
                            v.put("estadoVinculo", sm.get("estado"));
                            v.put("fechaVinculo", sm.get("fechaSolicitud"));
                            vinculos.add(v);
                            fetchAnimalName(sm.get("animalId"), animalNames);
                        }
                    }
                    for (Object ad : adopciones) {
                        if (ad instanceof Map) {
                            Map<String, Object> adm = (Map<String, Object>) ad;
                            Map<String, Object> v = new HashMap<>();
                            v.put("id", adm.get("id"));
                            v.put("animalId", adm.get("animalId"));
                            v.put("tipoVinculo", "ADOPCIÓN");
                            v.put("estadoVinculo", "FINALIZADA");
                            v.put("fechaVinculo", adm.get("fechaAdopcion"));
                            vinculos.add(v);
                            fetchAnimalName(adm.get("animalId"), animalNames);
                        }
                    }
                    model.addAttribute("vinculosAnimales", vinculos);
                    model.addAttribute("animalNames", animalNames);
                }
            }
        } catch (Exception ignored) {
        }

        // Fetch Voluntario info
        try {
            Object voluntario = restTemplate.getForObject(apiUrl + "/v1/voluntarios/usuario/" + id, Object.class);
            model.addAttribute("voluntario", voluntario);

            if (voluntario instanceof Map) {
                Map<String, Object> vm = (Map<String, Object>) voluntario;
                Object vIdRaw = vm.get("id");
                Integer vId = (vIdRaw instanceof Map) ? (Integer) ((Map) vIdRaw).get("value") : (Integer) vIdRaw;

                if (vId != null) {
                    List<Object> todasTareas = fetchList(apiUrl + "/v1/tareas");
                    List<Object> misTareas = todasTareas.stream()
                            .filter(t -> {
                                if (t instanceof Map) {
                                    Object vIds = ((Map<?, ?>) t).get("voluntarioIds");
                                    if (vIds instanceof List) {
                                        return ((List<?>) vIds).stream().anyMatch(vid -> {
                                            Object vidVal = (vid instanceof Map) ? ((Map) vid).get("value") : vid;
                                            return vId.equals(vidVal);
                                        });
                                    }
                                }
                                return false;
                            })
                            .toList();
                    model.addAttribute("tareas", misTareas);
                }
            }
        } catch (Exception ignored) {
        }

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

        List<Object> usuarios = fetchList(authUrl + "/v1/usuarios");
        List<Object> perfiles = fetchList(apiUrl + "/v1/perfiles-legales");
        List<Object> adoptantes = fetchList(apiUrl + "/v1/adoptantes");

        Map<Integer, Map<String, Object>> perfilesMap = new HashMap<>();
        for (Object p : perfiles) {
            if (p instanceof Map) {
                Map<String, Object> pm = (Map<String, Object>) p;
                Object uId = pm.get("usuarioId");
                if (uId instanceof Number)
                    perfilesMap.put(((Number) uId).intValue(), pm);
            }
        }

        Map<Integer, Integer> adoptantesMap = new HashMap<>();
        for (Object a : adoptantes) {
            if (a instanceof Map) {
                Map<String, Object> am = (Map<String, Object>) a;
                Object uIdRaw = am.get("usuarioId");
                Object aIdRaw = am.get("id");

                Integer uId = (uIdRaw instanceof Map) ? (Integer) ((Map) uIdRaw).get("value") : (Integer) uIdRaw;
                Integer aId = (aIdRaw instanceof Map) ? (Integer) ((Map) aIdRaw).get("value") : (Integer) aIdRaw;

                if (uId != null && aId != null)
                    adoptantesMap.put(uId, aId);
            }
        }

        String query = q.toLowerCase();
        List<Map<String, Object>> encontrados = new ArrayList<>();

        for (Object u : usuarios) {
            if (u instanceof Map) {
                Map<String, Object> um = (Map<String, Object>) u;
                Integer uId = ((Number) um.get("id")).intValue();
                Map<String, Object> perfil = perfilesMap.get(uId);

                String nombre = perfil != null && perfil.get("nombre") != null ? String.valueOf(perfil.get("nombre"))
                        : "";
                String apellido = perfil != null && perfil.get("apellido") != null
                        ? String.valueOf(perfil.get("apellido"))
                        : "";
                String email = String.valueOf(um.get("email"));

                if (nombre.toLowerCase().contains(query) || apellido.toLowerCase().contains(query)
                        || email.toLowerCase().contains(query)) {
                    Map<String, Object> result = new HashMap<>(um);
                    result.put("nombre", nombre);
                    result.put("apellido", apellido);
                    result.put("adoptanteId", adoptantesMap.get(uId));
                    encontrados.add(result);
                }
            }
        }

        model.addAttribute("usuariosEncontrados", encontrados);
        model.addAttribute("context", context);
        return FragmentoContenido.USUARIO_SUGERENCIAS.getPath() + " :: suggestions";
    }

    private List<Object> fetchList(String url) {
        try {
            Object[] arr = restTemplate.getForObject(url, Object[].class);
            return arr != null ? Arrays.asList(arr) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void fetchAnimalName(Object animalId, Map<String, String> names) {
        if (animalId == null)
            return;
        String idStr = String.valueOf(animalId);
        if (names.containsKey(idStr))
            return;

        try {
            Map<String, Object> animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId, Map.class);
            if (animal != null) {
                names.put(idStr, String.valueOf(animal.get("nombre")));
            }
        } catch (Exception e) {
            names.put(idStr, "Animal #" + idStr);
        }
    }
}
