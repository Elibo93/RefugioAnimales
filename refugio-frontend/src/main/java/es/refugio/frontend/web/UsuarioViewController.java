package es.refugio.frontend.web;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.util.ViewControllerHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import es.refugio.common.util.ExcelExportHelper;

import java.io.OutputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class UsuarioViewController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioViewController.class);

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${auth.api.url}")
    private String authUrl;

    @Value("${backend.api.url}")
    private String apiUrl;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(WebRoutes.PERSONAS_BASE)
    public String listarPersonas(@RequestParam(required = false) String q,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model, HttpServletRequest request) {

        logger.info("Filtrando personas - Query: '{}', Rol: '{}', Page: {}, Size: {}", q, rol, page, size);

        List<UsuarioRecord> personasAuth = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) {
                perfilesMap.put(p.usuarioId(), p);
            }
        }

        List<PersonaCompletaRecord> personasCompletas = new ArrayList<>();
        String query = q != null ? q.toLowerCase() : null;

        for (UsuarioRecord u : personasAuth) {
            PerfilLegalRecord perfil = perfilesMap.get(u.id());

            String nombre = perfil != null ? perfil.nombre() : "";
            String apellido = perfil != null ? perfil.apellido() : "";
            String dni = perfil != null ? perfil.dni() : "";
            String telefono = perfil != null ? perfil.telefono() : "";
            String direccion = perfil != null ? perfil.direccion() : "";
            String fechaNacimiento = perfil != null ? perfil.fechaNacimiento() : "";

            PersonaCompletaRecord persona = new PersonaCompletaRecord(
                    u.id(),
                    u.email(),
                    u.username(),
                    u.rol(),
                    nombre,
                    apellido,
                    dni,
                    telefono,
                    direccion,
                    fechaNacimiento
            );

            // Filtrado por rol
            if (rol != null && !rol.isEmpty() && !"ALL".equals(rol)) {
                if (!String.valueOf(u.rol()).equals(rol))
                    continue;
            }

            // Filtrado por búsqueda
            if (query != null && !query.isEmpty()) {
                String nombreLower = (nombre != null ? nombre : "").toLowerCase();
                String apellidoLower = (apellido != null ? apellido : "").toLowerCase();
                String emailLower = (u.email() != null ? u.email() : "").toLowerCase();
                String usernameLower = (u.username() != null ? u.username() : "").toLowerCase();

                if (!nombreLower.contains(query) && !apellidoLower.contains(query) &&
                        !emailLower.contains(query) && !usernameLower.contains(query)) {
                    continue;
                }
            }

            personasCompletas.add(persona);
        }

        // Paginación en memoria
        int totalElements = personasCompletas.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages == 0) totalPages = 1;

        int activePage = page;
        if (activePage < 1) activePage = 1;
        if (activePage > totalPages) activePage = totalPages;

        int fromIndex = (activePage - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<PersonaCompletaRecord> paginatedItems = new ArrayList<>();
        if (fromIndex < totalElements && fromIndex >= 0) {
            paginatedItems = personasCompletas.subList(fromIndex, toIndex);
        }

        boolean hasNext = activePage < totalPages;
        boolean hasPrevious = activePage > 1;

        PaginatedResponse<PersonaCompletaRecord> pagination = new PaginatedResponse<>(
                paginatedItems,
                totalPages,
                totalElements,
                activePage,
                size,
                hasNext,
                hasPrevious
        );

        model.addAttribute(ModelAttribute.Persona_LIST.getName(), paginatedItems);
        model.addAttribute("pagination", pagination);
        model.addAttribute("roles", List.of("ROLE_PUBLICO", "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE", "ROLE_ADMIN"));
        model.addAttribute("selectedRol", rol);
        model.addAttribute("query", q);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Persona_LIST.getPath();
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(WebRoutes.PERSONAS_NUEVO)
    public String crearPersonaForm(Model model, HttpServletRequest request) {
        Map<String, Object> emptyPersona = new HashMap<>();
        emptyPersona.put("id", null);
        emptyPersona.put("username", null);
        emptyPersona.put("email", null);
        emptyPersona.put("nombre", null);
        emptyPersona.put("apellido", null);
        emptyPersona.put("telefono", null);
        emptyPersona.put("dni", null);
        emptyPersona.put("fechaNacimiento", null);
        emptyPersona.put("rol", null);
        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), emptyPersona);
        model.addAttribute("roles", List.of("ROLE_PUBLICO", "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE", "ROLE_ADMIN"));

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Persona_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PreAuthorize("hasRole('ADMIN')")
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
            Map<?, ?> createdUser = restTemplate.postForObject(authUrl + "/v1/usuarios", userBody, Map.class);
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

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @GetMapping(WebRoutes.PERSONAS_EDITAR)
    public String editarPersonaForm(@PathVariable Integer id, Model model, HttpServletRequest request) {
        UsuarioRecord user = helper.fetchObject(authUrl + "/v1/usuarios/" + id, UsuarioRecord.class);
        Map<String, Object> persona = new HashMap<>();
        // Preinicializar todas las claves a nulo para evitar excepciones SpEL
        persona.put("id", null);
        persona.put("email", null);
        persona.put("username", null);
        persona.put("rol", null);
        persona.put("createdAt", null);
        persona.put("nombre", null);
        persona.put("apellido", null);
        persona.put("telefono", null);
        persona.put("dni", null);
        persona.put("direccion", null);
        persona.put("fechaNacimiento", null);

        if (user != null) {
            persona.put("id", user.id());
            persona.put("email", user.email());
            persona.put("username", user.username());
            persona.put("rol", user.rol());
            persona.put("createdAt", user.createdAt());
        }

        try {
            PerfilLegalRecord legal = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + id, PerfilLegalRecord.class);
            if (legal != null) {
                persona.put("nombre", legal.nombre());
                persona.put("apellido", legal.apellido());
                persona.put("telefono", legal.telefono());
                persona.put("dni", legal.dni());
                persona.put("direccion", legal.direccion());
                persona.put("fechaNacimiento", legal.fechaNacimiento());
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

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
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
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // 1. Actualizar en Auth
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", email);
        userBody.put("username", username);
        
        boolean isAdmin = request.isUserInRole("ROLE_ADMIN");
        if (isAdmin && rol != null) {
            userBody.put("rol", rol);
        }
        
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
        
        return "redirect:/web/personas/" + id;
    }

    @PostMapping(WebRoutes.PERSONAS_DETALLE + "/verificar-password")
    @ResponseBody
    public ResponseEntity<?> verificarPassword(@PathVariable Integer id, @RequestParam String password) {
        try {
            org.springframework.util.MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
            map.add("password", password != null ? password.trim() : "");

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
            
            org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> req = 
                new org.springframework.http.HttpEntity<>(map, headers);

            return restTemplate.postForEntity(authUrl + "/v1/usuarios/" + id + "/verificar-password", req, Map.class);
        } catch (HttpClientErrorException.Forbidden e) {
            logger.warn("Acceso denegado al verificar password para ID {}: {}", id, e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Contraseña actual incorrecta o falta de permisos"));
        } catch (Exception e) {
            logger.error("Error al verificar contraseña para usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al verificar la contraseña"));
        }
    }

    @PostMapping(WebRoutes.PERSONAS_DETALLE + "/cambiar-password")
    @ResponseBody
    public ResponseEntity<?> cambiarPassword(@PathVariable Integer id, @RequestParam String newPassword) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("newPassword", newPassword);
            
            restTemplate.put(authUrl + "/v1/usuarios/" + id + "/password", body);
            
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada"));
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña para usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al conectar con el servicio de autenticación"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(WebRoutes.PERSONAS_ELIMINAR)
    public String borrar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Iniciando borrado coordinado para usuario ID: {}", id);

            try {
                restTemplate.delete(apiUrl + "/v1/perfiles-legales/usuario/" + id);
                logger.info("PerfilLegal eliminado con éxito para usuario {}", id);
            } catch (Exception e) {
                logger.warn("No se pudo eliminar el PerfilLegal del usuario {} o no existía. Continuando...", id);
            }

            restTemplate.delete(authUrl + "/v1/usuarios/" + id);
            logger.info("Usuario ID {} eliminado con éxito de Auth", id);

            redirectAttributes.addFlashAttribute("successMessage", "Usuario y datos legales eliminados con éxito");
        } catch (Exception e) {
            logger.error("Error crítico al borrar usuario {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al borrar usuario: " + e.getMessage());
        }
        return "redirect:" + WebRoutes.PERSONAS_BASE;
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @GetMapping(WebRoutes.PERSONAS_DETALLE)
    public String verDetalle(@PathVariable Integer id, Model model, HttpServletRequest request) {
        model.addAttribute("tareas", new ArrayList<>());
        model.addAttribute("vinculosAnimales", new ArrayList<>());
        model.addAttribute("animalNames", new HashMap<>());

        UsuarioRecord userAuth = helper.fetchObject(authUrl + "/v1/usuarios/" + id, UsuarioRecord.class);
        Map<String, Object> persona = new HashMap<>();
        // Preinicializar todas las claves a nulo para evitar excepciones SpEL
        persona.put("id", null);
        persona.put("email", null);
        persona.put("username", null);
        persona.put("rol", null);
        persona.put("createdAt", null);
        persona.put("nombre", null);
        persona.put("apellido", null);
        persona.put("telefono", null);
        persona.put("dni", null);
        persona.put("direccion", null);
        persona.put("fechaNacimiento", null);

        if (userAuth != null) {
            persona.put("id", userAuth.id());
            persona.put("email", userAuth.email());
            persona.put("username", userAuth.username());
            persona.put("rol", userAuth.rol());
            persona.put("createdAt", userAuth.createdAt());
        }

        try {
            PerfilLegalRecord legal = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + id, PerfilLegalRecord.class);
            if (legal != null) {
                persona.put("nombre", legal.nombre());
                persona.put("apellido", legal.apellido());
                persona.put("telefono", legal.telefono());
                persona.put("dni", legal.dni());
                persona.put("direccion", legal.direccion());
                persona.put("fechaNacimiento", legal.fechaNacimiento());
            }
        } catch (Exception ignored) {
        }

        model.addAttribute(ModelAttribute.SINGLE_Persona.getName(), persona);

        // Obtener información de Adoptante y enlaces de animales
        try {
            AdoptanteRecord adoptante = helper.fetchObject(apiUrl + "/v1/adoptantes/usuario/" + id, AdoptanteRecord.class);
            model.addAttribute("adoptante", adoptante);
            if (adoptante != null) {
                int aId = adoptante.id();

                List<SolicitudAdopcionRecord> solicitudes = helper.fetchList(apiUrl + "/v1/solicitudes-adopcion/adoptante/" + aId, SolicitudAdopcionRecord.class);
                List<AdopcionRecord> adopciones = helper.fetchList(apiUrl + "/v1/adopciones/adoptante/" + aId, AdopcionRecord.class);

                List<Map<String, Object>> vinculos = new ArrayList<>();
                Map<String, String> animalNames = new HashMap<>();

                for (SolicitudAdopcionRecord s : solicitudes) {
                    Map<String, Object> v = new HashMap<>();
                    v.put("id", s.id());
                    v.put("animalId", s.animalId());
                    v.put("tipoVinculo", "SOLICITUD");
                    v.put("estadoVinculo", s.estado());
                    v.put("fechaVinculo", s.fecha());
                    vinculos.add(v);
                    fetchAnimalName(s.animalId(), animalNames);
                }
                for (AdopcionRecord ad : adopciones) {
                    Map<String, Object> v = new HashMap<>();
                    v.put("id", ad.id());
                    v.put("animalId", ad.animalId());
                    v.put("tipoVinculo", "ADOPCIÓN");
                    v.put("estadoVinculo", "FINALIZADA");
                    v.put("fechaVinculo", ad.fechaAdopcion());
                    vinculos.add(v);
                    fetchAnimalName(ad.animalId(), animalNames);
                }
                model.addAttribute("vinculosAnimales", vinculos);
                model.addAttribute("animalNames", animalNames);
            }
        } catch (Exception ignored) {
        }

        // Obtener información de Voluntario
        try {
            VoluntarioRecord voluntario = helper.fetchObject(apiUrl + "/v1/voluntarios/usuario/" + id, VoluntarioRecord.class);
            model.addAttribute("voluntario", voluntario);

            if (voluntario != null) {
                Integer vId = voluntario.id();
                if (vId != null) {
                    List<TareaRecord> todasTareas = helper.fetchList(apiUrl + "/v1/tareas", TareaRecord.class);
                    List<TareaRecord> misTareas = todasTareas.stream()
                            .filter(t -> t.voluntarioIds() != null && t.voluntarioIds().contains(vId))
                            .toList();
                    model.addAttribute("tareas", misTareas);
                    
                    try {
                        List<DisponibilidadRecord> disponibilidades = helper.fetchList(apiUrl + "/v1/voluntarios/" + vId + "/disponibilidad", DisponibilidadRecord.class);
                        model.addAttribute("disponibilidades", disponibilidades);
                    } catch (Exception e) {
                        model.addAttribute("disponibilidades", new ArrayList<>());
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // Gamificación
        try {
            model.addAttribute("metricas", restTemplate.getForObject(apiUrl + "/v1/gamificacion/metricas/usuario/" + id, Map.class));
            model.addAttribute("logrosUsuario", helper.fetchList(apiUrl + "/v1/gamificacion/logros/usuario/" + id, Map.class));
            model.addAttribute("todosLosLogros", helper.fetchList(apiUrl + "/v1/gamificacion/logros", Map.class));
        } catch (Exception e) {
            logger.warn("Error al cargar datos de gamificación para usuario {}: {}", id, e.getMessage());
        }

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Persona_DETALLE.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Persona_DETALLE.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(WebRoutes.PERSONAS_PDF)
    public void exportarPDF(HttpServletResponse response, @RequestParam(required = false) String rol) throws Exception {
        List<UsuarioRecord> personasAuth = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) {
                perfilesMap.put(p.usuarioId(), p);
            }
        }

        List<PersonaCompletaRecord> personasCompletas = new ArrayList<>();
        for (UsuarioRecord u : personasAuth) {
            if (rol != null && !rol.isEmpty() && !"ALL".equals(rol)) {
                if (!String.valueOf(u.rol()).equals(rol))
                    continue;
            }

            PerfilLegalRecord perfil = perfilesMap.get(u.id());

            String nombre = perfil != null ? perfil.nombre() : "";
            String apellido = perfil != null ? perfil.apellido() : "";
            String dni = perfil != null ? perfil.dni() : "";
            String telefono = perfil != null ? perfil.telefono() : "";
            String direccion = perfil != null ? perfil.direccion() : "";
            String fechaNacimiento = perfil != null ? perfil.fechaNacimiento() : "";

            PersonaCompletaRecord persona = new PersonaCompletaRecord(
                    u.id(),
                    u.email(),
                    u.username(),
                    u.rol(),
                    nombre,
                    apellido,
                    dni,
                    telefono,
                    direccion,
                    fechaNacimiento
            );

            personasCompletas.add(persona);
        }

        Context context = new Context(org.springframework.context.i18n.LocaleContextHolder.getLocale());
        context.setVariable("personas", personasCompletas);
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(WebRoutes.PERSONAS_EXCEL)
    public void exportarExcel(HttpServletResponse response, @RequestParam(required = false) String rol) throws Exception {
        List<UsuarioRecord> personasAuth = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfilesLegales = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) {
                perfilesMap.put(p.usuarioId(), p);
            }
        }

        List<PersonaCompletaRecord> personasCompletas = new ArrayList<>();
        for (UsuarioRecord u : personasAuth) {
            if (rol != null && !rol.isEmpty() && !"ALL".equals(rol)) {
                if (!String.valueOf(u.rol()).equals(rol))
                    continue;
            }

            PerfilLegalRecord perfil = perfilesMap.get(u.id());

            String nombre = perfil != null ? perfil.nombre() : "";
            String apellido = perfil != null ? perfil.apellido() : "";
            String dni = perfil != null ? perfil.dni() : "";
            String telefono = perfil != null ? perfil.telefono() : "";
            String direccion = perfil != null ? perfil.direccion() : "";
            String fechaNacimiento = perfil != null ? perfil.fechaNacimiento() : "";

            personasCompletas.add(new PersonaCompletaRecord(
                    u.id(),
                    u.email(),
                    u.username(),
                    u.rol(),
                    nombre,
                    apellido,
                    dni,
                    telefono,
                    direccion,
                    fechaNacimiento
            ));
        }

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
            "Usuarios",
            List.of("ID", "Username", "Email", "Nombre", "Apellido", "Teléfono", "DNI", "Rol", "Dirección", "Fecha Nacimiento"),
            personasCompletas,
            List.of(
                PersonaCompletaRecord::id,
                PersonaCompletaRecord::username,
                PersonaCompletaRecord::email,
                PersonaCompletaRecord::nombre,
                PersonaCompletaRecord::apellido,
                PersonaCompletaRecord::telefono,
                PersonaCompletaRecord::dni,
                PersonaCompletaRecord::rol,
                p -> p.direccion() != null ? p.direccion() : "-",
                p -> p.fechaNacimiento() != null ? p.fechaNacimiento() : "-"
            )
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(WebRoutes.PERSONAS_BUSCAR)
    public String buscarUsuarios(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String context,
            Model model) {

        if (q.trim().isEmpty()) {
            model.addAttribute("usuariosEncontrados", List.of());
            return FragmentoContenido.USUARIO_SUGERENCIAS.getPath() + " :: suggestions";
        }

        logger.info("buscarUsuarios called with q: '{}', context: '{}'", q, context);
        List<UsuarioRecord> usuarios = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfiles = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes?size=1000", AdoptanteRecord.class);
        List<VoluntarioRecord> voluntarios = helper.fetchList(apiUrl + "/v1/voluntarios?size=1000", VoluntarioRecord.class);
        logger.info("buscarUsuarios - Fetched {} usuarios, {} perfiles, {} adoptantes, {} voluntarios", 
                    usuarios.size(), perfiles.size(), adoptantes.size(), voluntarios.size());

        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfiles) {
            if (p.usuarioId() != null) {
                perfilesMap.put(p.usuarioId(), p);
            }
        }

        Map<Integer, Integer> adoptantesUserIds = new HashMap<>();
        for (AdoptanteRecord a : adoptantes) {
            if (a.usuarioId() != null) {
                adoptantesUserIds.put(a.usuarioId(), a.id());
            }
        }

        Set<Integer> voluntariosUserIds = new HashSet<>();
        for (VoluntarioRecord v : voluntarios) {
            if (v.usuarioId() != null) {
                voluntariosUserIds.add(v.usuarioId());
            }
        }

        String query = q.toLowerCase().trim();
        List<Map<String, Object>> encontrados = new ArrayList<>();

        for (UsuarioRecord u : usuarios) {
            PerfilLegalRecord perfil = perfilesMap.get(u.id());

            String nombre = perfil != null && perfil.nombre() != null ? perfil.nombre() : "";
            String apellido = perfil != null && perfil.apellido() != null ? perfil.apellido() : "";
            String email = u.email() != null ? u.email() : "";
            String username = u.username() != null ? u.username() : "";
            String nombreCompleto = (nombre + " " + apellido).trim();
            String idStr = String.valueOf(u.id());

            boolean matches = nombre.toLowerCase().contains(query) ||
                              apellido.toLowerCase().contains(query) ||
                              email.toLowerCase().contains(query) ||
                              username.toLowerCase().contains(query) ||
                              nombreCompleto.toLowerCase().contains(query) ||
                              idStr.equals(query);

            if (matches) {
                // Si el contexto es de solicitud y el usuario no tiene perfil de adoptante, lo creamos sobre la marcha
                if ("solicitud".equals(context) && !adoptantesUserIds.containsKey(u.id())) {
                    try {
                        Map<String, Object> adoptanteReq = new HashMap<>();
                        adoptanteReq.put("usuarioId", u.id());
                        adoptanteReq.put("estadoValidacion", "APROBADO");
                        
                        Map<?, ?> createdAdoptante = restTemplate.postForObject(apiUrl + "/v1/adoptantes", adoptanteReq, Map.class);
                        if (createdAdoptante != null && createdAdoptante.get("id") != null) {
                            Integer newAdoptanteId = ((Number) createdAdoptante.get("id")).intValue();
                            adoptantesUserIds.put(u.id(), newAdoptanteId);
                            logger.info("Creado perfil de adoptante ID {} para usuario {} automáticamente durante búsqueda", newAdoptanteId, u.id());
                        }
                    } catch (Exception e) {
                        logger.error("No se pudo crear perfil de adoptante automático para usuario {}: {}", u.id(), e.getMessage());
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("id", u.id());
                result.put("username", username);
                result.put("email", email);
                result.put("rol", u.rol());
                result.put("nombre", nombre);
                result.put("apellido", apellido);
                
                boolean yaRegistrado = false;
                if ("adoptante".equals(context)) {
                    yaRegistrado = adoptantesUserIds.containsKey(u.id());
                } else if ("voluntario".equals(context)) {
                    yaRegistrado = voluntariosUserIds.contains(u.id());
                } else if ("adopcion_filter".equals(context)) {
                    // Contexto especial para filtrado donde SOLO queremos adoptantes válidos
                    if (adoptantesUserIds.containsKey(u.id())) {
                        result.put("adoptanteId", adoptantesUserIds.get(u.id()));
                    } else {
                        continue;
                    }
                }
                
                if (adoptantesUserIds.containsKey(u.id())) {
                    result.put("adoptanteId", adoptantesUserIds.get(u.id()));
                }
                
                result.put("yaRegistrado", yaRegistrado);
                encontrados.add(result);
            }
        }

        logger.info("buscarUsuarios - Found {} matched users", encontrados.size());
        model.addAttribute("usuariosEncontrados", encontrados);
        model.addAttribute("context", context);
        return FragmentoContenido.USUARIO_SUGERENCIAS.getPath() + " :: suggestions";
    }

    private void fetchAnimalName(Object animalId, Map<String, String> names) {
        if (animalId == null)
            return;
        String idStr = String.valueOf(animalId);
        if (names.containsKey(idStr))
            return;

        try {
            AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + animalId, AnimalRecord.class);
            if (animal != null) {
                names.put(String.valueOf(animal.id()), animal.nombre());
            }
        } catch (Exception e) {
            names.put(idStr, "Animal #" + idStr);
        }
    }
}
