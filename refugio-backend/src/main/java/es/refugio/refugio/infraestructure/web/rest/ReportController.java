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

import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la generación de informes y documentos legales.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Informes", description = "Endpoints para la generación de documentos PDF")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Report.
 *
 * @author Elisabeth
 * @author Diego
 */
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

        // Cargar logo en Base64
        try {
            String userDir = System.getProperty("user.dir");
            java.io.File logoFile = new java.io.File(
                    userDir + "/refugio-frontend/src/main/resources/static/images/icono_con_eslogan.png");
            if (!logoFile.exists()) {

                logoFile = new java.io.File(
                        userDir + "/../refugio-frontend/src/main/resources/static/images/icono_con_eslogan.png");
            }
            if (!logoFile.exists()) {

                logoFile = new java.io.File(
                        "/home/srromer0/workspace/RefugioAnimales/refugio-frontend/src/main/resources/static/images/icono_con_eslogan.png");
            }
            if (!logoFile.exists()) {

                logoFile = new java.io.File(
                        "/home/ely/workspace/RefugioAnimales/refugio-frontend/src/main/resources/static/images/icono_con_eslogan.png");
            }
            if (logoFile.exists()) {
                byte[] logoBytes = Files.readAllBytes(logoFile.toPath());
                String logoBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(logoBytes);
                data.put("logoBase64", logoBase64);
            }
        } catch (Exception e) {
            // Si falla la carga del logo, el PDF se genera sin él
        }
        data.put("adoptanteNombre", perfil.getNombre());
        data.put("adoptanteApellido", perfil.getApellido());
        data.put("adoptanteDni", perfil.getDni());
        data.put("adoptanteTelefono", perfil.getTelefono());
        data.put("adoptanteDireccion", perfil.getDireccion());

        data.put("animalNombre", adopcion.getAnimal().getNombre());
        data.put("animalEspecie", adopcion.getAnimal().getEspecie());
        data.put("animalEspeciePersonalizada", adopcion.getAnimal().getEspeciePersonalizada());
        data.put("animalRaza", adopcion.getAnimal().getRaza());
        data.put("animalEdad", adopcion.getAnimal().getEdad());
        data.put("animalChip", adopcion.getAnimal().getChipId() != null ? adopcion.getAnimal().getChipId() : "N/A");
        data.put("animalSexo", adopcion.getAnimal().getSexo() != null ? adopcion.getAnimal().getSexo().name() : "-");
        data.put("animalEstado", adopcion.getAnimal().getEstado() != null ? adopcion.getAnimal().getEstado().name() : "-");
        data.put("animalPeso", adopcion.getAnimal().getPeso() != null ? adopcion.getAnimal().getPeso() + " kg" : "-");
        data.put("animalTamano", adopcion.getAnimal().getTamano() != null ? adopcion.getAnimal().getTamano().name() : "-");
        data.put("animalNivelEnergia", adopcion.getAnimal().getNivelEnergia() != null ? adopcion.getAnimal().getNivelEnergia() : "-");
        data.put("animalUrgencia", adopcion.getAnimal().getUrgencia() != null && adopcion.getAnimal().getUrgencia() ? "SÍ" : "NO");
        data.put("animalDescripcion", adopcion.getAnimal().getDescripcion() != null ? adopcion.getAnimal().getDescripcion() : "-");
        data.put("animalFechaIngreso", adopcion.getAnimal().getFechaIngreso() != null ? adopcion.getAnimal().getFechaIngreso().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-");

        data.put("fechaActual", adopcion.getFechaAdopcion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        byte[] pdfBytes = pdfService.generatePdf("contrato_adopcion", data);

        String filename = "Contrato_Adopcion_" + adopcion.getAnimal().getNombre() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(pdfBytes);
    }
}
