package es.refugio.frontend.web;

import es.refugio.frontend.web.dto.AdopcionRecord;
import es.refugio.frontend.web.dto.AnimalRecord;
import es.refugio.frontend.web.dto.PaginatedResponse;
import es.refugio.frontend.web.dto.UsuarioRecord;
import es.refugio.frontend.web.dto.VoluntarioRecord;
import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.util.ViewControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping({ "/", "/web/home" })
@RequiredArgsConstructor
/**
 * Controlador MVC que gestiona las vistas Thymeleaf y la navegación web para Home.
 *
 * @author Elisabeth
 * @author Diego
 */
public class HomeViewController {

        private final ViewControllerHelper helper;

        @Value("${auth.api.url}")
        private String authUrl;

        @Value("${backend.api.url}")
        private String apiUrl;

        @GetMapping
        public String home(Model model) {
                // Obtener respuestas paginadas para conocer los totales reales de la base de
                // datos
                PaginatedResponse<AnimalRecord> paginatedAnimales = helper.fetchPaginated(apiUrl + "/v1/animales", 1,
                                1000, AnimalRecord.class);
                PaginatedResponse<VoluntarioRecord> paginatedVoluntarios = helper
                                .fetchPaginated(apiUrl + "/v1/voluntarios", 1, 1000, VoluntarioRecord.class);
                PaginatedResponse<AdopcionRecord> paginatedAdopciones = helper.fetchPaginated(apiUrl + "/v1/adopciones",
                                1, 1000, AdopcionRecord.class);

                List<AnimalRecord> animales = paginatedAnimales.items();

                model.addAttribute(ModelAttribute.Persona_LIST.getName(),
                                helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class));
                model.addAttribute(ModelAttribute.Animal_LIST.getName(), animales);
                model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), paginatedVoluntarios.items());
                model.addAttribute(ModelAttribute.Adopcion_LIST.getName(), paginatedAdopciones.items());
                model.addAttribute("especiesActivas", helper.fetchList(apiUrl + "/v1/animales/especies", String.class));

                // Totales reales desde la metadata de paginación de la base de datos
                model.addAttribute("totalAnimales", paginatedAnimales.total());
                model.addAttribute("totalVoluntarios", paginatedVoluntarios.total());
                model.addAttribute("totalAdopciones", paginatedAdopciones.total());

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

                model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                                FragmentoContenido.HOME_VIEW.getPath());
                return ThymTemplates.MAIN_LAYOUT.getPath();
        }
}
