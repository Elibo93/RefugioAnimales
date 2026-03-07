package es.refugio.animales.vista.infraestructure.web;

import java.io.OutputStream;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.refugio.animales.refugio.application.service.animal.DeleteAnimalService;
import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import org.springframework.web.bind.annotation.PathVariable;

import es.refugio.animales.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.animales.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.animales.refugio.application.service.animal.CreateAnimalService;
import es.refugio.animales.refugio.application.service.animal.FindAnimalService;
import es.refugio.animales.refugio.domain.model.animal.Animal;
import es.refugio.animales.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.animales.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.animales.vista.infraestructure.web.enums.ThymTemplates;
import es.refugio.animales.vista.infraestructure.web.enums.FragmentoContenido;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AnimalViewController {

    private final FindAnimalService findAnimalService;
    private final CreateAnimalService createAnimalService;
    private final DeleteAnimalService deleteAnimalService;
    private final FindVoluntarioService findVoluntarioService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.animales_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Animal_LIST.getName(), findAnimalService.findAll());
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), findVoluntarioService.findAll());
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.animales_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), Animal.builder().build());
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), findVoluntarioService.findAll());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.animales_NUEVO)
    public String crearAnimal(@RequestParam String nombre,
            @RequestParam String especie,
            @RequestParam String raza,
            @RequestParam String sexo,
            @RequestParam String chipId,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {

        createAnimalService.createAnimal(
                new CreateAnimalCommand(nombre, especie, raza, sexo, chipId, estado));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Animal creado correctamente");

        return "redirect:" + WebRoutes.animales_BASE;
    }

    @PostMapping(WebRoutes.animales_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        deleteAnimalService.delete(new AnimalId(id));

        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Animal eliminado correctamente");

        return ResponseEntity.status(302)
                .header("Location", WebRoutes.animales_BASE)
                .build();
    }

    @GetMapping(WebRoutes.animales_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Animal> animales = findAnimalService.findAll();
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
}
