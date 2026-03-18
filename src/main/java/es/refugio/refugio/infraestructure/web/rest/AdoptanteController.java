package es.refugio.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.command.adoptante.EditAdoptanteCommand;
import es.refugio.refugio.application.service.adoptante.CreateAdoptanteService;
import es.refugio.refugio.application.service.adoptante.DeleteAdoptanteService;
import es.refugio.refugio.application.service.adoptante.EditAdoptanteService;
import es.refugio.refugio.application.service.adoptante.FindAdoptanteService;
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
public class AdoptanteController {

    private final CreateAdoptanteService createService;
    private final FindAdoptanteService findService;
    private final DeleteAdoptanteService deleteService;
    private final EditAdoptanteService editService;

    @PostMapping
    public ResponseEntity<AdoptanteResponse> createAdoptante(@Valid @RequestBody AdoptanteRequest request) {
        CreateAdoptanteCommand command = AdoptanteMapper.toCommand(request);
        Adoptante adoptante = createService.createAdoptante(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AdoptanteMapper.toResponse(adoptante));
    }

    @GetMapping
    public List<AdoptanteResponse> getAll() {
        return findService.findAll()
                .stream()
                .map(AdoptanteMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AdoptanteResponse getById(@PathVariable int id) {
        Adoptante adoptante = findService.findById(new AdoptanteId(id));
        return AdoptanteMapper.toResponse(adoptante);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        deleteService.delete(new AdoptanteId(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public AdoptanteResponse editAdoptante(
            @PathVariable int id,
            @Valid @RequestBody AdoptanteRequest request) {
        EditAdoptanteCommand command = AdoptanteMapper.toEditCommand(new AdoptanteId(id), request);
        Adoptante adoptante = editService.update(command);
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