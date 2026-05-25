package es.refugio.refugio.infraestructure.web.rest; // Controlador de Clean Architecture

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import es.refugio.common.infraestructure.web.dto.common.PaginatedResponse;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.application.service.adoptante.FindAdoptanteService;
import es.refugio.refugio.application.service.adoptante.CreateAdoptanteService;
import es.refugio.refugio.application.service.solicitud_adopcion.CreateSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.DeleteSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.EditSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.FindSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.AprobarSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.RechazarSolicitudAdopcionService;
import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.infraestructure.mapper.AdopcionMapper;
import es.refugio.refugio.infraestructure.mapper.SolicitudAdopcionMapper;
import es.refugio.refugio.infraestructure.security.CustomUserDetails;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionResponse;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionResponse;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionUpdateRequest;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.PublicSolicitudAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.ConvertAdoptanteRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/solicitudes-adopcion")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Adopción", description = "Gestión de solicitudes de adopción")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class SolicitudAdopcionController {

    private final CreateAdoptanteService createAdoptanteService;
    private final CreateSolicitudAdopcionService createSolicitudAdopcionService;
    private final FindSolicitudAdopcionService findSolicitudAdopcionService;
    private final EditSolicitudAdopcionService editSolicitudAdopcionService;
    private final DeleteSolicitudAdopcionService deleteSolicitudAdopcionService;
    private final AprobarSolicitudAdopcionService aprobarSolicitudAdopcionService;
    private final RechazarSolicitudAdopcionService rechazarSolicitudAdopcionService;
    private final FindAdoptanteService findAdoptanteService;
    private final AdopcionMapper adopcionMapper;
    private final PerfilLegalRepository perfilLegalRepository;
    private final SolicitudAdopcionMapper solicitudAdopcionMapper;

    @Operation(summary = "Crear solicitud de adopción")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Solicitud creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    @Transactional
    public ResponseEntity<SolicitudAdopcionResponse> createSolicitudAdopcion(
            @Valid @RequestBody SolicitudAdopcionRequest request) {
        CreateSolicitudAdopcionCommand command = solicitudAdopcionMapper.toCommand(request);
        SolicitudAdopcion solicitud = createSolicitudAdopcionService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Registro público y solicitud de adopción", description = "Registra un nuevo usuario, crea su perfil de adoptante y envía la solicitud de adopción en un solo paso.")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Registro exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping("/publico/registro-y-adopcion")
    @Transactional
    public ResponseEntity<SolicitudAdopcionResponse> registerAndRequest(
            @Valid @RequestBody PublicSolicitudAdopcionRequest request) {
        Integer usuarioId = request.usuarioId();
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 1. Asegurar PerfilLegal (Necesario para que CreateAdoptante no falle)
        // Solo lo creamos si no existe ya
        if (perfilLegalRepository.findByUsuarioId(usuarioId).isEmpty()) {
            PerfilLegal perfil = PerfilLegal.builder()
                    .usuarioId(usuarioId)
                    .nombre(request.nombre())
                    .apellido(request.apellido())
                    .dni(request.dni())
                    .telefono(request.telefono())
                    .direccion(request.direccion())
                    .fechaNacimiento(request.fechaNacimiento())
                    .build();
            perfilLegalRepository.save(perfil);
        }

        // 2. Crear Adoptante
        var adoptanteCommand = new CreateAdoptanteCommand(usuarioId);
        var adoptante = createAdoptanteService.createAdoptante(adoptanteCommand, false);

        // 3. Crear Solicitud
        var solicitudCommand = new CreateSolicitudAdopcionCommand(
                request.animalId(),
                adoptante.getId().getValue(),
                LocalDateTime.now(),
                request.comentario(),
                null);
        var solicitud = createSolicitudAdopcionService.create(solicitudCommand);

        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Convertir usuario a adoptante y enviar solicitud", description = "Asocia un perfil de adoptante a un usuario existente.")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Conversión y solicitud exitosas"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya tiene perfil") })
    @PostMapping("/convertir-y-adopcion")
    @Transactional
    public ResponseEntity<SolicitudAdopcionResponse> convertAndRequest(
            @Valid @RequestBody ConvertAdoptanteRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Integer usuarioId = userDetails.getId();

        PerfilLegal perfil = PerfilLegal.builder()
                .usuarioId(usuarioId)
                .nombre(request.nombre())
                .apellido(request.apellido())
                .dni(request.dni())
                .telefono(request.telefono())
                .direccion(request.direccion())
                .fechaNacimiento(request.fechaNacimiento())
                .build();

        perfilLegalRepository.findByUsuarioId(usuarioId)
                .ifPresentOrElse(
                        existing -> {
                            perfil.setId(existing.getId());
                            perfilLegalRepository.save(perfil);
                        },
                        () -> perfilLegalRepository.save(perfil));

        // 2. Crear perfil de Adoptante
        var adoptanteCommand = new CreateAdoptanteCommand(usuarioId);
        var adoptante = createAdoptanteService.createAdoptante(adoptanteCommand, false);

        // Actualización de rol es manejada remotamente (por el frontend que orquesta)

        // 3. Crear Solicitud
        var solicitudCommand = new CreateSolicitudAdopcionCommand(
                request.animalId(),
                adoptante.getId().getValue(),
                LocalDateTime.now(),
                request.comentario(),
                null);
        var solicitud = createSolicitudAdopcionService.create(solicitudCommand);

        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Adopción directa para adoptantes registrados", description = "Crea una solicitud de adopción directamente para el perfil del usuario autenticado.")
    @PostMapping("/directa")
    @Transactional
    public ResponseEntity<SolicitudAdopcionResponse> directRequest(@RequestBody Map<String, Object> request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Integer usuarioId = userDetails.getId();

        Adoptante adoptante = findAdoptanteService.findAll().stream()
                .filter(a -> a.getUsuarioId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("El usuario no tiene un perfil de adoptante activo"));

        Integer animalId = (Integer) request.get("animalId");
        String comentario = (String) request.get("comentario");

        var solicitudCommand = new CreateSolicitudAdopcionCommand(
                animalId,
                adoptante.getId().getValue(),
                LocalDateTime.now(),
                comentario,
                null);
        var solicitud = createSolicitudAdopcionService.create(solicitudCommand);

        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Actualizar solicitud de adopción")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<SolicitudAdopcionResponse> updateSolicitudAdopcion(@PathVariable Integer id,
            @Valid @RequestBody SolicitudAdopcionUpdateRequest request) {

        boolean isVolunteer = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VOLUNTARIO"));

        if (isVolunteer) {
            SolicitudAdopcion existing = findSolicitudAdopcionService.findById(new SolicitudAdopcionId(id));
            if (!existing.getEstado().name().equalsIgnoreCase(request.estado())) {
                throw new AccessDeniedException("Los voluntarios no pueden cambiar el estado de la solicitud.");
            }
        }

        EditSolicitudAdopcionCommand command = solicitudAdopcionMapper.toCommand(id, request);
        SolicitudAdopcion solicitud = editSolicitudAdopcionService.update(command);
        return ResponseEntity.ok(solicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Aprobar solicitud de adopción", description = "Aprueba una solicitud pendiente, actualizando el animal a RESERVADO, el adoptante a APROBADO y creando la Adopción.")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Solicitud aprobada y Adopción creada"),
            @ApiResponse(responseCode = "400", description = "Estado inválido") })
    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdopcionResponse> aprobarSolicitud(@PathVariable Integer id) {
        var adopcion = aprobarSolicitudAdopcionService.aprobar(new SolicitudAdopcionId(id));
        return ResponseEntity.status(HttpStatus.CREATED).body(adopcionMapper.toResponse(adopcion));
    }

    @Operation(summary = "Rechazar solicitud de adopción", description = "Rechaza una solicitud pendiente, marcando a su vez al adoptante como RECHAZADO.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Solicitud rechazada"),
            @ApiResponse(responseCode = "400", description = "Estado inválido") })
    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolicitudAdopcionResponse> rechazarSolicitud(@PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {
        String comentario = (body != null && body.containsKey("comentario")) ? body.get("comentario") : null;
        var solicitud = rechazarSolicitudAdopcionService.rechazar(new SolicitudAdopcionId(id), comentario);
        return ResponseEntity.ok(solicitudAdopcionMapper.toResponse(solicitud));
    }

    @Operation(summary = "Listar solicitudes")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public PaginatedResponse<SolicitudAdopcionResponse> getAll(
            @PageableDefault(size = 10) Pageable pageable) {
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));

        if (isStaff) {
            Page<SolicitudAdopcion> page = findSolicitudAdopcionService.findAll(pageable);
            return PaginatedResponse.fromPage(page, page.map(solicitudAdopcionMapper::toResponse).getContent());
        }

        // Para adoptantes, seguimos filtrando por ahora
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Integer usuarioId = userDetails.getId();

        Adoptante adoptante = findAdoptanteService.findAll().stream()
                .filter(a -> a.getUsuarioId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Adoptante no vinculado"));

        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findByAdoptanteId(adoptante.getId());

        List<SolicitudAdopcionResponse> responses = solicitudes.stream()
                .map(solicitudAdopcionMapper::toResponse)
                .toList();

        return PaginatedResponse.<SolicitudAdopcionResponse>builder()
                .items(responses)
                .total(responses.size())
                .count(responses.size())
                .page(1)
                .pageSize(responses.size() > 0 ? responses.size() : 10)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }

    @Operation(summary = "Contar solicitudes pendientes")
    @GetMapping("/count/pendiente")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public ResponseEntity<Long> countPendientes() {
        long count = findSolicitudAdopcionService.findAll().stream()
                .filter(s -> "PENDIENTE".equalsIgnoreCase(s.getEstado().name())
                        || "EN_REVISION".equalsIgnoreCase(s.getEstado().name()))
                .count();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Listar mis solicitudes", description = "Devuelve solo las solicitudes del usuario autenticado actual.")
    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public List<SolicitudAdopcionResponse> getMisSolicitudes() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return List.of();
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer usuarioId = userDetails.getId();

        try {
            // Intentamos buscar el perfil de adoptante del usuario
            var adoptante = findAdoptanteService.findByUsuarioId(usuarioId);
            if (adoptante == null)
                return List.of();

            return findSolicitudAdopcionService.findByAdoptanteId(adoptante.getId()).stream()
                    .map(solicitudAdopcionMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            // Si no tiene perfil de adoptante, es normal que no tenga solicitudes
            return List.of();
        }
    }

    @Operation(summary = "Obtener solicitud por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public SolicitudAdopcionResponse getSolicitudAdopcionById(@PathVariable Integer id) {
        SolicitudAdopcion solicitud = findSolicitudAdopcionService.findById(new SolicitudAdopcionId(id));
        checkOwnership(solicitud.getAdoptanteId());
        return solicitudAdopcionMapper.toResponse(solicitud);
    }

    @Operation(summary = "Solicitudes por animal")
    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    public List<SolicitudAdopcionResponse> getSolicitudAdopcionByAnimalId(@PathVariable Integer animalId) {
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findByAnimalId(new AnimalId(animalId));
        return solicitudAdopcionMapper.toResponse(solicitudes);
    }

    @Operation(summary = "Solicitudes por adoptante")
    @GetMapping("/adoptante/{adoptanteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO', 'ADOPTANTE')")
    public List<SolicitudAdopcionResponse> getSolicitudAdopcionByAdoptanteId(@PathVariable Integer adoptanteId) {
        checkOwnership(new AdoptanteId(adoptanteId));
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService
                .findByAdoptanteId(new AdoptanteId(adoptanteId));
        return solicitudAdopcionMapper.toResponse(solicitudes);
    }

    private void checkOwnership(AdoptanteId adoptanteId) {
        boolean isStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));

        if (!isStaff) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            Integer usuarioId = userDetails.getId();

            Adoptante adoptante = findAdoptanteService.findById(adoptanteId);
            if (!adoptante.getUsuarioId().equals(usuarioId)) {
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
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handleDataIntegrity(DataIntegrityViolationException ex) {
        String msg = ex.getMessage();
        if (msg != null && msg.contains("perfiles_legales.dni")) {
            return Map.of("message", "El DNI ya está registrado en el sistema. Por favor, use uno distinto.");
        }
        return Map.of("message", "Error de integridad de datos: Posible registro duplicado.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public Map<String, String> handleIllegalState(IllegalStateException ex) {
        return Map.of("message", ex.getMessage());
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
