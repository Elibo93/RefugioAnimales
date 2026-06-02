package es.refugio.refugio.infraestructure.web.rest;

import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.infraestructure.mapper.PerfilLegalMapper;
import es.refugio.refugio.infraestructure.web.dto.perfil_legal.PerfilLegalRequest;
import es.refugio.refugio.infraestructure.web.dto.perfil_legal.PerfilLegalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/perfiles-legales")
@RequiredArgsConstructor
@Tag(name = "Perfiles Legales", description = "Gestión de información legal (DNI, Teléfono) de los usuarios")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Perfil Legal.
 *
 * @author Elisabeth
 * @author Diego
 */
public class PerfilLegalController {

    private final PerfilLegalRepository repository;
    private final PerfilLegalMapper perfilLegalMapper;

    @Operation(summary = "Obtener perfil legal por Usuario ID")
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE', 'ROLE_PUBLICO', 'PUBLICO')")
    public ResponseEntity<PerfilLegalResponse> getByUsuarioId(@PathVariable Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .map(perfilLegalMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear o actualizar perfil legal")
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<PerfilLegalResponse> save(@Valid @RequestBody PerfilLegalRequest request) {
        PerfilLegal domain = perfilLegalMapper.toDomain(request);
        
        return repository.findByUsuarioId(domain.getUsuarioId())
                .map(existing -> {
                    domain.setId(existing.getId());
                    PerfilLegal saved = repository.save(domain);
                    return ResponseEntity.ok(perfilLegalMapper.toResponse(saved));
                })
                .orElseGet(() -> {
                    PerfilLegal saved = repository.save(domain);
                    return ResponseEntity.status(HttpStatus.CREATED).body(perfilLegalMapper.toResponse(saved));
                });
    }

    @Operation(summary = "Listar todos los perfiles (Admin)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PerfilLegalResponse> getAll() {
        return repository.getAll().stream()
                .map(perfilLegalMapper::toResponse)
                .toList();
    }
}
