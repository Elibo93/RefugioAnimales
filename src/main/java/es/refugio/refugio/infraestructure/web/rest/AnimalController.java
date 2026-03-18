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

import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.refugio.application.service.animal.CreateAnimalService;
import es.refugio.refugio.application.service.animal.DeleteAnimalService;
import es.refugio.refugio.application.service.animal.EditAnimalService;
import es.refugio.refugio.application.service.animal.FindAnimalService;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.infraestructure.mapper.AnimalMapper;
import es.refugio.refugio.infraestructure.web.dto.animal.AnimalRequest;
import es.refugio.refugio.infraestructure.web.dto.animal.AnimalResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/animales")
@RequiredArgsConstructor
public class AnimalController {

    private final CreateAnimalService createAnimalService;
    private final FindAnimalService findAnimalService;
    private final EditAnimalService editAnimalService;
    private final DeleteAnimalService deleteAnimalService;

    @PostMapping
    public ResponseEntity<AnimalResponse> createAnimal(@Valid @RequestBody AnimalRequest request) {
        CreateAnimalCommand comando = AnimalMapper.toCommand(request);
        Animal animal = createAnimalService.createAnimal(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(AnimalMapper.toResponse(animal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponse> updateAnimal(@PathVariable Integer id,
                                                     @Valid @RequestBody AnimalRequest request) {
        EditAnimalCommand comando = AnimalMapper.toCommand(id, request);
        Animal animal = editAnimalService.update(comando);
        return ResponseEntity.ok(AnimalMapper.toResponse(animal));
    }

    @GetMapping
    public List<AnimalResponse> getAll() {
        return findAnimalService.findAll()
                .stream()
                .map(AnimalMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AnimalResponse getAnimalById(@PathVariable Integer id) {
        return AnimalMapper.toResponse(findAnimalService.findById(new AnimalId(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Integer id) {
        deleteAnimalService.delete(new AnimalId(id));
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