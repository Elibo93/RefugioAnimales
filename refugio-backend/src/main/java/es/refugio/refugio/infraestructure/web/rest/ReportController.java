package es.refugio.refugio.infraestructure.web.rest;

import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.adopcion.AdopcionEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.perfil_legal.PerfilLegalEntityJpaRepository;
import es.refugio.refugio.infraestructure.service.pdf.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la generación de informes y documentos legales.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Informes", description = "Endpoints para la generación de documentos PDF")
public class ReportController {

    private final AdopcionEntityJpaRepository adopcionRepository;
    private final PerfilLegalEntityJpaRepository perfilLegalRepository;
    private final PdfService pdfService;

    @Operation(summary = "Generar contrato de adopción", description = "Genera un PDF con el contrato legal de la adopción")
    @GetMapping("/adopcion/{id}/contrato")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public ResponseEntity<byte[]> downloadContrato(@PathVariable Integer id) {
        AdopcionEntity adopcion = adopcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

        PerfilLegalEntity perfil = perfilLegalRepository.findByUsuarioId(adopcion.getAdoptante().getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Perfil legal del adoptante no encontrado"));

        Map<String, Object> data = new HashMap<>();
        data.put("adoptanteNombre", perfil.getNombre());
        data.put("adoptanteApellido", perfil.getApellido());
        data.put("adoptanteDni", perfil.getDni());
        data.put("adoptanteTelefono", perfil.getTelefono());
        data.put("adoptanteDireccion", perfil.getDireccion());

        data.put("animalNombre", adopcion.getAnimal().getNombre());
        data.put("animalEspecie", adopcion.getAnimal().getEspecie());
        data.put("animalRaza", adopcion.getAnimal().getRaza());
        data.put("animalEdad", adopcion.getAnimal().getEdad());
        data.put("animalChip", "N/A"); // Podrías añadir este campo a la entidad animal si existe

        data.put("fechaActual", adopcion.getFechaAdopcion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        byte[] pdfBytes = pdfService.generatePdf("contrato_adopcion", data);

        String filename = "Contrato_Adopcion_" + adopcion.getAnimal().getNombre() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
