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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import es.refugio.auth.infrastructure.repository.UserRepository;
import es.refugio.auth.domain.AuthCredentialEntity;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.application.service.adoptante.FindAdoptanteService;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.application.service.solicitud_adopcion.CreateSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.DeleteSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.EditSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.FindSolicitudAdopcionService;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.infraestructure.mapper.SolicitudAdopcionMapper;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/solicitudes-adopcion")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Adopción", description = "Gestión de solicitudes de adopción")
public class SolicitudAdopcionController {

    private final CreateSolicitudAdopcionService createSolicitudAdopcionService;
    private final FindSolicitudAdopcionService findSolicitudAdopcionService;
    private final EditSolicitudAdopcionService editSolicitudAdopcionService;
    private final DeleteSolicitudAdopcionService deleteSolicitudAdopcionService;
    private final UserRepository userRepository;
    private final FindAdoptanteService findAdoptanteService;

    @Operation(summary = "Crear solicitud de adopción")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Solicitud creada"), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    public ResponseEntity<SolicitudAdopcionResponse> createSolicitudAdopcion(@Valid @RequestBody SolicitudAdopcionRequest request) {
        CreateSolicitudAdopcionCommand command = SolicitudAdopcionMapper.toCommand(request);
        SolicitudAdopcion solicitud = createSolicitudAdopcionService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Actualizar solicitud de adopción")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<SolicitudAdopcionResponse> updateSolicitudAdopcion(@PathVariable Integer id,
                                                                             @Valid @RequestBody SolicitudAdopcionRequest request) {
        
        boolean isVolunteer = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VOLUNTARIO"));
        
        if (isVolunteer) {
            SolicitudAdopcion existing = findSolicitudAdopcionService.findById(new SolicitudAdopcionId(id));
            if (!existing.getEstado().name().equalsIgnoreCase(request.estado())) {
                throw new AccessDeniedException("Los voluntarios no pueden cambiar el estado de la solicitud.");
            }
        }

        EditSolicitudAdopcionCommand command = SolicitudAdopcionMapper.toCommand(id, request);
        SolicitudAdopcion solicitud = editSolicitudAdopcionService.update(command);
        return ResponseEntity.ok(SolicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Listar solicitudes")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public List<SolicitudAdopcionResponse> getAll() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));
        
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findAll();
        
        if (!isStaff) {
            AuthCredentialEntity user = userRepository.findByEmail(currentEmail)
                    .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
            Adoptante adoptante = findAdoptanteService.findAll().stream()
                    .filter(a -> a.getUsuarioId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AccessDeniedException("Adoptante no vinculado"));
                    
            solicitudes = solicitudes.stream()
                    .filter(s -> s.getAdoptanteId().equals(adoptante.getId()))
                    .toList();
        }

        return solicitudes.stream()
                .map(SolicitudAdopcionMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Obtener solicitud por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public SolicitudAdopcionResponse getSolicitudAdopcionById(@PathVariable Integer id) {
        SolicitudAdopcion solicitud = findSolicitudAdopcionService.findById(new SolicitudAdopcionId(id));
        checkOwnership(solicitud.getAdoptanteId());
        return SolicitudAdopcionMapper.toResponse(solicitud);
    }

    @Operation(summary = "Solicitudes por animal")
    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public List<SolicitudAdopcionResponse> getSolicitudAdopcionByAnimalId(@PathVariable Integer animalId) {
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findByAnimalId(new AnimalId(animalId));
        return SolicitudAdopcionMapper.toResponse(solicitudes);
    }

    @Operation(summary = "Solicitudes por adoptante")
    @GetMapping("/adoptante/{adoptanteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public List<SolicitudAdopcionResponse> getSolicitudAdopcionByAdoptanteId(@PathVariable Integer adoptanteId) {
        checkOwnership(new AdoptanteId(adoptanteId));
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findByAdoptanteId(new AdoptanteId(adoptanteId));
        return SolicitudAdopcionMapper.toResponse(solicitudes);
    }

    private void checkOwnership(AdoptanteId adoptanteId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));
        
        if (!isStaff) {
            AuthCredentialEntity user = userRepository.findByEmail(currentEmail)
                    .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
            
            Adoptante adoptante = findAdoptanteService.findById(adoptanteId);
            if (!adoptante.getUsuarioId().equals(user.getId())) {
                throw new AccessDeniedException("No tienes permiso para acceder a estas solicitudes.");
            }
        }
    }

    @Operation(summary = "Eliminar solicitud")
    @ApiResponse(responseCode = "204", description = "Solicitud eliminada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSolicitudAdopcion(@PathVariable Integer id) {
        deleteSolicitudAdopcionService.delete(new SolicitudAdopcionId(id));
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
