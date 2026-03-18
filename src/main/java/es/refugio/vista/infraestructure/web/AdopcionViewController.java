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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.application.service.adopcion.CreateAdopcionService;
import es.refugio.refugio.application.service.adopcion.DeleteAdopcionService;
import es.refugio.refugio.application.service.adopcion.EditAdopcionService;
import es.refugio.refugio.application.service.adopcion.FindAdopcionService;
import es.refugio.refugio.application.service.animal.FindAnimalService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.vista.infraestructure.web.enums.FragmentoContenido;
import es.refugio.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.vista.infraestructure.web.enums.ThymTemplates;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdopcionViewController {

        private final FindAdopcionService findAdopcionService;
        private final CreateAdopcionService createAdopcionService;
        private final DeleteAdopcionService deleteAdopcionService;
        private final EditAdopcionService editAdopcionService;
        private final FindUsuarioService findPersonaService;
        private final FindAnimalService findAnimalService;

        private final TemplateEngine templateEngine;

        @GetMapping(WebRoutes.adopciones_PDF)
        public void exportarPDF(HttpServletResponse response) throws Exception {
                List<Adopcion> adopciones = findAdopcionService.findAll();
                Context context = new Context();
                context.setVariable("adopciones", adopciones);
                java.util.Map<Integer, Usuario> personasMap = findPersonaService
                                .findAll().stream()
                                .collect(java.util.stream.Collectors.toMap(a -> a.getId().getValue(), a -> a));

                java.util.Map<Integer, Animal> animalesMap = findAnimalService
                                .findAll().stream()
                                .collect(java.util.stream.Collectors.toMap(t -> t.getId().getValue(), t -> t));

                context.setVariable("personas", personasMap);
                context.setVariable("animales", animalesMap);
                String htmlContent = templateEngine.process(ThymTemplates.Adopcion_LIST_PDF.getPath(), context);
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=adopciones.pdf");
                OutputStream outputStream = response.getOutputStream();
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);

                outputStream.close();
        }

        @GetMapping(WebRoutes.adopciones_BASE)
        public String listar(Model model,
                        @RequestParam(required = false) Integer personaId,
                        @RequestParam(required = false) Integer animalId,
                        @RequestParam(required = false) String successMessage) {

                List<Adopcion> adopciones = findAdopcionService.findByCriteria(personaId, animalId);
                model.addAttribute(ModelAttribute.Adopcion_LIST.getName(), adopciones);
                model.addAttribute("selectedPersonaId", personaId);
                model.addAttribute("selectedAnimalId", animalId);
                java.util.Map<Integer, Usuario> personasMap = findPersonaService
                                .findAll().stream()
                                .collect(java.util.stream.Collectors.toMap(a -> a.getId().getValue(), a -> a));

                java.util.Map<Integer, Animal> animalesMap = findAnimalService
                                .findAll().stream()
                                .collect(java.util.stream.Collectors.toMap(t -> t.getId().getValue(), t -> t));

                model.addAttribute("personas", personasMap);
                model.addAttribute("animales", animalesMap);
                model.addAttribute("listapersonas", findPersonaService.findAll());
                model.addAttribute("listaanimales", findAnimalService.findAll());

                if (successMessage != null) {
                        model.addAttribute("successMessage", successMessage);
                }

                model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                                FragmentoContenido.Adopcion_LIST.getPath());
                return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        @GetMapping(WebRoutes.adopciones_NUEVA)
        public String formulario(Model model) {
                model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), Adopcion.builder().build());
                model.addAttribute(ModelAttribute.Persona_LIST.getName(), findPersonaService.findAll());
                model.addAttribute(ModelAttribute.Animal_LIST.getName(), findAnimalService.findAll());
                model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                                FragmentoContenido.Adopcion_FORM.getPath());
                return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        @PostMapping(WebRoutes.adopciones_NUEVA)
        public String crearAdopcion(@RequestParam Integer idPersona,
                        @RequestParam Integer idAnimal,
                        RedirectAttributes redirectAttributes) {

                createAdopcionService.createAdopcion(
                                new CreateAdopcionCommand(new UsuarioId(idPersona), new AnimalId(idAnimal)));

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                "Adopcion creada correctamente");

                return "redirect:" + WebRoutes.adopciones_BASE;
        }

        @GetMapping(WebRoutes.adopciones_EDITAR)
        public String editarFormulario(@PathVariable Integer id, Model model) {
                Adopcion adopcion = findAdopcionService.findById(new AdopcionId(id));
                model.addAttribute(ModelAttribute.SINGLE_Adopcion.getName(), adopcion);
                model.addAttribute(ModelAttribute.Persona_LIST.getName(), findPersonaService.findAll());
                model.addAttribute(ModelAttribute.Animal_LIST.getName(), findAnimalService.findAll());
                model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(),
                                FragmentoContenido.Adopcion_FORM.getPath());
                return ThymTemplates.MAIN_LAYOUT.getPath();
        }

        @PostMapping(WebRoutes.adopciones_EDITAR)
        public String procesarEdicion(@PathVariable Integer id,
                        @RequestParam Integer idPersona,
                        @RequestParam Integer idAnimal,
                        RedirectAttributes redirectAttributes) {

                editAdopcionService.update(
                                new EditAdopcionCommand(new AdopcionId(id), new UsuarioId(idPersona),
                                                new AnimalId(idAnimal)));

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                "Adopcion editada correctamente");

                return "redirect:" + WebRoutes.adopciones_BASE;
        }

        @PostMapping(WebRoutes.adopciones_ELIMINAR)
        @ResponseBody
        public ResponseEntity<String> borrar(@PathVariable Integer id, RedirectAttributes redirectAttributes,
                        HttpServletRequest request) {

                deleteAdopcionService.delete(new AdopcionId(id));

                if ("true".equals(request.getHeader("HX-Request"))) {
                        return ResponseEntity.ok("");
                }

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                "Adopcion eliminada correctamente");

                return ResponseEntity.status(302)
                                .header("Location", WebRoutes.adopciones_BASE)
                                .build();
        }
}
