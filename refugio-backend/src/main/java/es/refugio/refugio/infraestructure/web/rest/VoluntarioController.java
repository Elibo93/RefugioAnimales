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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/voluntarios")
@RequiredArgsConstructor
@Tag(name = "Voluntarios", description = "Gestión de voluntarios del refugio")
public class VoluntarioController {

    private final CreateVoluntarioService createVoluntarioService;
    private final FindVoluntarioService findVoluntarioService;
    private final EditVoluntarioService editVoluntarioService;
    private final DeleteVoluntarioService deleteVoluntarioService;

    @Operation(summary = "Crear voluntario", description = "Registra un nuevo voluntario vinculado a un usuario")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Voluntario creado"), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    public ResponseEntity<VoluntarioResponse> create(@Valid @RequestBody VoluntarioRequest request) {
        CreateVoluntarioCommand command = VoluntarioMapper.toCommand(request);
        Voluntario voluntario = createVoluntarioService.createVoluntario(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(VoluntarioMapper.toResponse(voluntario));
    }

    @Operation(summary = "Actualizar voluntario")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Voluntario actualizado"), @ApiResponse(responseCode = "404", description = "No encontrado") })
    @PutMapping("/{id}")
    public ResponseEntity<VoluntarioResponse> update(@PathVariable Integer id,
            @Valid @RequestBody VoluntarioRequest request) {
        EditVoluntarioCommand command = VoluntarioMapper.toCommand(id, request);
        Voluntario voluntario = editVoluntarioService.update(command);
        return ResponseEntity.ok(VoluntarioMapper.toResponse(voluntario));
    }

    @Operation(summary = "Listar voluntarios")
    @ApiResponse(responseCode = "200", description = "Listado retornado")
    @GetMapping
    public List<VoluntarioResponse> findAll() {
        return VoluntarioMapper.toResponse(findVoluntarioService.findAll());
    }

    @Operation(summary = "Obtener voluntario por ID")
    @GetMapping("/{id}")
    public VoluntarioResponse findById(@PathVariable Integer id) {
        return VoluntarioMapper.toResponse(findVoluntarioService.findById(new VoluntarioId(id)));
    }

    @Operation(summary = "Voluntario por usuario", description = "Retorna el voluntario asociado a un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public VoluntarioResponse findByUsuarioId(@PathVariable Integer usuarioId) {
        return VoluntarioMapper.toResponse(findVoluntarioService.findByUsuarioId(new UsuarioId(usuarioId)));
    }

    @Operation(summary = "Eliminar voluntario")
    @ApiResponse(responseCode = "204", description = "Voluntario eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteVoluntarioService.delete(new VoluntarioId(id));
        return ResponseEntity.noContent().build();
    }
}
