package es.refugio.frontend.web;
import org.springframework.context.i18n.LocaleContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.HttpClientErrorException;
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

import es.refugio.frontend.service.AnimalService;

@Controller
@RequiredArgsConstructor
/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Animal.
 *
 * @author Elisabeth
 * @author Diego
 */
public class AnimalViewController {

    private final AnimalService animalService;
    private final TemplateEngine templateEngine;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping("/web/animales/buscar")
    public String buscarAnimales(@RequestParam(required = false) String animal_q, Model model) {
        try {
            List<AnimalRecord> todos = animalService.fetchAllAnimals();

            List<AnimalRecord> filtrados;
            if (animal_q != null && !animal_q.trim().isEmpty()) {
                String search = animal_q.toLowerCase().trim();
                filtrados = todos.stream()
                        .filter(a -> {
                            String nombre = a.nombre() != null ? a.nombre().toLowerCase() : "";
                            String especie = a.especie() != null ? a.especie().toLowerCase() : "";
                            String raza = a.raza() != null ? a.raza().toLowerCase() : "";
                            String chipId = a.chipId() != null ? a.chipId().toLowerCase() : "";
                            String id = String.valueOf(a.id()).toLowerCase();
                            String estado = a.estado() != null ? a.estado() : "";

                            boolean esApto = estado.equals("DISPONIBLE") || estado.equals("EN_TRATAMIENTO")
                                    || estado.equals("EN_ACOGIDA");

                            return esApto && (nombre.contains(search) || especie.contains(search)
                                    || raza.contains(search) || chipId.contains(search) || id.contains(search));
                        })
                        .limit(10)
                        .toList();
            } else {
                filtrados = todos.stream()
                        .filter(a -> {
                            String estado = a.estado() != null ? a.estado() : "";
                            return "DISPONIBLE".equals(estado) || "EN_TRATAMIENTO".equals(estado);
                        })
                        .limit(10)
                        .toList();
            }

            model.addAttribute("animalesEncontrados", filtrados);
        } catch (Exception e) {
            model.addAttribute("animalesEncontrados", List.of());
        }
        return "fragments/content/animales/animales-sugerencias :: suggestions";
    }

    @GetMapping(WebRoutes.ANIMALES_BASE)
    public String listar(Model model,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String successMessage,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) List<String> edad,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) Boolean urgencia,
            @RequestParam(required = false) Boolean favoritos,
            @RequestParam(required = false) String q,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (response != null)
            response.setHeader("Vary", "HX-Request");

        Integer currentUserId = (Integer) model.getAttribute("currentUserId");
        List<Integer> misFavoritosIds = animalService.fetchMisFavoritosIds(currentUserId);
        model.addAttribute("misFavoritosIds", misFavoritosIds);

        try {
            PaginatedResponse<AnimalRecord> pagination;
            List<AnimalRecord> animalesList;

            if (Boolean.TRUE.equals(favoritos) && !misFavoritosIds.isEmpty()) {
                // Obtenemos todos los animales y filtramos por favoritos
                List<Integer> finalFavs = misFavoritosIds;
                List<AnimalRecord> todosFavoritos = animalService.fetchAllAnimals().stream()
                        .filter(a -> finalFavs.contains(a.id()))
                        .toList();

                int totalFavoritos = todosFavoritos.size();
                int totalPages = (int) Math.ceil((double) totalFavoritos / size);
                int start = (page - 1) * size;
                int end = Math.min(start + size, totalFavoritos);

                if (start < totalFavoritos) {
                    animalesList = todosFavoritos.subList(start, end);
                } else {
                    animalesList = List.of();
                }

                pagination = new PaginatedResponse<>(
                        animalesList,
                        totalPages,       // int totalPages
                        totalFavoritos,   // long total
                        page,             // int page
                        size,             // int pageSize
                        page < totalPages,// boolean hasNext
                        page > 1          // boolean hasPrevious
                );

            } else if (Boolean.TRUE.equals(favoritos)) {
                animalesList = List.of();
                pagination = new PaginatedResponse<>(animalesList, 0, 0, page, size, false, false);
            } else {
                pagination = animalService.fetchPaginatedAnimals(page, size, estado,
                        especie, tamano, edad, sexo, urgencia, q);
                animalesList = pagination.items();
            }

            model.addAttribute(ModelAttribute.Animal_LIST.getName(), animalesList);
            model.addAttribute("pagination", pagination);
        } catch (Exception e) {
            model.addAttribute(ModelAttribute.Animal_LIST.getName(), List.of());
        }

        try {
            List<String> especiesActivas = animalService.fetchEspeciesActivas();
            model.addAttribute("especiesActivas", especiesActivas);
        } catch (Exception e) {
            model.addAttribute("especiesActivas", List.of());
        }

        try {
            List<VoluntarioRecord> voluntarios = helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class);
            model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), voluntarios);
        } catch (Exception e) {
            model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), List.of());
        }

        model.addAttribute("selectedEstado", estado);
        model.addAttribute("selectedEspecie", especie);
        model.addAttribute("selectedTamano", tamano);
        model.addAttribute("selectedEdad", edad);
        model.addAttribute("selectedSexo", sexo);
        model.addAttribute("selectedUrgencia", urgencia);
        model.addAttribute("selectedFavoritos", favoritos);
        model.addAttribute("q", q);

        if (successMessage != null)
            model.addAttribute("successMessage", successMessage);

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Animal_LIST.getPath();
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.ANIMALES_NUEVO)
    @PreAuthorize("hasRole('ADMIN')")
    public String formulario(Model model, HttpServletRequest request) {
        Map<String, Object> animal = new HashMap<>();
        // Preinicializar todas las claves a nulo/valores por defecto para evitar
        // excepciones SpEL
        animal.put("id", null);
        animal.put("nombre", null);
        animal.put("especie", null);
        animal.put("especiePersonalizada", null);
        animal.put("raza", null);
        animal.put("sexo", null);
        animal.put("chipId", null);
        animal.put("fechaIngreso", null);
        animal.put("estado", null);
        animal.put("edad", null);
        animal.put("tamano", null);
        animal.put("peso", null);
        animal.put("nivelEnergia", null);
        animal.put("urgencia", false);
        animal.put("foto", null);
        animal.put("descripcion", null);
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);

        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(),
                helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class));
        model.addAttribute("tamanos", List.of("PEQUEÑO", "MEDIANO", "GRANDE", "GIGANTE"));
        model.addAttribute("sexos", List.of("MACHO", "HEMBRA"));
        model.addAttribute("estados", List.of("DISPONIBLE", "ADOPTADO", "EN_TRATAMIENTO", "RESERVADO"));
        model.addAttribute("especies", List.of("PERRO", "GATO", "ROEDOR", "AVE", "REPTIL", "OTRO"));

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Animal_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ANIMALES_NUEVO)
    @PreAuthorize("hasRole('ADMIN')")
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
            @RequestParam(required = false) String fechaIngreso,
            @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo,
            RedirectAttributes redirectAttributes) {

        String cleanFoto = "";
        if (foto != null && !foto.trim().isEmpty()) {
            String[] parts = foto.split(",");
            for (String p : parts) {
                if (p != null && !p.trim().isEmpty()) {
                    cleanFoto = p.trim();
                    break;
                }
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("nombre", nombre);
        body.put("especie", especie);
        body.put("especiePersonalizada", especiePersonalizada != null ? especiePersonalizada : "");
        body.put("raza", raza);
        body.put("sexo", sexo);
        body.put("chipId", chipId);
        body.put("estado", estado);
        body.put("edad", edad != null ? edad : 0);
        body.put("tamano", tamano != null ? tamano : "");
        body.put("descripcion", descripcion != null ? descripcion : "");
        body.put("foto", cleanFoto);
        body.put("peso", peso != null ? peso : 0.0);
        body.put("nivelEnergia", nivelEnergia != null ? nivelEnergia : 0);
        body.put("urgencia", urgencia != null && urgencia);
        if (fechaIngreso != null && !fechaIngreso.isEmpty()) {
            body.put("fechaIngreso", fechaIngreso);
        }

        try {
            animalService.crearAnimal(body, fotoArchivo);
            redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.animal_creado"));
        } catch (HttpClientErrorException e) {
            throw e;
        }

        return "redirect:" + WebRoutes.ANIMALES_BASE;
    }

    @GetMapping(WebRoutes.ANIMALES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
    public String editarFormulario(@PathVariable Integer id, Model model, HttpServletRequest request) {
        AnimalRecord animal = animalService.fetchAnimalById(id);
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(),
                helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class));
        model.addAttribute("tamanos", List.of("PEQUEÑO", "MEDIANO", "GRANDE", "GIGANTE"));
        model.addAttribute("sexos", List.of("MACHO", "HEMBRA"));
        model.addAttribute("estados", List.of("DISPONIBLE", "ADOPTADO", "EN_TRATAMIENTO", "RESERVADO"));
        model.addAttribute("especies", List.of("PERRO", "GATO", "ROEDOR", "AVE", "REPTIL", "OTRO"));

        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Animal_FORM.getPath() + " :: content";
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.ANIMALES_EDITAR)
    @PreAuthorize("hasRole('ADMIN')")
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
            @RequestParam(required = false) String fechaIngreso,
            @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo,
            RedirectAttributes redirectAttributes) {

        String cleanFoto = "";
        if (foto != null && !foto.trim().isEmpty()) {
            String[] parts = foto.split(",");
            for (String p : parts) {
                if (p != null && !p.trim().isEmpty()) {
                    cleanFoto = p.trim();
                    break;
                }
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("nombre", nombre);
        body.put("especie", especie);
        body.put("especiePersonalizada", especiePersonalizada != null ? especiePersonalizada : "");
        body.put("chipId", chipId);
        body.put("estado", estado);
        body.put("edad", edad != null ? edad : 0);
        body.put("tamano", tamano != null ? tamano : "");
        body.put("descripcion", descripcion != null ? descripcion : "");
        body.put("foto", cleanFoto);
        body.put("peso", peso != null ? peso : 0.0);
        body.put("nivelEnergia", nivelEnergia != null ? nivelEnergia : 0);
        body.put("urgencia", urgencia != null && urgencia);
        if (fechaIngreso != null && !fechaIngreso.isEmpty()) {
            body.put("fechaIngreso", fechaIngreso);
        }

        try {
            animalService.editarAnimal(id, body, fotoArchivo);
        } catch (Exception e) {
            System.err.println("Error editando animal: " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute("successMessage", helper.getMessage("toast.success.animal_editado"));
        return "redirect:" + WebRoutes.ANIMALES_BASE;
    }

    @PostMapping(WebRoutes.ANIMALES_ELIMINAR)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> borrar(@PathVariable Integer id,
            HttpServletRequest request) {
        animalService.eliminarAnimal(id);
        if ("true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.ANIMALES_BASE).build();
    }

    @GetMapping(WebRoutes.ANIMALES_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<AnimalRecord> animales = animalService.fetchAllAnimals();
        Context context = new Context(LocaleContextHolder.getLocale());
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

    @GetMapping(WebRoutes.ANIMALES_EXCEL)
    public void exportarExcel(HttpServletResponse response) throws Exception {
        List<AnimalRecord> animales = animalService.fetchAllAnimals();
        byte[] excelBytes = ExcelExportHelper.exportToExcel(
                "Animales",
                List.of("ID", "Nombre", "Especie", "Raza", "Sexo", "Chip ID", "Estado", "Edad", "Tamaño", "Peso (kg)",
                        "Nivel Energía", "Urgente", "Visitas", "Conteo Solicitudes", "Descripción", "Fecha Ingreso"),
                animales,
                List.of(
                        AnimalRecord::id,
                        AnimalRecord::nombre,
                        a -> "OTRO".equals(a.especie()) ? a.especiePersonalizada() : a.especie(),
                        AnimalRecord::raza,
                        AnimalRecord::sexo,
                        AnimalRecord::chipId,
                        AnimalRecord::estado,
                        a -> a.edad() != null ? a.edad() : "-",
                        a -> a.tamano() != null ? a.tamano() : "-",
                        a -> a.peso() != null ? a.peso() : "-",
                        a -> a.nivelEnergia() != null ? a.nivelEnergia() : "-",
                        a -> a.urgencia() != null && a.urgencia() ? "SÍ" : "NO",
                        a -> a.visitas() != null ? a.visitas() : 0,
                        a -> a.conteoSolicitudes() != null ? a.conteoSolicitudes() : 0,
                        a -> a.descripcion() != null ? a.descripcion() : "",
                        a -> a.fechaIngreso() != null ? a.fechaIngreso().toString() : "-"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=animales.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(excelBytes);
        }
    }

    @GetMapping(WebRoutes.ANIMALES_BASE + "/{id}/detalle")
    public String detalleModal(@PathVariable Integer id, Model model) {
        Object isAdminObj = model.getAttribute("isAdmin");
        boolean isAdmin = isAdminObj != null && (Boolean) isAdminObj;
        if (!isAdmin) {
            animalService.registrarVisita(id);
        }

        AnimalRecord animal = animalService.fetchAnimalById(id);
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);

        model.addAttribute("favoritosCount", animalService.countFavoritos(id));

        return "fragments/content/animales/animales-detalle-modal :: detalle";
    }

    @SuppressWarnings("rawtypes")
    @GetMapping(WebRoutes.ANIMALES_DETALLE)
    public String verDetalle(@PathVariable Integer id, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        if (response != null)
            response.setHeader("Vary", "HX-Request");

        Object isAdminObj = model.getAttribute("isAdmin");
        boolean isAdmin = isAdminObj != null && (Boolean) isAdminObj;
        if (!isAdmin) {
            animalService.registrarVisita(id);
        }

        AnimalRecord animal = animalService.fetchAnimalById(id);
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);

        model.addAttribute("favoritosCount", animalService.countFavoritos(id));

        model.addAttribute("historiales", helper.fetchList(apiUrl + "/v1/historial-medico/animal/" + id, Map.class));

        try {
            List<AdopcionRecord> adopciones = helper.fetchList(apiUrl + "/v1/adopciones/animal/" + id,
                    AdopcionRecord.class);
            if (adopciones != null && !adopciones.isEmpty()) {
                AdopcionRecord adopcion = adopciones.get(0);

                Map<String, Object> adopcionMap = new HashMap<>();
                adopcionMap.put("id", adopcion.id());
                adopcionMap.put("animalId", adopcion.animalId());
                adopcionMap.put("adoptanteId", adopcion.adoptanteId());
                adopcionMap.put("solicitudAdopcionId", adopcion.solicitudAdopcionId());
                adopcionMap.put("fechaAdopcion", adopcion.fechaAdopcion());
                adopcionMap.put("estado", adopcion.estado());
                adopcionMap.put("contrato", adopcion.contrato());
                model.addAttribute("adopcion", adopcionMap);

                Integer adoptanteId = adopcion.adoptanteId();
                if (adoptanteId != null) {
                    AdoptanteRecord adoptante = helper.fetchObject(apiUrl + "/v1/adoptantes/" + adoptanteId,
                            AdoptanteRecord.class);
                    if (adoptante != null) {
                        Map<String, Object> adoptanteMap = new HashMap<>();
                        adoptanteMap.put("id", adoptante.id());
                        adoptanteMap.put("usuarioId", adoptante.usuarioId());
                        adoptanteMap.put("estadoValidacion", adoptante.estadoValidacion());
                        adoptanteMap.put("nombre", null);
                        adoptanteMap.put("apellido", null);
                        adoptanteMap.put("telefono", null);
                        adoptanteMap.put("direccion", null);

                        if (adoptante.usuarioId() != null) {
                            try {
                                PerfilLegalRecord legal = helper.fetchObject(
                                        apiUrl + "/v1/perfiles-legales/usuario/" + adoptante.usuarioId(),
                                        PerfilLegalRecord.class);
                                if (legal != null) {
                                    adoptanteMap.put("nombre", legal.nombre());
                                    adoptanteMap.put("apellido", legal.apellido());
                                    adoptanteMap.put("telefono", legal.telefono());
                                    adoptanteMap.put("direccion", legal.direccion());
                                }
                            } catch (Exception e) {
                                System.err.println("Error fetching PerfilLegal for adoptante: " + e.getMessage());
                            }
                        }
                        model.addAttribute("adoptante", adoptanteMap);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching adopcion/adoptante: " + e.getMessage());
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_DETALLE.getPath());

        if (request != null && "true".equals(request.getHeader("HX-Request"))
                && !"true".equals(request.getHeader("HX-History-Restore-Request"))) {
            return FragmentoContenido.Animal_DETALLE.getPath() + " :: content";
        }
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    // The fetchPaginatedAnimals was moved to AnimalService

    @PostMapping("/web/animales/{id}/favorito")
    @ResponseBody
    public String toggleFavorito(@PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "detalle") String source, Model model) {
        Integer currentUserId = (Integer) model.getAttribute("currentUserId");
        if (currentUserId == null)
            return "";

        try {
            Boolean isFavorito = animalService.toggleFavorito(id, currentUserId);

            String svgSize = "lista".equals(source) ? "20" : "24";
            String svg = Boolean.TRUE.equals(isFavorito)
                    ? "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + svgSize + "\" height=\"" + svgSize
                            + "\" viewBox=\"0 0 24 24\" fill=\"#ef4444\" stroke=\"#ef4444\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z\"></path></svg>"
                    : "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + svgSize + "\" height=\"" + svgSize
                            + "\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z\"></path></svg>";

            String baseClass = "lista".equals(source) ? "btn-favorito" : "btn-favorito-detalle";
            String btnClass = Boolean.TRUE.equals(isFavorito) ? baseClass + " active" : baseClass;

            return "<button type=\"button\" class=\"" + btnClass + "\" hx-post=\"/web/animales/" + id
                    + "/favorito?source=" + source + "\" hx-swap=\"outerHTML\">" + svg + "</button>";
        } catch (Exception e) {
            return "";
        }
    }
}
