package es.refugio.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import es.refugio.refugio.application.command.adoptante.ApproveAdoptanteCommand;
import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.command.adoptante.EditAdoptanteCommand;
import es.refugio.refugio.application.command.adoptante.RejectAdoptanteCommand;
import es.refugio.refugio.application.service.adoptante.ApproveAdoptanteService;
import es.refugio.refugio.application.service.adoptante.CreateAdoptanteService;
import es.refugio.refugio.application.service.adoptante.DeleteAdoptanteService;
import es.refugio.refugio.application.service.adoptante.EditAdoptanteService;
import es.refugio.refugio.application.service.adoptante.FindAdoptanteService;
import es.refugio.refugio.application.service.adoptante.RejectAdoptanteService;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.infraestructure.mapper.AdoptanteMapper;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteRequest;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/adoptantes")
@RequiredArgsConstructor
@Tag(name = "Adoptantes", description = "Gestión de adoptantes del refugio")
public class AdoptanteController {

    private final CreateAdoptanteService createService;
    private final FindAdoptanteService findService;
    private final DeleteAdoptanteService deleteService;
    private final EditAdoptanteService editService;
    private final ApproveAdoptanteService approveService;
    private final RejectAdoptanteService rejectService;

    @Operation(summary = "Crear adoptante", description = "Registra un nuevo adoptante vinculado a un usuario")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Adoptante creado"), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    public ResponseEntity<AdoptanteResponse> createAdoptante(@Valid @RequestBody AdoptanteRequest request) {
        CreateAdoptanteCommand command = AdoptanteMapper.toCommand(request);
        Adoptante adoptante = createService.createAdoptante(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AdoptanteMapper.toResponse(adoptante));
    }

    @Operation(summary = "Listar adoptantes")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    @GetMapping
    public List<AdoptanteResponse> getAll() {
        return findService.findAll()
                .stream()
                .map(AdoptanteMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Obtener adoptante por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Adoptante encontrado"), @ApiResponse(responseCode = "404", description = "No encontrado") })
    @GetMapping("/{id}")
    public AdoptanteResponse getById(@PathVariable int id) {
        Adoptante adoptante = findService.findById(new AdoptanteId(id));
        return AdoptanteMapper.toResponse(adoptante);
    }

    @Operation(summary = "Eliminar adoptante")
    @ApiResponse(responseCode = "204", description = "Adoptante eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        deleteService.delete(new AdoptanteId(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Editar adoptante")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Adoptante actualizado"), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PutMapping("/{id}")
    public AdoptanteResponse editAdoptante(
            @PathVariable int id,
            @Valid @RequestBody AdoptanteRequest request) {
        EditAdoptanteCommand command = AdoptanteMapper.toEditCommand(new AdoptanteId(id), request);
        Adoptante adoptante = editService.update(command);
        return AdoptanteMapper.toResponse(adoptante);
    }

    @Operation(summary = "Aprobar adoptante", description = "Cambia el estado de validación del adoptante a APROBADO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Adoptante aprobado correctamente"),
            @ApiResponse(responseCode = "404", description = "Adoptante no encontrado")
    })
    @PatchMapping("/{id}/approve")
    public AdoptanteResponse approveAdoptante(@PathVariable int id) {
        ApproveAdoptanteCommand command = new ApproveAdoptanteCommand(new AdoptanteId(id));
        Adoptante adoptante = approveService.approve(command);
        return AdoptanteMapper.toResponse(adoptante);
    }

    @Operation(summary = "Rechazar adoptante", description = "Cambia el estado de validación del adoptante a RECHAZADO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Adoptante rechazado correctamente"),
            @ApiResponse(responseCode = "404", description = "Adoptante no encontrado")
    })
    @PatchMapping("/{id}/reject")
    public AdoptanteResponse rejectAdoptante(@PathVariable int id) {
        RejectAdoptanteCommand command = new RejectAdoptanteCommand(new AdoptanteId(id));
        Adoptante adoptante = rejectService.reject(command);
        return AdoptanteMapper.toResponse(adoptante);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return errors;
    }
}