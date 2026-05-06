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

    @Value("${backend.api.url}")
    private String apiUrl;

    @GetMapping
    public String home(Model model) {

        // Cada llamada es independiente: si una falla, las demás siguen funcionando
        List<Object> animales = fetch(apiUrl + "/v1/animales");

        model.addAttribute(ModelAttribute.Persona_LIST.getName(),    fetchFull(authUrl + "/v1/usuarios"));
        model.addAttribute(ModelAttribute.Animal_LIST.getName(),     animales);
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), fetch(apiUrl + "/v1/voluntarios"));
        model.addAttribute(ModelAttribute.Adopcion_LIST.getName(),   fetch(apiUrl + "/v1/adopciones"));
        model.addAttribute("especiesActivas", fetch(apiUrl + "/v1/animales/especies"));

        // Top 3 animales más populares basados en visitas
        List<Object> favoritos = animales.stream()
                .filter(a -> a instanceof java.util.Map)
                .map(a -> (java.util.Map<String, Object>) a)
                .sorted((a1, a2) -> {
                    Integer v1 = (Integer) a1.getOrDefault("visitas", 0);
                    Integer v2 = (Integer) a2.getOrDefault("visitas", 0);
                    return v2.compareTo(v1); // Orden descendente
                })
                .limit(3)
                .map(a -> (Object) a)
                .toList();
        
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
