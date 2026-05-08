package es.refugio.refugio.infraestructure.web.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;
import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.application.service.voluntario.CreateVoluntarioService;
import es.refugio.refugio.application.service.voluntario.DeleteVoluntarioService;
import es.refugio.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.infraestructure.mapper.VoluntarioMapper;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioResponse;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioUpdateRequest;

import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/voluntarios")
@RequiredArgsConstructor
@Tag(name = "Voluntarios", description = "Gestión de voluntarios del refugio")
public class VoluntarioController {

    private final CreateVoluntarioService createVoluntarioService;
    private final FindVoluntarioService findVoluntarioService;
    private final EditVoluntarioService editVoluntarioService;
    private final DeleteVoluntarioService deleteVoluntarioService;
    private final PerfilLegalRepository perfilLegalRepository;

    @Operation(summary = "Crear voluntario", description = "Registra un nuevo voluntario vinculado a un usuario")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Voluntario creado"), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    public ResponseEntity<VoluntarioResponse> create(@Valid @RequestBody VoluntarioRequest request) {
        // Asegurar PerfilLegal
        perfilLegalRepository.findByUsuarioId(request.usuarioId())
                .map(existing -> {
                    if (request.nombre() != null) existing.setNombre(request.nombre());
                    if (request.apellido() != null) existing.setApellido(request.apellido());
                    if (request.dni() != null) existing.setDni(request.dni());
                    if (request.telefono() != null) existing.setTelefono(request.telefono());
                    if (request.direccion() != null) existing.setDireccion(request.direccion());
                    return perfilLegalRepository.save(existing);
                })
                .orElseGet(() -> perfilLegalRepository.save(PerfilLegal.builder()
                        .usuarioId(request.usuarioId())
                        .nombre(request.nombre() != null ? request.nombre() : "")
                        .apellido(request.apellido() != null ? request.apellido() : "")
                        .dni(request.dni() != null ? request.dni() : "")
                        .telefono(request.telefono() != null ? request.telefono() : "")
                        .direccion(request.direccion() != null ? request.direccion() : "")
                        .build()));

        CreateVoluntarioCommand command = VoluntarioMapper.toCommand(request);
        Voluntario voluntario = createVoluntarioService.createVoluntario(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(VoluntarioMapper.toResponse(voluntario));
    }

    @Operation(summary = "Actualizar voluntario")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Voluntario actualizado"), @ApiResponse(responseCode = "404", description = "No encontrado") })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VoluntarioResponse> update(@PathVariable Integer id,
            @Valid @RequestBody VoluntarioUpdateRequest request) {
        EditVoluntarioCommand command = VoluntarioMapper.toCommand(id, request);
        Voluntario voluntario = editVoluntarioService.update(command);
        return ResponseEntity.ok(VoluntarioMapper.toResponse(voluntario));
    }

    @Operation(summary = "Listar voluntarios")
    @ApiResponse(responseCode = "200", description = "Listado retornado")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<VoluntarioResponse> findAll() {
        return VoluntarioMapper.toResponse(findVoluntarioService.findAll());
    }

    @Operation(summary = "Obtener voluntario por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @GetMapping("/{id}")
    public VoluntarioResponse findById(@PathVariable Integer id) {
        return VoluntarioMapper.toResponse(findVoluntarioService.findById(new VoluntarioId(id)));
    }

    @Operation(summary = "Voluntario por usuario", description = "Retorna el voluntario asociado a un usuario")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @GetMapping("/usuario/{usuarioId}")
    public VoluntarioResponse findByUsuarioId(@PathVariable Integer usuarioId) {
        return VoluntarioMapper.toResponse(findVoluntarioService.findByUsuarioId(new UsuarioId(usuarioId)));
    }

    @Operation(summary = "Eliminar voluntario")
    @ApiResponse(responseCode = "204", description = "Voluntario eliminado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteVoluntarioService.delete(new VoluntarioId(id));
        return ResponseEntity.noContent().build();
    }
}
