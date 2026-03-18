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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/historial-medico")
@RequiredArgsConstructor
public class HistorialMedicoController {

    private final CreateHistorialMedicoService createHistorialMedicoService;
    private final FindHistorialMedicoService findHistorialMedicoService;
    private final EditHistorialMedicoService editHistorialMedicoService;
    private final DeleteHistorialMedicoService deleteHistorialMedicoService;

    @PostMapping
    public ResponseEntity<HistorialMedicoResponse> createHistorialMedico(@Valid @RequestBody HistorialMedicoRequest request) {
        CreateHistorialMedicoCommand command = HistorialMedicoMapper.toCommand(request);
        HistorialMedico historialMedico = createHistorialMedicoService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(HistorialMedicoMapper.toResponse(historialMedico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialMedicoResponse> updateHistorialMedico(@PathVariable Integer id,
                                                                         @Valid @RequestBody HistorialMedicoRequest request) {
        EditHistorialMedicoCommand command = HistorialMedicoMapper.toCommand(id, request);
        HistorialMedico historialMedico = editHistorialMedicoService.update(command);
        return ResponseEntity.ok(HistorialMedicoMapper.toResponse(historialMedico));
    }

    @GetMapping
    public List<HistorialMedicoResponse> getAll() {
        return findHistorialMedicoService.findAll()
                .stream()
                .map(HistorialMedicoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public HistorialMedicoResponse getHistorialMedicoById(@PathVariable Integer id) {
        return HistorialMedicoMapper.toResponse(findHistorialMedicoService.findById(new HistorialMedicoId(id)));
    }

    @GetMapping("/animal/{animalId}")
    public List<HistorialMedicoResponse> getHistorialMedicoByAnimalId(@PathVariable Integer animalId) {
        List<HistorialMedico> historiales = findHistorialMedicoService.findByAnimalId(new AnimalId(animalId));
        return HistorialMedicoMapper.toResponse(historiales);
    }

    @DeleteMapping("/{id}")
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
