package es.refugio.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.refugio.common.infraestructure.web.dto.common.PaginatedResponse;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;

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
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import java.util.stream.Collectors;
import java.util.Arrays;

import es.refugio.refugio.application.service.storage.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import es.refugio.refugio.infraestructure.db.jpa.entity.FavoritoAnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.FavoritoAnimalJpaRepository;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/animales")
@RequiredArgsConstructor
@Tag(name = "Animales", description = "Gestión de animales del refugio")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Animal.
 *
 * @author Elisabeth
 * @author Diego
 */
public class AnimalController {

    private final CreateAnimalService createAnimalService;
    private final FindAnimalService findAnimalService;
    private final EditAnimalService editAnimalService;
    private final DeleteAnimalService deleteAnimalService;
    private final FileStorageService fileStorageService;
    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final FavoritoAnimalJpaRepository favoritoAnimalRepository;
    private final AnimalMapper animalMapper;

    @Operation(summary = "Crear animal", description = "Registra un nuevo animal en el refugio")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Animal creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnimalResponse> createAnimal(
            @RequestPart("animal") @Valid AnimalRequest request,
            @RequestPart(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {

        String fotoUrl = request.foto();
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            fotoUrl = fileStorageService.storeFile(fotoArchivo, request.nombre());
        }

        CreateAnimalCommand comando = animalMapper.toCommand(request.withFoto(fotoUrl));
        Animal animal = createAnimalService.createAnimal(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(animalMapper.toResponse(animal));
    }

    @Operation(summary = "Actualizar animal", description = "Modifica los datos de un animal existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Animal actualizado"),
            @ApiResponse(responseCode = "404", description = "Animal no encontrado") })
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<AnimalResponse> updateAnimal(@PathVariable Integer id,
            @RequestPart("animal") @Valid AnimalRequest request,
            @RequestPart(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {

        String fotoUrl = request.foto();
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            fotoUrl = fileStorageService.storeFile(fotoArchivo, request.nombre());
        }

        EditAnimalCommand comando = animalMapper.toCommand(id, request.withFoto(fotoUrl));
        Animal animal = editAnimalService.update(comando);
        return ResponseEntity.ok(animalMapper.toResponse(animal));
    }

    @Operation(summary = "Listar animales", description = "Retorna todos los animales registrados")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    @GetMapping
    public PaginatedResponse<AnimalResponse> getAll(
            Pageable pageable,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) List<String> edad,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) Boolean urgencia,
            @RequestParam(required = false) String q) {

        log.debug("Backend - findFiltered con q={}, estado={}, página={}", q, estado, pageable.getPageNumber());
        Page<Animal> page = findAnimalService.findFiltered(q, estado, especie, tamano, edad, sexo, urgencia, pageable);
        log.debug("Backend - Resultados encontrados: {}, Elementos totales: {}", page.getContent().size(), page.getTotalElements());

        Map<Integer, Long> conteos = solicitudAdopcionRepository.getAll().stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE || s.getEstado() == EstadoSolicitud.EN_REVISION)
                .collect(Collectors.groupingBy(s -> s.getAnimalId().getValue(), Collectors.counting()));

        return PaginatedResponse.fromPage(page, page.getContent().stream()
                .map(a -> animalMapper.toResponse(a, conteos.getOrDefault(a.getId().getValue(), 0L).intValue()))
                .toList());
    }

    @Operation(summary = "Obtener lista única de especies activas")
    @GetMapping("/especies")
    public List<String> getEspeciesActivas() {
        return Arrays.stream(Especie.values())
                .map(Enum::name)
                .toList();
    }

    @Operation(summary = "Obtener animal por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Animal encontrado"),
            @ApiResponse(responseCode = "404", description = "Animal no encontrado") })
    @GetMapping("/{id}")
    public AnimalResponse getAnimalById(@PathVariable Integer id) {
        Animal a = findAnimalService.findById(new AnimalId(id));
        long count = solicitudAdopcionRepository.getByAnimalId(a.getId()).stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE || s.getEstado() == EstadoSolicitud.EN_REVISION)
                .count();
        return animalMapper.toResponse(a, (int) count);
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

    @Operation(summary = "Alternar estado de favorito (Toggle)")
    @PostMapping("/{id}/favorito")
    public ResponseEntity<Boolean> toggleFavorito(@PathVariable Integer id, @RequestParam Integer usuarioId) {
        Optional<FavoritoAnimalEntity> existente = favoritoAnimalRepository.findByUsuarioIdAndAnimalId(usuarioId, id);
        if (existente.isPresent()) {
            favoritoAnimalRepository.delete(existente.get());
            return ResponseEntity.ok(false); // Retorna false si lo ha quitado
        } else {
            FavoritoAnimalEntity nuevo = FavoritoAnimalEntity.builder()
                    .usuarioId(usuarioId)
                    .animalId(id)
                    .build();
            favoritoAnimalRepository.save(nuevo);
            return ResponseEntity.ok(true); // Retorna true si lo ha añadido
        }
    }

    @Operation(summary = "Obtener IDs de animales favoritos de un usuario")
    @GetMapping("/favoritos")
    public List<Integer> getFavoritosByUsuario(@RequestParam Integer usuarioId) {
        return favoritoAnimalRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(FavoritoAnimalEntity::getAnimalId)
                .toList();
    }

    @Operation(summary = "Obtener el contador de favoritos de un animal")
    @GetMapping("/{id}/favoritos/count")
    public Integer countFavoritosByAnimal(@PathVariable Integer id) {
        return favoritoAnimalRepository.countByAnimalId(id);
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