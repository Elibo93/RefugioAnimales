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

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.application.service.adopcion.CreateAdopcionService;
import es.refugio.refugio.application.service.adopcion.DeleteAdopcionService;
import es.refugio.refugio.application.service.adopcion.EditAdopcionService;
import es.refugio.refugio.application.service.adopcion.FindAdopcionService;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.infraestructure.mapper.AdopcionMapper;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/adopciones")
@RequiredArgsConstructor
public class AdopcionController {

    private final CreateAdopcionService createService;
    private final FindAdopcionService findService;
    private final DeleteAdopcionService deleteService;
    private final EditAdopcionService editService;

    @PostMapping
    public ResponseEntity<AdopcionResponse> createAdopcion(@RequestBody AdopcionRequest request) {
        CreateAdopcionCommand command = AdopcionMapper.toCommand(request);
        Adopcion adopcion = createService.createAdopcion(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AdopcionMapper.toResponse(adopcion));
    }

    @GetMapping
    public List<AdopcionResponse> getAll() {
        return findService.findAll()
                .stream()
                .map(AdopcionMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AdopcionResponse getById(@PathVariable int id) {
        Adopcion adopcion = findService.findById(new AdopcionId(id));
        return AdopcionMapper.toResponse(adopcion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        deleteService.delete(new AdopcionId(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public AdopcionResponse editAdopcion(
            @PathVariable int id,
            @Valid @RequestBody AdopcionRequest request) {
        EditAdopcionCommand command = AdopcionMapper.toCommand(id, request);
        Adopcion adopcion = editService.update(command);
        return AdopcionMapper.toResponse(adopcion);
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
