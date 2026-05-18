package es.refugio.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import es.refugio.refugio.application.command.historial_medico.CreateHistorialMedicoCommand;
import es.refugio.refugio.application.command.historial_medico.EditHistorialMedicoCommand;
import es.refugio.refugio.application.service.historial_medico.CreateHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.DeleteHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.EditHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.FindHistorialMedicoService;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.infraestructure.mapper.HistorialMedicoMapper;
import es.refugio.refugio.infraestructure.web.dto.historial_medico.HistorialMedicoRequest;
import es.refugio.refugio.infraestructure.web.dto.historial_medico.HistorialMedicoResponse;
import es.refugio.common.infraestructure.web.dto.common.PaginatedResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/historial-medico")
@RequiredArgsConstructor
@Tag(name = "Historial Médico", description = "Registro del historial médico de los animales")
public class HistorialMedicoController {

    private final CreateHistorialMedicoService createHistorialMedicoService;
    private final FindHistorialMedicoService findHistorialMedicoService;
    private final EditHistorialMedicoService editHistorialMedicoService;
    private final DeleteHistorialMedicoService deleteHistorialMedicoService;

    @Operation(summary = "Crear registro médico")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Registro creado"), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<HistorialMedicoResponse> createHistorialMedico(@Valid @RequestBody HistorialMedicoRequest request) {
        CreateHistorialMedicoCommand command = HistorialMedicoMapper.toCommand(request);
        HistorialMedico historialMedico = createHistorialMedicoService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(HistorialMedicoMapper.toResponse(historialMedico));
    }

    @Operation(summary = "Actualizar registro médico")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<HistorialMedicoResponse> updateHistorialMedico(@PathVariable Integer id,
                                                                         @Valid @RequestBody HistorialMedicoRequest request) {
        EditHistorialMedicoCommand command = HistorialMedicoMapper.toCommand(id, request);
        HistorialMedico historialMedico = editHistorialMedicoService.update(command);
        return ResponseEntity.ok(HistorialMedicoMapper.toResponse(historialMedico));
    }

    @Operation(summary = "Listar historial médico")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public PaginatedResponse<HistorialMedicoResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size);
        Page<HistorialMedico> historialPage = findHistorialMedicoService.findAll(pageable);
        return PaginatedResponse.fromPage(historialPage, HistorialMedicoMapper.toResponse(historialPage.getContent()));
    }

    @Operation(summary = "Obtener registro médico por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public HistorialMedicoResponse getHistorialMedicoById(@PathVariable Integer id) {
        return HistorialMedicoMapper.toResponse(findHistorialMedicoService.findById(new HistorialMedicoId(id)));
    }

    @Operation(summary = "Historial médico por animal", description = "Retorna todos los registros médicos de un animal concreto")
    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public List<HistorialMedicoResponse> getHistorialMedicoByAnimalId(@PathVariable Integer animalId) {
        List<HistorialMedico> historiales = findHistorialMedicoService.findByAnimalId(new AnimalId(animalId));
        return HistorialMedicoMapper.toResponse(historiales);
    }

    @Operation(summary = "Eliminar registro médico")
    @ApiResponse(responseCode = "204", description = "Registro eliminado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<Void> deleteHistorialMedico(@PathVariable Integer id) {
        deleteHistorialMedicoService.delete(new HistorialMedicoId(id));
        return ResponseEntity.noContent().build();
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
