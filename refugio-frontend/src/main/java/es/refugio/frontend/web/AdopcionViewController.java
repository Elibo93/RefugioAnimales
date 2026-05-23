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
import es.refugio.common.util.ExcelExportHelper;

import es.refugio.frontend.web.constants.WebRoutes;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdopcionViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping(WebRoutes.ADOPCIONES_BASE)
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer adoptanteId,
            @RequestParam(required = false) Integer animalId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String successMessage,
            HttpServletRequest request) {

        String path = "/v1/adopciones";
        if (q != null && !q.trim().isEmpty()) {
            path += "?q=" + q;
        }

        PaginatedResponse<AdopcionRecord> paginationMap = helper.fetchPaginated(apiUrl + path, page, size, AdopcionRecord.class);
        List<AdopcionRecord> adopciones = paginationMap.items();
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes", AdoptanteRecord.class);
        List<AnimalRecord> animales   = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
        List<UsuarioRecord> usuarios   = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfiles   = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
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
                String uidStr = String.valueOf(a.usuarioId());
                PerfilLegalRecord perfil = perfilesMap.get(uidStr);
                UsuarioRecord user = usuariosMap.get(uidStr);
                
                String nombre = "";
                String apellido = "";
                
                if (perfil != null) {
                    nombre = perfil.nombre() != null ? perfil.nombre() : "";
                    apellido = perfil.apellido() != null ? perfil.apellido() : "";
                } else if (user != null) {
                    nombre = user.username() != null ? user.username() : "Adoptante";
                }
                
                String fullName = (nombre + " " + apellido).trim();
                adoptanteNombres.put(String.valueOf(a.id()), fullName);
            }
        }

        Map<String, AnimalRecord> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a);
        }

        Map<String, String> adoptanteUsuarioIds = new HashMap<>();
        for (AdoptanteRecord a : adoptantes) {
            if (a.usuarioId() != null) {
                adoptanteUsuarioIds.put(String.valueOf(a.id()), a.usuarioId().toString());
            }
        }

        model.addAttribute(ModelAttribute.Adopcion_LIST.getName(),   adopciones);
        model.addAttribute("pagination",                             paginationMap);
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
        
        if (request != null && "true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Adopcion_LIST.getPath();
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Adopcion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ADOPCIONES_NUEVA)
    public String formulario(Model model) {
        Map<String, Object> emptyAdopcion = new HashMap<>();
        emptyAdopcion.put("id", null);
        emptyAdopcion.put("animalId", null);
        emptyAdopcion.put("adoptanteId", null);
        emptyAdopcion.put("estado", null);
        emptyAdopcion.put("fechaAdopcion", null);
        model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), emptyAdopcion);
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
        
        String formattedDate = fechaAdopcion;
        if (formattedDate != null && !formattedDate.trim().isEmpty()) {
            if (!formattedDate.contains("T")) {
                formattedDate = formattedDate.trim() + "T00:00:00";
            }
        } else {
            formattedDate = java.time.LocalDateTime.now().toString();
        }
        body.put("fechaAdopcion", formattedDate);
        body.put("contrato",    "Contrato formalizado");

        restTemplate.postForObject(apiUrl + "/v1/adopciones", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.adopcion_creada"));
        return "redirect:" + WebRoutes.ADOPCIONES_BASE;
    }

    @GetMapping(WebRoutes.ADOPCIONES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        AdopcionRecord adopcion = helper.fetchObject(apiUrl + "/v1/adopciones/" + id, AdopcionRecord.class);
        model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), adopcion);
        
        if (adopcion != null) {
            Integer animalId = adopcion.animalId();
            Integer adoptanteId = adopcion.adoptanteId();
            
            if (animalId != null) {
                AnimalRecord animal = helper.fetchObject(apiUrl + "/v1/animales/" + animalId, AnimalRecord.class);
                model.addAttribute("animalData", animal);
            }
            
            if (adoptanteId != null) {
                try {
                    AdoptanteRecord adoptante = helper.fetchObject(apiUrl + "/v1/adoptantes/" + adoptanteId, AdoptanteRecord.class);
                    if (adoptante != null) {
                        Integer usuarioId = adoptante.usuarioId();
                        if (usuarioId != null) {
                            PerfilLegalRecord perfilObj = helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + usuarioId, PerfilLegalRecord.class);
                            if (perfilObj != null) {
                                String nombre = perfilObj.nombre() + " " + perfilObj.apellido();
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
        
        String formattedDate = fechaAdopcion;
        if (formattedDate != null && !formattedDate.trim().isEmpty()) {
            if (!formattedDate.contains("T")) {
                formattedDate = formattedDate.trim() + "T00:00:00";
            }
        } else {
            formattedDate = java.time.LocalDateTime.now().toString();
        }
        body.put("fechaAdopcion", formattedDate);

        restTemplate.put(apiUrl + "/v1/adopciones/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.adopcion_editada"));
        return "redirect:" + WebRoutes.ADOPCIONES_BASE;
    }

    @PostMapping(WebRoutes.ADOPCIONES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/adopciones/" + id);
        if ("true".equals(request.getHeader("HX-Request")) && !"true".equals(request.getHeader("HX-History-Restore-Request"))) return ResponseEntity.ok("");
        return ResponseEntity.status(302).header("Location", WebRoutes.ADOPCIONES_BASE).build();
    }

    @GetMapping(WebRoutes.ADOPCIONES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<AdopcionRecord> adopciones = helper.fetchList(apiUrl + "/v1/adopciones", AdopcionRecord.class);
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes", AdoptanteRecord.class);
        List<AnimalRecord> animales   = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
        List<UsuarioRecord> usuarios   = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfiles   = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
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
                String uidStr = String.valueOf(a.usuarioId());
                PerfilLegalRecord perfil = perfilesMap.get(uidStr);
                UsuarioRecord user = usuariosMap.get(uidStr);
                
                String nombre = "";
                String apellido = "";
                
                if (perfil != null) {
                    nombre = perfil.nombre() != null ? perfil.nombre() : "";
                    apellido = perfil.apellido() != null ? perfil.apellido() : "";
                } else if (user != null) {
                    nombre = user.username() != null ? user.username() : "Adoptante";
                }
                
                String fullName = (nombre + " " + apellido).trim();
                adoptanteNombres.put(String.valueOf(a.id()), fullName);
            }
        }

        Map<String, String> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a.nombre());
        }

        Context context = new Context(org.springframework.context.i18n.LocaleContextHolder.getLocale());
        context.setVariable("adopciones", adopciones);
        context.setVariable("adoptanteNombres", adoptanteNombres);
        context.setVariable("animalesMap", animalesMap);

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

    @GetMapping(WebRoutes.ADOPCIONES_EXCEL)
    public void exportarExcel(HttpServletResponse response) throws Exception {
        List<AdopcionRecord> adopciones = helper.fetchList(apiUrl + "/v1/adopciones", AdopcionRecord.class);
        List<AdoptanteRecord> adoptantes = helper.fetchList(apiUrl + "/v1/adoptantes", AdoptanteRecord.class);
        List<AnimalRecord> animales   = helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
        List<UsuarioRecord> usuarios   = helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
        List<PerfilLegalRecord> perfiles   = helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
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
                String uidStr = String.valueOf(a.usuarioId());
                PerfilLegalRecord perfil = perfilesMap.get(uidStr);
                UsuarioRecord user = usuariosMap.get(uidStr);
                
                String nombre = "";
                String apellido = "";
                
                if (perfil != null) {
                    nombre = perfil.nombre() != null ? perfil.nombre() : "";
                    apellido = perfil.apellido() != null ? perfil.apellido() : "";
                } else if (user != null) {
                    nombre = user.username() != null ? user.username() : "Adoptante";
                }
                
                String fullName = (nombre + " " + apellido).trim();
                adoptanteNombres.put(String.valueOf(a.id()), fullName);
            }
        }

        Map<String, String> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a.nombre());
        }

        byte[] excelBytes = ExcelExportHelper.exportToExcel(
            "Adopciones",
            List.of("ID", "ID Animal", "Animal", "ID Adoptante", "Adoptante", "ID Solicitud Adopción", "Fecha Adopción", "Estado", "Contrato"),
            adopciones,
            List.of(
                AdopcionRecord::id,
                AdopcionRecord::animalId,
                a -> animalesMap.getOrDefault(String.valueOf(a.animalId()), "Animal #" + a.animalId()),
                AdopcionRecord::adoptanteId,
                a -> adoptanteNombres.getOrDefault(String.valueOf(a.adoptanteId()), "Adoptante #" + a.adoptanteId()),
                a -> a.solicitudAdopcionId() != null ? a.solicitudAdopcionId() : "-",
                a -> a.fechaAdopcion() != null ? a.fechaAdopcion().toString() : "",
                AdopcionRecord::estado,
                a -> a.contrato() != null ? a.contrato() : "-"
            )
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=adopciones.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @GetMapping(WebRoutes.ADOPCIONES_CONTRATO)
    public ResponseEntity<byte[]> descargarContrato(@PathVariable Integer id) {
        return restTemplate.getForEntity(apiUrl + "/v1/reports/adopcion/" + id + "/contrato", byte[].class);
    }
}
