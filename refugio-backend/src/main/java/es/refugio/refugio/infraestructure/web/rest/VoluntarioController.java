package es.refugio.refugio.infraestructure.web.rest;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.refugio.common.infraestructure.web.dto.common.PaginatedResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.application.service.voluntario.CreateVoluntarioService;
import es.refugio.refugio.application.service.voluntario.DeleteVoluntarioService;
import es.refugio.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.refugio.application.service.voluntario.ApproveVoluntarioService;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoVoluntario;
import es.refugio.refugio.infraestructure.mapper.VoluntarioMapper;
import es.refugio.refugio.infraestructure.security.CustomUserDetails;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioResponse;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioUpdateRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.DisponibilidadRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.DisponibilidadResponse;
import es.refugio.refugio.application.command.voluntario.SetDisponibilidadCommand;
import es.refugio.refugio.application.service.voluntario.SetDisponibilidadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/voluntarios")
@RequiredArgsConstructor
@Tag(name = "Voluntarios", description = "Gestión de voluntarios del refugio")
public class VoluntarioController {

    private final CreateVoluntarioService createVoluntarioService;
    private final FindVoluntarioService findVoluntarioService;
    private final EditVoluntarioService editVoluntarioService;
    private final DeleteVoluntarioService deleteVoluntarioService;
    private final ApproveVoluntarioService approveVoluntarioService;
    private final SetDisponibilidadService setDisponibilidadService;

    @Operation(summary = "Crear voluntario", description = "Registra un nuevo voluntario vinculado a un usuario")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Voluntario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    @PostMapping
    public ResponseEntity<VoluntarioResponse> create(@Valid @RequestBody VoluntarioRequest request) {
        CreateVoluntarioCommand command = VoluntarioMapper.toCommand(request);
        Voluntario voluntario = createVoluntarioService.createVoluntario(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(VoluntarioMapper.toResponse(voluntario));
    }

    @Operation(summary = "Actualizar voluntario")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Voluntario actualizado"),
            @ApiResponse(responseCode = "404", description = "No encontrado") })
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<VoluntarioResponse> update(@PathVariable Integer id,
            @Valid @RequestBody VoluntarioUpdateRequest request,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            Voluntario existing = findVoluntarioService.findById(new VoluntarioId(id));
            if (!existing.getUsuarioId().getValue().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        EditVoluntarioCommand command = VoluntarioMapper.toCommand(id, request);
        Voluntario voluntario = editVoluntarioService.update(command);
        return ResponseEntity.ok(VoluntarioMapper.toResponse(voluntario));
    }

    @Operation(summary = "Listar voluntarios")
    @ApiResponse(responseCode = "200", description = "Listado retornado")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PaginatedResponse<VoluntarioResponse> findAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer excludeTareaId,
            @RequestParam(required = false) String excludeDate,
            Pageable pageable) {
        Page<Voluntario> page = findVoluntarioService.findFiltered(q, excludeTareaId, excludeDate, pageable);
        return PaginatedResponse.fromPage(page, VoluntarioMapper.toResponse(page.getContent()));
    }

    @Operation(summary = "Contar voluntarios pendientes")
    @GetMapping("/count/pendiente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countPendientes() {
        long count = findVoluntarioService.findAll().stream()
                .filter(v -> EstadoVoluntario.PENDIENTE.equals(v.getEstado()))
                .count();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Listar voluntarios pendientes de aprobación")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pendientes")
    public List<VoluntarioResponse> findPending() {
        return findVoluntarioService.findAll().stream()
                .filter(v -> EstadoVoluntario.PENDIENTE.equals(v.getEstado()))
                .map(VoluntarioMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Aprobar solicitud de voluntariado")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<Void> approve(@PathVariable Integer id, HttpServletRequest request) {
        approveVoluntarioService.approve(id, getJwtToken(request));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Rechazar solicitud de voluntariado")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<Void> reject(@PathVariable Integer id, HttpServletRequest request) {
        approveVoluntarioService.reject(id, getJwtToken(request));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marcar disponibilidad de un voluntario")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @PostMapping("/{id}/disponibilidad")
    public ResponseEntity<List<DisponibilidadResponse>> setDisponibilidad(
            @PathVariable Integer id,
            @Valid @RequestBody DisponibilidadRequest request,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        Voluntario voluntario = findVoluntarioService.findById(new VoluntarioId(id));
        if (!isAdmin && !voluntario.getUsuarioId().getValue().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SetDisponibilidadCommand command = new SetDisponibilidadCommand(
            new VoluntarioId(id), request.fecha(), request.turno(), request.estado());
            
        Voluntario updated = setDisponibilidadService.setDisponibilidad(command);
        
        List<DisponibilidadResponse> responses = updated.getDisponibilidades().stream()
                .map(VoluntarioMapper::toResponse)
                .toList();
                
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtener calendario de disponibilidad de un voluntario")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<List<DisponibilidadResponse>> getDisponibilidades(
            @PathVariable Integer id,
            Authentication authentication) {
            
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        Voluntario voluntario = findVoluntarioService.findById(new VoluntarioId(id));
        if (!isAdmin && !voluntario.getUsuarioId().getValue().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (voluntario.getDisponibilidades() == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<DisponibilidadResponse> responses = voluntario.getDisponibilidades().stream()
                .map(VoluntarioMapper::toResponse)
                .toList();
                
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Eliminar disponibilidad de un voluntario en una fecha")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @DeleteMapping("/{id}/disponibilidad/{fecha}")
    public ResponseEntity<Void> deleteDisponibilidad(
            @PathVariable Integer id,
            @PathVariable String fecha,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        Voluntario voluntario = findVoluntarioService.findById(new VoluntarioId(id));
        if (!isAdmin && !voluntario.getUsuarioId().getValue().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        setDisponibilidadService.deleteDisponibilidad(new VoluntarioId(id), java.time.LocalDate.parse(fecha));
        return ResponseEntity.noContent().build();
    }

    private String getJwtToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    @Operation(summary = "Obtener voluntario por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOLUNTARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<VoluntarioResponse> findById(@PathVariable Integer id, Authentication authentication) {
        Voluntario voluntario = findVoluntarioService.findById(new VoluntarioId(id));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !voluntario.getUsuarioId().getValue().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(VoluntarioMapper.toResponse(voluntario));
    }

    @Operation(summary = "Voluntario por usuario", description = "Retorna el voluntario asociado a un usuario")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<VoluntarioResponse> findByUsuarioId(@PathVariable Integer usuarioId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !usuarioId.equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(VoluntarioMapper.toResponse(findVoluntarioService.findByUsuarioId(new UsuarioId(usuarioId))));
    }

    @Operation(summary = "Eliminar voluntario")
    @ApiResponse(responseCode = "204", description = "Voluntario eliminado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteVoluntarioService.delete(new VoluntarioId(id));
        return ResponseEntity.noContent().build();
    }
}
