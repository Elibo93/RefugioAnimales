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
public class AnimalViewController {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping(WebRoutes.ANIMALES_BASE)
    public String listar(Model model,
            @RequestParam(required = false) String successMessage,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) List<String> edad,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) Boolean urgencia,
            HttpServletRequest request) {

        try {
            // Construir URL con parámetros de filtro
            StringBuilder url = new StringBuilder(apiUrl + "/v1/animales?");
            if (especie  != null) url.append("especie=").append(especie).append("&");
            if (tamano   != null) url.append("tamano=").append(tamano).append("&");
            if (sexo     != null) url.append("sexo=").append(sexo).append("&");
            if (urgencia != null) url.append("urgencia=").append(urgencia).append("&");
            if (estado   != null) url.append("estado=").append(estado).append("&");
            if (edad     != null) edad.forEach(e -> url.append("edad=").append(e).append("&"));

            Object[] animales = restTemplate.getForObject(url.toString(), Object[].class);
            model.addAttribute(ModelAttribute.Animal_LIST.getName(), animales != null ? Arrays.asList(animales) : List.of());
        } catch (Exception e) {
            model.addAttribute(ModelAttribute.Animal_LIST.getName(), List.of());
        }

        // Fetch dinámico de especies activas para los filtros
        try {
            Object[] arrEspecies = restTemplate.getForObject(apiUrl + "/v1/animales/especies", Object[].class);
            model.addAttribute("especiesActivas", arrEspecies != null ? Arrays.asList(arrEspecies) : List.of());
        } catch (Exception e) {
            model.addAttribute("especiesActivas", List.of());
        }

        // Voluntarios solo son necesarios para el panel de admin — fallo aquí no afecta la lista de animales
        try {
            Object[] voluntarios = restTemplate.getForObject(apiUrl + "/v1/voluntarios", Object[].class);
            model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), voluntarios != null ? Arrays.asList(voluntarios) : List.of());
        } catch (Exception e) {
            model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), List.of());
        }

        model.addAttribute("selectedEstado",   estado);
        model.addAttribute("selectedEspecie",  especie);
        model.addAttribute("selectedTamano",   tamano);
        model.addAttribute("selectedEdad",     edad);
        model.addAttribute("selectedSexo",     sexo);
        model.addAttribute("selectedUrgencia", urgencia);

        if (successMessage != null) model.addAttribute("successMessage", successMessage);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Animal_LIST.getPath();
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ANIMALES_NUEVO)
    public String formulario(Model model) {
        // Enums disponibles en el backend — se listan hardcoded ya que son valores fijos
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), new HashMap<>());
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), fetchList("/v1/voluntarios"));
        model.addAttribute("tamanos", List.of("PEQUEÑO", "MEDIANO", "GRANDE", "GIGANTE"));
        model.addAttribute("sexos",   List.of("MACHO", "HEMBRA"));
        model.addAttribute("estados", List.of("DISPONIBLE", "ADOPTADO", "EN_ACOGIDA", "EN_TRATAMIENTO", "RESERVADO", "FALLECIDO"));
        model.addAttribute("especies", List.of("PERRO", "GATO", "CONEJO", "AVE", "REPTIL", "OTRO"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ANIMALES_NUEVO)
    public String crearAnimal(@RequestParam String nombre,
            @RequestParam String especie,
            @RequestParam(required = false) String especiePersonalizada,
            @RequestParam String raza,
            @RequestParam String sexo,
            @RequestParam String chipId,
            @RequestParam String estado,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String foto,
            @RequestParam(required = false) Double peso,
            @RequestParam(required = false) Integer nivelEnergia,
            @RequestParam(required = false) Boolean urgencia,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("nombre",               nombre);
        body.put("especie",              especie);
        body.put("especiePersonalizada", especiePersonalizada != null ? especiePersonalizada : "");
        body.put("raza",                 raza);
        body.put("sexo",                 sexo);
        body.put("chipId",               chipId);
        body.put("estado",               estado);
        body.put("edad",                 edad != null ? edad : 0);
        body.put("tamano",               tamano != null ? tamano : "");
        body.put("descripcion",          descripcion != null ? descripcion : "");
        body.put("foto",                 foto != null ? foto : "");
        body.put("peso",                 peso != null ? peso : 0.0);
        body.put("nivelEnergia",         nivelEnergia != null ? nivelEnergia : 0);
        body.put("urgencia",             urgencia != null && urgencia);

        restTemplate.postForObject(apiUrl + "/v1/animales", body, Object.class);
        redirectAttributes.addFlashAttribute("successMessage", "Animal creado correctamente");
        return "redirect:" + WebRoutes.ANIMALES_BASE;
    }

    @GetMapping(WebRoutes.ANIMALES_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), fetchList("/v1/voluntarios"));
        model.addAttribute("tamanos", List.of("PEQUEÑO", "MEDIANO", "GRANDE", "GIGANTE"));
        model.addAttribute("sexos",   List.of("MACHO", "HEMBRA"));
        model.addAttribute("estados", List.of("DISPONIBLE", "ADOPTADO", "EN_ACOGIDA", "EN_TRATAMIENTO", "RESERVADO", "FALLECIDO"));
        model.addAttribute("especies", List.of("PERRO", "GATO", "CONEJO", "AVE", "REPTIL", "OTRO"));
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ANIMALES_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String especie,
            @RequestParam(required = false) String especiePersonalizada,
            @RequestParam String chipId,
            @RequestParam String estado,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String foto,
            @RequestParam(required = false) Double peso,
            @RequestParam(required = false) Integer nivelEnergia,
            @RequestParam(required = false) Boolean urgencia,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> body = new HashMap<>();
        body.put("nombre",               nombre);
        body.put("especie",              especie);
        body.put("especiePersonalizada", especiePersonalizada != null ? especiePersonalizada : "");
        body.put("chipId",               chipId);
        body.put("estado",               estado);
        body.put("edad",                 edad != null ? edad : 0);
        body.put("tamano",               tamano != null ? tamano : "");
        body.put("descripcion",          descripcion != null ? descripcion : "");
        body.put("foto",                 foto != null ? foto : "");
        body.put("peso",                 peso != null ? peso : 0.0);
        body.put("nivelEnergia",         nivelEnergia != null ? nivelEnergia : 0);
        body.put("urgencia",             urgencia != null && urgencia);

        restTemplate.put(apiUrl + "/v1/animales/" + id, body);
        redirectAttributes.addFlashAttribute("successMessage", "Animal editado correctamente");
        return "redirect:" + WebRoutes.ANIMALES_BASE;
    }

    @PostMapping(WebRoutes.ANIMALES_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id,
            HttpServletRequest request) {
        restTemplate.delete(apiUrl + "/v1/animales/" + id);
        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.ANIMALES_BASE).build();
    }

    @GetMapping(WebRoutes.ANIMALES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Object> animales = fetchList("/v1/animales");
        Context context = new Context();
        context.setVariable("animales", animales);
        String htmlContent = templateEngine.process(ThymTemplates.Animal_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=animales.pdf");
        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
    }

    @GetMapping(WebRoutes.ANIMALES_BASE + "/{id}/detalle")
    public String detalleModal(@PathVariable Integer id, Model model) {
        // Intentar incrementar visitas (endpoint opcional — no falla si no existe)
        try {
            restTemplate.postForObject(apiUrl + "/v1/animales/" + id + "/visitas", null, Object.class);
        } catch (Exception ignored) { /* endpoint opcional */ }

        Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);
        return "fragments/content/animales-detalle-modal :: detalle";
    }

    @GetMapping(WebRoutes.ANIMALES_DETALLE)
    public String verDetalle(@PathVariable Integer id, Model model) {
        // Intentar incrementar visitas (endpoint opcional)
        try {
            restTemplate.postForObject(apiUrl + "/v1/animales/" + id + "/visitas", null, Object.class);
        } catch (Exception ignored) {}

        Object animal = restTemplate.getForObject(apiUrl + "/v1/animales/" + id, Object.class);
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);

        // Fetch Historial Médico
        model.addAttribute("historiales", fetchList("/v1/historial-medico/animal/" + id));

        // Fetch Info Adopción (si está adoptado)
        try {
            Object[] adopciones = restTemplate.getForObject(apiUrl + "/v1/adopciones/animal/" + id, Object[].class);
            if (adopciones != null && adopciones.length > 0) {
                Map<String, Object> adopcion = (Map<String, Object>) adopciones[0];
                model.addAttribute("adopcion", adopcion);
                
                // Fetch info del adoptante
                Object adoptanteId = adopcion.get("adoptanteId");
                if (adoptanteId != null) {
                    Object adoptante = restTemplate.getForObject(apiUrl + "/v1/adoptantes/" + adoptanteId, Object.class);
                    model.addAttribute("adoptante", adoptante);
                }
            }
        } catch (Exception ignored) {}

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_DETALLE.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    // ─── Helper ────────────────────────────────────────────────────────────────
    private List<Object> fetchList(String path) {
        try {
            Object[] arr = restTemplate.getForObject(apiUrl + path, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) {
            System.err.println("Error llamando a " + path + ": " + e.getMessage());
            return List.of();
        }
    }
}
