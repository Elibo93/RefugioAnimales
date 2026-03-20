package es.refugio.vista.infraestructure.web;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.application.service.donacion.CreateDonacionService;
import es.refugio.refugio.application.service.donacion.DeleteDonacionService;
import es.refugio.refugio.application.service.donacion.EditDonacionService;
import es.refugio.refugio.application.service.donacion.FindDonacionService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import es.refugio.vista.infraestructure.web.enums.FragmentoContenido;
import es.refugio.vista.infraestructure.web.enums.ModelAttribute;
import es.refugio.vista.infraestructure.web.enums.ThymTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DonacionViewController {

    private final FindDonacionService findDonacionService;
    private final CreateDonacionService createDonacionService;
    private final DeleteDonacionService deleteDonacionService;
    private final EditDonacionService editDonacionService;
    private final FindUsuarioService findUsuarioService;

    private final TemplateEngine templateEngine;

    @GetMapping(WebRoutes.donaciones_BASE)
    public String listar(Model model, @RequestParam(required = false) String successMessage) {
        model.addAttribute(ModelAttribute.Donacion_LIST.getName(), findDonacionService.findAll());
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_LIST.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @GetMapping(WebRoutes.donaciones_NUEVA)
    public String formulario(Model model) {
        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), Donacion.builder().fecha(LocalDateTime.now()).build());
        model.addAttribute("usuarios", findUsuarioService.findAll());
        model.addAttribute("tipos", TipoDonacion.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.donaciones_NUEVA)
    public String crear(@RequestParam Integer usuarioId,
                        @RequestParam String tipo,
                        @RequestParam Double cantidad,
                        @RequestParam String descripcion,
                        RedirectAttributes redirectAttributes) {

        createDonacionService.create(new CreateDonacionCommand(usuarioId, tipo, cantidad, LocalDateTime.now(), descripcion));
        redirectAttributes.addFlashAttribute("successMessage", "Donación registrada correctamente");
        return "redirect:" + WebRoutes.donaciones_BASE;
    }

    @GetMapping(WebRoutes.donaciones_EDITAR)
    public String editarFormulario(@PathVariable Integer id, Model model) {
        Donacion donacion = findDonacionService.findById(new DonacionId(id));
        model.addAttribute(ModelAttribute.SINGLE_Donacion.getName(), donacion);
        model.addAttribute("usuarios", findUsuarioService.findAll());
        model.addAttribute("tipos", TipoDonacion.values());
        model.addAttribute(ModelAttribute.FRAGMENTO_CONTENIDO.getName(), FragmentoContenido.Donacion_FORM.getPath());
        return ThymTemplates.MAIN_LAYOUT.getPath();
    }

    @PostMapping(WebRoutes.donaciones_EDITAR)
    public String procesarEdicion(@PathVariable Integer id,
                                 @RequestParam Integer usuarioId,
                                 @RequestParam String tipo,
                                 @RequestParam Double cantidad,
                                 @RequestParam String descripcion,
                                 RedirectAttributes redirectAttributes) {

        editDonacionService.update(new EditDonacionCommand(new DonacionId(id), usuarioId, tipo, cantidad, LocalDateTime.now(), descripcion));
        redirectAttributes.addFlashAttribute("successMessage", "Donación editada correctamente");
        return "redirect:" + WebRoutes.donaciones_BASE;
    }

    @PostMapping(WebRoutes.donaciones_ELIMINAR)
    @ResponseBody
    public ResponseEntity<String> borrar(@PathVariable Integer id, HttpServletRequest request) {
        deleteDonacionService.delete(new DonacionId(id));
        if ("true".equals(request.getHeader("HX-Request"))) {
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(302).header("Location", WebRoutes.donaciones_BASE).build();
    }

    @GetMapping(WebRoutes.donaciones_PDF)
    public void exportarPDF(HttpServletResponse response) throws Exception {
        List<Donacion> donaciones = findDonacionService.findAll();
        Context context = new Context();
        context.setVariable("donaciones", donaciones);
        String htmlContent = templateEngine.process(ThymTemplates.Donacion_LIST_PDF.getPath(), context);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=donaciones.pdf");
        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
    }
}
