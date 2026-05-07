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

import java.util.List;

@RestController
@RequestMapping("/api/v1/perfiles-legales")
@RequiredArgsConstructor
@Tag(name = "Perfiles Legales", description = "Gestión de información legal (DNI, Teléfono) de los usuarios")
public class PerfilLegalController {

    private final PerfilLegalRepository repository;

    @Operation(summary = "Obtener perfil legal por Usuario ID")
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public ResponseEntity<PerfilLegalResponse> getByUsuarioId(@PathVariable Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .map(PerfilLegalMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear o actualizar perfil legal")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerfilLegalResponse> save(@RequestBody PerfilLegalRequest request) {
        PerfilLegal domain = PerfilLegalMapper.toDomain(request);
        
        return repository.findByUsuarioId(domain.getUsuarioId())
                .map(existing -> {
                    domain.setId(existing.getId());
                    PerfilLegal saved = repository.save(domain);
                    return ResponseEntity.ok(PerfilLegalMapper.toResponse(saved));
                })
                .orElseGet(() -> {
                    PerfilLegal saved = repository.save(domain);
                    return ResponseEntity.status(HttpStatus.CREATED).body(PerfilLegalMapper.toResponse(saved));
                });
    }

    @Operation(summary = "Listar todos los perfiles (Admin)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PerfilLegalResponse> getAll() {
        return repository.getAll().stream()
                .map(PerfilLegalMapper::toResponse)
                .toList();
    }
}
