package es.refugio.refugio.infraestructure.web.rest;

import es.refugio.refugio.application.service.preferencia.PreferenciaAdopcionService;
import es.refugio.refugio.domain.model.preferencia.PreferenciaAdopcion;
import es.refugio.refugio.infraestructure.mapper.PreferenciaAdopcionMapper;
import es.refugio.refugio.infraestructure.web.dto.preferencia.PreferenciaAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.preferencia.PreferenciaAdopcionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/preferencias")
@RequiredArgsConstructor
@Tag(name = "Preferencias", description = "Gestión de preferencias de adopción de los usuarios")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Preferencia Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class PreferenciaAdopcionController {

    private final PreferenciaAdopcionService service;
    private final PreferenciaAdopcionMapper mapper;

    @Operation(summary = "Obtener preferencias del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<PreferenciaAdopcionResponse> getByUsuarioId(@PathVariable Integer usuarioId) {
        return service.findByUsuarioId(usuarioId)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Guardar preferencias del usuario")
    @PostMapping
    public ResponseEntity<PreferenciaAdopcionResponse> save(@RequestBody PreferenciaAdopcionRequest request) {
        PreferenciaAdopcion domain = mapper.toDomain(request);
        PreferenciaAdopcion saved = service.save(domain);
        return ResponseEntity.ok(mapper.toResponse(saved));
    }
}
