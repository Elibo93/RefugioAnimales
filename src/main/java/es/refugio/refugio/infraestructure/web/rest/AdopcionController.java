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
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
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

    private final CreateAdopcionService createAdopcionService;
    private final FindAdopcionService findAdopcionService;
    private final EditAdopcionService editAdopcionService;
    private final DeleteAdopcionService deleteAdopcionService;

    @PostMapping
    public ResponseEntity<AdopcionResponse> createAdopcion(@Valid @RequestBody AdopcionRequest request) {
        CreateAdopcionCommand command = AdopcionMapper.toCommand(request);
        Adopcion adopcion = createAdopcionService.createAdopcion(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdopcionMapper.toResponse(adopcion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdopcionResponse> updateAdopcion(@PathVariable Integer id,
            @Valid @RequestBody AdopcionRequest request) {
        EditAdopcionCommand command = AdopcionMapper.toCommand(id, request);
        Adopcion adopcion = editAdopcionService.update(command);
        return ResponseEntity.ok(AdopcionMapper.toResponse(adopcion));
    }

    @GetMapping
    public List<AdopcionResponse> getAll() {
        return findAdopcionService.findAll()
                .stream()
                .map(AdopcionMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AdopcionResponse getAdopcionById(@PathVariable Integer id) {
        return AdopcionMapper.toResponse(findAdopcionService.findById(new AdopcionId(id)));
    }

    @GetMapping("/animal/{animalId}")
    public List<AdopcionResponse> getAdopcionByAnimalId(@PathVariable Integer animalId) {
        List<Adopcion> adopciones = findAdopcionService.findByAnimalId(new AnimalId(animalId));
        return AdopcionMapper.toResponse(adopciones);
    }

    @GetMapping("/adoptante/{adoptanteId}")
    public List<AdopcionResponse> getAdopcionByAdoptanteId(@PathVariable Integer adoptanteId) {
        List<Adopcion> adopciones = findAdopcionService.findByAdoptanteId(new AdoptanteId(adoptanteId));
        return AdopcionMapper.toResponse(adopciones);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdopcion(@PathVariable Integer id) {
        deleteAdopcionService.delete(new AdopcionId(id));
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
