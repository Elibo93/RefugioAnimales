package es.refugio.refugio.infraestructure.web.rest;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.refugio.common.infraestructure.web.dto.common.PaginatedResponse;

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

import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.application.command.tarea.EditTareaCommand;
import es.refugio.refugio.application.service.tarea.CreateTareaService;
import es.refugio.refugio.application.service.tarea.DeleteTareaService;
import es.refugio.refugio.application.service.tarea.EditTareaService;
import es.refugio.refugio.application.service.tarea.FindTareaService;
import es.refugio.refugio.application.service.tarea.FindTareaHistorialService;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.infraestructure.mapper.TareaMapper;
import es.refugio.refugio.infraestructure.mapper.TareaHistorialMapper;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaRequest;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaResponse;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaHistorialResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/tareas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
@Tag(name = "Tareas", description = "Gestión de tareas asignadas a los voluntarios")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Tarea.
 *
 * @author Elisabeth
 * @author Diego
 */
public class TareaController {

    private final CreateTareaService createTareaService;
    private final FindTareaService findTareaService;
    private final TareaMapper tareaMapper;
    private final TareaHistorialMapper tareaHistorialMapper;
    private final EditTareaService editTareaService;
    private final DeleteTareaService deleteTareaService;
    private final FindTareaHistorialService findTareaHistorialService;
    private final PerfilLegalRepository perfilLegalRepository;

    @Operation(summary = "Obtener historial de una tarea")
    @GetMapping("/{id}/historial")
    public List<TareaHistorialResponse> getHistorial(@PathVariable Integer id) {
        return findTareaHistorialService.findByTareaId(new TareaId(id)).stream()
                .map(h -> {
                    String nombre = "Sistema";
                    if (h.getUsuarioId() != null) {
                        nombre = perfilLegalRepository.findByUsuarioId(h.getUsuarioId())
                                .map(p -> p.getNombre() + " " + p.getApellido())
                                .orElse("Usuario #" + h.getUsuarioId());
                    }
                    return tareaHistorialMapper.toResponse(h, nombre);
                })
                .toList();
    }

    @Operation(summary = "Crear tarea")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Tarea creada"), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    public ResponseEntity<TareaResponse> create(@Valid @RequestBody TareaRequest request) {
        CreateTareaCommand command = tareaMapper.toCommand(request);
        Tarea tarea = createTareaService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaMapper.toResponse(tarea));
    }

    @Operation(summary = "Actualizar tarea")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Tarea actualizada"), @ApiResponse(responseCode = "404", description = "No encontrada") })
    @PutMapping("/{id}")
    public ResponseEntity<TareaResponse> update(@PathVariable Integer id, @Valid @RequestBody TareaRequest request) {
        EditTareaCommand command = tareaMapper.toCommand(id, request);
        Tarea tarea = editTareaService.update(command);
        return ResponseEntity.ok(tareaMapper.toResponse(tarea));
    }

    @Operation(summary = "Listar tareas")
    @GetMapping
    public PaginatedResponse<TareaResponse> findAll(Pageable pageable) {
        Page<Tarea> page = findTareaService.findAll(pageable);
        return PaginatedResponse.fromPage(page, tareaMapper.toResponse(page.getContent()));
    }

    @Operation(summary = "Obtener tarea por ID")
    @GetMapping("/{id}")
    public TareaResponse findById(@PathVariable Integer id) {
        return tareaMapper.toResponse(findTareaService.findById(new TareaId(id)));
    }

    @Operation(summary = "Eliminar tarea")
    @ApiResponse(responseCode = "204", description = "Tarea eliminada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteTareaService.delete(new TareaId(id));
        return ResponseEntity.noContent().build();
    }
}
