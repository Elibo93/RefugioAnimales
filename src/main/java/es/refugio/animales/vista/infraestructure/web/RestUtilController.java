package es.refugio.animales.vista.infraestructure.web;

import java.sql.Time;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestUtilController {

    @GetMapping("/api/hora")
    public String getHora(Model model) {
        return new Time(System.currentTimeMillis()).toString();
    }
}
















