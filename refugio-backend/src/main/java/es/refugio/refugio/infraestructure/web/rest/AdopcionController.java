package es.refugio.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.application.service.adoptante.FindAdoptanteService;
import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.application.service.adopcion.CreateAdopcionService;
import es.refugio.refugio.application.service.adopcion.DeleteAdopcionService;
import es.refugio.refugio.application.service.adopcion.EditAdopcionService;
import es.refugio.refugio.application.service.adopcion.FindAdopcionService;
import es.refugio.refugio.infraestructure.mapper.AdopcionMapper;
import es.refugio.refugio.infraestructure.security.CustomUserDetails;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionResponse;
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
@RequestMapping("/api/v1/adopciones")
@RequiredArgsConstructor
@Tag(name = "Adopciones", description = "Gestión de adopciones de animales")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class AdopcionController {

    private final CreateAdopcionService createAdopcionService;
    private final FindAdopcionService findAdopcionService;
    private final EditAdopcionService editAdopcionService;
    private final DeleteAdopcionService deleteAdopcionService;
    private final FindAdoptanteService findAdoptanteService;
    private final AdopcionMapper adopcionMapper;

    @Operation(summary = "Registrar adopción", description = "Crea una nueva adopción vinculando adoptante y animal")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Adopción registrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    public ResponseEntity<AdopcionResponse> createAdopcion(@Valid @RequestBody AdopcionRequest request) {
        CreateAdopcionCommand command = adopcionMapper.toCommand(request);
        Adopcion adopcion = createAdopcionService.createAdopcion(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(adopcionMapper.toResponse(adopcion));
    }

    @Operation(summary = "Actualizar adopción")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Adopción actualizada"),
            @ApiResponse(responseCode = "404", description = "No encontrada") })
    @PutMapping("/{id}")
    public ResponseEntity<AdopcionResponse> updateAdopcion(@PathVariable Integer id,
            @Valid @RequestBody AdopcionRequest request) {
        EditAdopcionCommand command = adopcionMapper.toCommand(id, request);
        Adopcion adopcion = editAdopcionService.update(command);
        return ResponseEntity.ok(adopcionMapper.toResponse(adopcion));
    }

    @Operation(summary = "Listar adopciones", description = "Retorna todas las adopciones registradas")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public PaginatedResponse<AdopcionResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String estado) {
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));

        if (!isStaff) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Integer usuarioId = userDetails.getId();
            Adoptante adoptante = findAdoptanteService.findAll().stream()
                    .filter(a -> a.getUsuarioId().equals(usuarioId))
                    .findFirst()
                    .orElseThrow(() -> new AccessDeniedException("Adoptante no vinculado"));

            List<Adopcion> adopciones = findAdopcionService.findByAdoptanteId(adoptante.getId());
            
            if (q != null && !q.trim().isEmpty()) {
                String pattern = q.toLowerCase().trim();
                adopciones = adopciones.stream()
                        .filter(a -> String.valueOf(a.getId().getValue()).contains(pattern) || 
                                     a.getEstado().name().toLowerCase().contains(pattern))
                        .toList();
            }
            if (estado != null && !estado.trim().isEmpty()) {
                String est = estado.toUpperCase().trim();
                adopciones = adopciones.stream()
                        .filter(a -> a.getEstado().name().equals(est))
                        .toList();
            }

            // Paginación manual para listas filtradas en memoria
            int start = Math.min(Math.max(0, page) * size, adopciones.size());
            int end = Math.min(start + size, adopciones.size());
            List<AdopcionResponse> pagedList = adopciones.subList(start, end).stream()
                    .map(adopcionMapper::toResponse)
                    .toList();
            int totalPages = (int) Math.ceil((double) adopciones.size() / size);
            return PaginatedResponse.<AdopcionResponse>builder()
                    .items(pagedList)
                    .total(adopciones.size())
                    .count(adopciones.size())
                    .page(page + 1)
                    .pageSize(size)
                    .totalPages(totalPages)
                    .hasNext(page + 1 < totalPages)
                    .hasPrevious(page > 0)
                    .build();
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), size);
        Page<Adopcion> adopcionesPage = findAdopcionService.findFiltered(q, estado, pageable);
        return PaginatedResponse.fromPage(adopcionesPage, adopcionesPage.getContent().stream()
                .map(adopcionMapper::toResponse)
                .toList());
    }

    @Operation(summary = "Obtener adopción por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public AdopcionResponse getAdopcionById(@PathVariable Integer id) {
        Adopcion adopcion = findAdopcionService.findById(new AdopcionId(id));
        checkOwnership(adopcion.getAdoptanteId());
        return adopcionMapper.toResponse(adopcion);
    }

    @Operation(summary = "Adopciones por animal", description = "Retorna adopciones asociadas a un animal concreto")
    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public List<AdopcionResponse> getAdopcionByAnimalId(@PathVariable Integer animalId) {
        List<Adopcion> adopciones = findAdopcionService.findByAnimalId(new AnimalId(animalId));
        return adopcionMapper.toResponse(adopciones);
    }

    @Operation(summary = "Adopciones por adoptante", description = "Retorna adopciones de un adoptante concreto")
    @GetMapping("/adoptante/{adoptanteId}")
    public List<AdopcionResponse> getAdopcionByAdoptanteId(@PathVariable Integer adoptanteId) {
        List<Adopcion> adopciones = findAdopcionService.findByAdoptanteId(new AdoptanteId(adoptanteId));
        return adopcionMapper.toResponse(adopciones);
    }

    @Operation(summary = "Eliminar adopción")
    @ApiResponse(responseCode = "204", description = "Adopción eliminada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdopcion(@PathVariable Integer id) {
        deleteAdopcionService.delete(new AdopcionId(id));
        return ResponseEntity.noContent().build();
    }

    private void checkOwnership(AdoptanteId adoptanteId) {
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));

        if (!isStaff) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Integer usuarioId = userDetails.getId();

            Adoptante adoptante = findAdoptanteService.findById(adoptanteId);
            if (!adoptante.getUsuarioId().equals(usuarioId)) {
                throw new AccessDeniedException("No tienes permiso para acceder a esta información.");
            }
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return errors;
    }
}
