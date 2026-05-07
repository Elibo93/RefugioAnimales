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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/animales")
@RequiredArgsConstructor
@Tag(name = "Animales", description = "Gestión de animales del refugio")
public class AnimalController {

    private final CreateAnimalService createAnimalService;
    private final FindAnimalService findAnimalService;
    private final EditAnimalService editAnimalService;
    private final DeleteAnimalService deleteAnimalService;

    @Operation(summary = "Crear animal", description = "Registra un nuevo animal en el refugio")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Animal creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnimalResponse> createAnimal(@Valid @RequestBody AnimalRequest request) {
        CreateAnimalCommand comando = AnimalMapper.toCommand(request);
        Animal animal = createAnimalService.createAnimal(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(AnimalMapper.toResponse(animal));
    }

    @Operation(summary = "Actualizar animal", description = "Modifica los datos de un animal existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Animal actualizado"),
            @ApiResponse(responseCode = "404", description = "Animal no encontrado") })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<AnimalResponse> updateAnimal(@PathVariable Integer id,
            @Valid @RequestBody AnimalRequest request) {
        EditAnimalCommand comando = AnimalMapper.toCommand(id, request);
        Animal animal = editAnimalService.update(comando);
        return ResponseEntity.ok(AnimalMapper.toResponse(animal));
    }

    @Operation(summary = "Listar animales", description = "Retorna todos los animales registrados")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    @GetMapping
    public List<AnimalResponse> getAll(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String estado,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String especie,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String tamano,
            @org.springframework.web.bind.annotation.RequestParam(required = false) List<String> edad,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String sexo,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Boolean urgencia) {
        return findAnimalService.findAll()
                .stream()
                .filter(a -> estado == null || estado.isEmpty()
                        || (a.getEstado() != null && a.getEstado().name().equalsIgnoreCase(estado)))
                .filter(a -> especie == null || especie.isEmpty()
                        || (a.getEspecie() != null && a.getEspecie().name().equalsIgnoreCase(especie)))
                .filter(a -> tamano == null || tamano.isEmpty()
                        || (a.getTamano() != null && a.getTamano().name().equalsIgnoreCase(tamano)))
                .filter(a -> edad == null || edad.isEmpty() || edad.stream().anyMatch(e -> matchEdad(a.getEdad(), e)))
                .filter(a -> sexo == null || sexo.isEmpty()
                        || (a.getSexo() != null && a.getSexo().name().equalsIgnoreCase(sexo)))
                .filter(a -> urgencia == null || (a.getUrgencia() != null && a.getUrgencia().equals(urgencia)))
                .map(AnimalMapper::toResponse)
                .toList();
    }

    private boolean matchEdad(Integer animalEdad, String filtroEdad) {
        if (animalEdad == null)
            return false;
        return switch (filtroEdad.toUpperCase()) {
            case "CACHORRO" -> animalEdad <= 1;
            case "JOVEN" -> animalEdad == 2 || animalEdad == 3;
            case "ADULTO" -> animalEdad >= 2 && animalEdad < 7;
            case "SENIOR" -> animalEdad >= 7;
            default -> true;
        };
    }

    @Operation(summary = "Obtener lista única de especies activas")
    @GetMapping("/especies")
    public List<String> getEspeciesActivas() {
        return findAnimalService.findAll().stream()
                .map(Animal::getEspecie)
                .filter(e -> e != null)
                .map(Enum::name)
                .distinct()
                .toList();
    }

    @Operation(summary = "Obtener animal por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Animal encontrado"),
            @ApiResponse(responseCode = "404", description = "Animal no encontrado") })
    @GetMapping("/{id}")
    public AnimalResponse getAnimalById(@PathVariable Integer id) {
        return AnimalMapper.toResponse(findAnimalService.findById(new AnimalId(id)));
    }

    @Operation(summary = "Eliminar animal")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Animal eliminado"),
            @ApiResponse(responseCode = "404", description = "Animal no encontrado") })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Integer id) {
        deleteAnimalService.delete(new AnimalId(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Incrementar visitas")
    @PostMapping("/{id}/visitas")
    public ResponseEntity<Void> incrementVisitas(@PathVariable Integer id) {
        editAnimalService.incrementVisitas(new AnimalId(id));
        return ResponseEntity.ok().build();
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