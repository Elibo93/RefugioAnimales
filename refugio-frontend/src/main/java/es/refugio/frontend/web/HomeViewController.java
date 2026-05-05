package es.refugio.frontend.web;

import es.refugio.frontend.web.enums.FragmentoContenido;
import es.refugio.frontend.web.enums.ModelAttribute;
import es.refugio.frontend.web.enums.ThymTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/", "/web/home"})
@RequiredArgsConstructor
public class HomeViewController {

    private final RestTemplate restTemplate;

    @Value("${auth.api.url}")
    private String authUrl;

    @GetMapping
    public String home(Model model) {

        // Cada llamada es independiente: si una falla, las demás siguen funcionando
        List<Object> animales = fetch("/v1/animales");

        model.addAttribute(ModelAttribute.Persona_LIST.getName(),    fetchFull(authUrl + "/v1/usuarios"));
        model.addAttribute(ModelAttribute.Animal_LIST.getName(),     animales);
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), fetch("/v1/voluntarios"));
        model.addAttribute(ModelAttribute.Adopcion_LIST.getName(),   fetch("/v1/adopciones"));
        model.addAttribute("especiesActivas", fetch("/v1/animales/especies"));

        // Top 3 animales como "favoritos destacados"
        List<Object> favoritos = animales.size() > 3 ? animales.subList(0, 3) : animales;
        model.addAttribute("favoritos", favoritos);

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.HOME_VIEW.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    private List<Object> fetch(String path) {
        try {
            Object[] arr = restTemplate.getForObject(path, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<Object> fetchFull(String fullUrl) {
        try {
            Object[] arr = restTemplate.getForObject(fullUrl, Object[].class);
            return arr != null ? Arrays.asList(arr) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
