package es.refugio.vista.infraestructure.web;

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
import org.springframework.web.bind.annotation.PathVariable;

import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.refugio.application.service.animal.CreateAnimalService;
import es.refugio.refugio.application.service.animal.DeleteAnimalService;
import es.refugio.refugio.application.service.animal.EditAnimalService;
import es.refugio.refugio.application.service.animal.FindAnimalService;
import es.refugio.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.vista.infraestructure.web.enums.FragmentoContenido;
import es.refugio.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.vista.infraestructure.web.enums.ThymTemplates;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AnimalViewController {

    private final FindAnimalService findAnimalService;
    private final CreateAnimalService createAnimalService;
    private final DeleteAnimalService deleteAnimalService;
    private final EditAnimalService editAnimalService;
    private final FindVoluntarioService findVoluntarioService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.animales_BASE)
    public String listar(Model model, 
                        @RequestParam(required = false) String successMessage,
                        @RequestParam(required = false) EstadoAnimal estado,
                        @RequestParam(required = false) String especie,
                        @RequestParam(required = false) String tamano,
                        @RequestParam(required = false) java.util.List<String> edad,
                        @RequestParam(required = false) String sexo,
                        @RequestParam(required = false) Boolean urgencia,
                        HttpServletRequest request) {
        
        List<Animal> animales;
        if (especie != null || tamano != null || edad != null || sexo != null || urgencia != null) {
            animales = findAnimalService.findFiltered(especie, tamano, edad, sexo, urgencia);
        } else if (estado != null) {
            animales = findAnimalService.findByStatus(estado);
        } else {
            animales = findAnimalService.findAll();
        }

        model.addAttribute(ModelAttribute.Animal_LIST.getName(), animales);
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), findVoluntarioService.findAll());
        model.addAttribute("selectedEstado", estado);
        model.addAttribute("selectedEspecie", especie);
        model.addAttribute("selectedTamano", tamano);
        model.addAttribute("selectedEdad", edad);
        model.addAttribute("selectedSexo", sexo);
        model.addAttribute("selectedUrgencia", urgencia);
        
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }

        if ("true".equals(request.getHeader("HX-Request"))) {
            return FragmentoContenido.Animal_LIST.getPath();
        }

        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.animales_NUEVO)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), Animal.builder().build());
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), findVoluntarioService.findAll());
        model.addAttribute("tamanos", es.refugio.refugio.domain.model.animal.enums.Tamano.values());
        model.addAttribute("sexos", es.refugio.refugio.domain.model.animal.enums.Sexo.values());
        model.addAttribute("estados", es.refugio.refugio.domain.model.animal.enums.EstadoAnimal.values());
        model.addAttribute("especies", es.refugio.refugio.domain.model.animal.enums.Especie.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.animales_NUEVO)
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

        createAnimalService.createAnimal(
                new CreateAnimalCommand(nombre, especie, especiePersonalizada, raza, sexo, chipId, estado, edad, tamano,
                        descripcion, foto, peso, nivelEnergia, urgencia));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Animal creado correctamente");

        return "redirect:" + WebRoutes.animales_BASE;
    }

    @GetMapping(WebRoutes.animales_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Animal animal = findAnimalService.findById(new AnimalId(id));
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);
        model.addAttribute(ModelAttribute.Voluntario_LIST.getName(), findVoluntarioService.findAll());
        model.addAttribute("tamanos", es.refugio.refugio.domain.model.animal.enums.Tamano.values());
        model.addAttribute("sexos", es.refugio.refugio.domain.model.animal.enums.Sexo.values());
        model.addAttribute("estados", es.refugio.refugio.domain.model.animal.enums.EstadoAnimal.values());
        model.addAttribute("especies", es.refugio.refugio.domain.model.animal.enums.Especie.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Animal_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.animales_EDITAR)
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

        editAnimalService.update(
                new EditAnimalCommand(new AnimalId(id), nombre, especie, especiePersonalizada, chipId, estado, edad, tamano,
                        descripcion, foto, peso, nivelEnergia, urgencia));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Animal editado correctamente");

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

    @GetMapping(WebRoutes.animales_BASE + "/{id}/detalle")
    public String detalleModal(@PathVariable Integer id, Model model) {
        Animal animal = findAnimalService.findById(new AnimalId(id));
        model.addAttribute(ModelAttribute.SINGLE_Animal.getName(), animal);
        return "fragments/content/animales-detalle-modal :: detalle";
    }
}
