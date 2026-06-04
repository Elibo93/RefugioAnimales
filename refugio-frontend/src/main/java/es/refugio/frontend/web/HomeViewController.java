package es.refugio.frontend.web;

import es.refugio.frontend.web.dto.AdopcionRecord;
import es.refugio.frontend.web.dto.AnimalRecord;
import es.refugio.frontend.web.dto.PaginatedResponse;
import es.refugio.frontend.web.dto.VoluntarioRecord;
import es.refugio.frontend.web.dto.DonacionRecord;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.service.AnimalService;
import es.refugio.frontend.service.VoluntarioService;
import es.refugio.frontend.service.AdopcionService;
import es.refugio.frontend.service.UsuarioService;
import es.refugio.frontend.service.DonacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Home.
 *
 * @author Elisabeth
 * @author Diego
 */
@Controller
@RequestMapping({ "/", "/web/home" })
@RequiredArgsConstructor
public class HomeViewController {

    private final AnimalService animalService;
    private final VoluntarioService voluntarioService;
    private final AdopcionService adopcionService;
    private final UsuarioService usuarioService;
    private final DonacionService donacionService;
    private final es.refugio.frontend.service.TareaService tareaService;

    @GetMapping
    public String home(Model model) {
        // Obtener respuestas paginadas para conocer los totales reales de la base de
        // datos
        PaginatedResponse<AnimalRecord> paginatedAnimales = animalService.fetchPaginatedAnimals(1, 1000, "ALL", "ALL", "ALL", null, "ALL", null, "");
        PaginatedResponse<VoluntarioRecord> paginatedVoluntarios = voluntarioService.fetchPaginatedVoluntarios(1, 1000, null);
        PaginatedResponse<AdopcionRecord> paginatedAdopciones = adopcionService.fetchPaginatedAdopciones(1, 1000, null, null);

        List<AnimalRecord> animales = paginatedAnimales.items();

        model.addAttribute(ModelAttribute.Persona_LIST.getName(), usuarioService.fetchAllUsuarios());
        model.addAttribute(ModelAttribute.Animal_LIST.getName(), animales);
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), paginatedVoluntarios.items());
        model.addAttribute(ModelAttribute.Adopcion_LIST.getName(), paginatedAdopciones.items());
        model.addAttribute("especiesActivas", animalService.fetchEspeciesActivas());

        // Totales reales desde la metadata de paginación de la base de datos
        model.addAttribute("totalAnimales", paginatedAnimales.total());
        model.addAttribute("totalVoluntarios", paginatedVoluntarios.total());
        model.addAttribute("totalAdopciones", paginatedAdopciones.total());

        // Calcular datos para las Alertas Críticas (Atención Requerida)
        long animalesEnTratamientoCount = animales.stream()
                .filter(a -> "EN_TRATAMIENTO".equals(a.estado()))
                .count();
        
        long tareasVencidasCount = tareaService.fetchAllTareas().stream()
                .filter(t -> t.fechaLimite() != null 
                          && t.fechaLimite().isBefore(LocalDateTime.now()) 
                          && !"COMPLETADA".equals(t.estado()) 
                          && !"CANCELADA".equals(t.estado()))
                .count();

        model.addAttribute("animalesEnTratamientoCount", animalesEnTratamientoCount);
        model.addAttribute("tareasVencidasCount", tareasVencidasCount);

        // Calcular total de donaciones en el último mes (últimos 30 días) que sean de dinero
        LocalDateTime haceUnMes = LocalDateTime.now().minusDays(30);
        double totalDonacionesMes = donacionService.fetchAllDonaciones().stream()
                .filter(d -> d.fecha() != null && d.fecha().isAfter(haceUnMes) && d.cantidad() != null && "DINERO".equals(d.tipo()))
                .mapToDouble(DonacionRecord::cantidad)
                .sum();
        
        // Redondear a 2 decimales para mostrarlo bien en la vista
        model.addAttribute("totalDonacionesMes", Math.round(totalDonacionesMes * 100.0) / 100.0);

        // Top 3 animales más populares basados en visitas
        List<AnimalRecord> favoritos = animales.stream()
                .sorted((a1, a2) -> {
                        Integer v1 = a1.visitas() != null ? a1.visitas() : 0;
                        Integer v2 = a2.visitas() != null ? a2.visitas() : 0;
                        return v2.compareTo(v1); // Orden descendente
                })
                .limit(3)
                .toList();

        model.addAttribute("favoritos", favoritos);

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.HOME_VIEW.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }
}
