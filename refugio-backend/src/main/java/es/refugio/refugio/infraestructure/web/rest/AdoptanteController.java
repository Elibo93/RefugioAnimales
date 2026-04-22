package es.refugio.refugio.infraestructure.web.rest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

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
import es.refugio.refugio.application.service.solicitud_adopcion.CreateSolicitudAdopcionService;
import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.infraestructure.mapper.AdoptanteMapper;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteRequest;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteResponse;
import es.refugio.refugio.infraestructure.web.dto.adoptante.ConvertirAdoptanteRequest;
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
    private final CreateSolicitudAdopcionService solicitudService;

    @Operation(summary = "Crear adoptante", description = "Registra un nuevo adoptante vinculado a un usuario")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Adoptante creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
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
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public List<AdoptanteResponse> getAll() {
        return findService.findAll()
                .stream()
                .map(AdoptanteMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Obtener adoptante por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Adoptante encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado") })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public AdoptanteResponse getById(@PathVariable int id) {
        Adoptante adoptante = findService.findById(new AdoptanteId(id));
        checkOwnership(adoptante);
        return AdoptanteMapper.toResponse(adoptante);
    }

    @Operation(summary = "Obtener adoptante por ID de usuario")
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public AdoptanteResponse getByUsuarioId(@PathVariable int usuarioId) {
        Adoptante adoptante = findService.findByUsuarioId(usuarioId);
        checkOwnership(adoptante);
        return AdoptanteMapper.toResponse(adoptante);
    }

    @Operation(summary = "Eliminar adoptante")
    @ApiResponse(responseCode = "204", description = "Adoptante eliminado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable int id) {
        deleteService.delete(new AdoptanteId(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Editar adoptante")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Adoptante actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public AdoptanteResponse editAdoptante(
            @PathVariable int id,
            @Valid @RequestBody AdoptanteRequest request) {
        Adoptante adoptanteExistente = findService.findById(new AdoptanteId(id));
        checkOwnership(adoptanteExistente);

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

    @Operation(summary = "Convertir usuario público a adoptante y crear solicitud", description = "Actualiza el rol del usuario, crea su perfil de adoptante y registra la solicitud de adopción automáticamente.")
    @PostMapping("/convertir-y-solicitar")
    @PreAuthorize("hasRole('PUBLICO')")
    public ResponseEntity<String> convertirYSolicitar(@Valid @RequestBody ConvertirAdoptanteRequest request) {
        // TODO: Mover lógica a refugio-auth o usar FeignClient
        Integer usuarioId = 1;

        // 2. Crear Perfil de Adoptante
        Adoptante adoptante = createService.createAdoptante(new CreateAdoptanteCommand(
                usuarioId,
                request.getDni(),
                request.getDireccion(),
                request.getFechaNacimiento()));

        // 3. Crear Solicitud de Adopción
        solicitudService.create(new CreateSolicitudAdopcionCommand(
                request.getAnimalId(),
                adoptante.getId().getValue(),
                LocalDateTime.now(),
                "Solicitud automática tras conversión de perfil."));

        return ResponseEntity.ok("Perfil actualizado y solicitud enviada correctamente.");
    }

    private void checkOwnership(Adoptante adoptante) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));

        if (!isStaff) {
            // TODO: Extraer desde JWT
            Integer usuarioId = 1;

            if (!adoptante.getUsuarioId().equals(usuarioId)) {
                throw new AccessDeniedException("No tienes permiso para acceder a este perfil.");
            }
        }
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