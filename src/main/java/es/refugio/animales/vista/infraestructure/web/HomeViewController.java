package es.refugio.animales.vista.infraestructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.refugio.animales.refugio.application.service.persona.FindPersonaService;
import es.refugio.animales.refugio.application.service.adopcion.FindAdopcionService;
import es.refugio.animales.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.animales.refugio.application.service.animal.FindAnimalService;
import es.refugio.animales.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.animales.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.animales.vista.infraestructure.web.enums.ThymTemplates;
import es.refugio.animales.vista.infraestructure.web.enums.FragmentoContenido;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(WebRoutes.HOME)
public class HomeViewController {

    private final FindPersonaService findPersonaService;
    private final FindAnimalService findAnimalService;
    private final FindVoluntarioService findVoluntarioService;
    private final FindAdopcionService findAdopcionService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute(ModelAttribute.Persona_LIST.getName(), findPersonaService.findAll());
        model.addAttribute(ModelAttribute.Animal_LIST.getName(), findAnimalService.findAll());
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), findVoluntarioService.findAll());
        model.addAttribute(ModelAttribute.Adopcion_LIST.getName(), findAdopcionService.findAll());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.HOME_VIEW.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }
}
