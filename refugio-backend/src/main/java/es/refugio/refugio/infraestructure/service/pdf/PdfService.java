package es.refugio.refugio.infraestructure.service.pdf;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Servicio encargado de la generación de documentos PDF a partir de plantillas HTML.
 */
@Service
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Pdf.
 *
 * @author Elisabeth
 * @author Diego
 */
public class PdfService {

    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Genera un PDF a partir de una plantilla Thymeleaf y un mapa de datos.
     *
     * @param templateName Nombre de la plantilla (sin .html).
     * @param data Mapa con los datos a inyectar en la plantilla.
     * @return Array de bytes con el contenido del PDF.
     */
    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);

        String htmlContent = templateEngine.process("pdf/" + templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
    }
}
