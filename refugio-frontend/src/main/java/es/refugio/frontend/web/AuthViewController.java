package es.refugio.frontend.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.refugio.frontend.web.enums.ThymTemplates;
import es.refugio.frontend.web.enums.ModelAttribute;

@Controller
public class AuthViewController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("currentUri", "/login");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/login");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        // No enlazamos un DTO al modelo porque la validación se manejará 
        // de forma nativa con HTML5 en el frontend o redirigiendo errores mapeados si es necesario.
        // Por simplicidad, exclusivamente retornamos la vista del formulario.
        model.addAttribute("currentUri", "/registro");
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), "fragments/content/registro");
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }
}
