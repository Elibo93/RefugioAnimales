package es.refugio.refugio.infraestructure.web.rest;

import es.refugio.refugio.infraestructure.db.jpa.entity.NotificacionEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.NotificacionRepository;
import es.refugio.refugio.infraestructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
/**
 * Controlador REST que expone los endpoints HTTP de la API para la gestión de Notificacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class NotificacionController {

    private final NotificacionRepository repository;

    @GetMapping("/me")
    public List<NotificacionEntity> listarMisNotificaciones() {
        Integer usuarioId = obtenerIdUsuarioActual();
        Collection<String> roles = obtenerRolesUsuarioActual();
        
        log.debug("Cargando notificaciones para ID={}, Roles={}", usuarioId, roles);
        
        if (roles.isEmpty()) {
            return repository.findByUsuarioIdOrderByFechaDesc(usuarioId);
        }
        return repository.findByUsuarioIdOrRoles(usuarioId, roles);
    }

    @GetMapping("/me/count")
    public long contarMisNoLeidas() {
        Integer usuarioId = obtenerIdUsuarioActual();
        Collection<String> roles = obtenerRolesUsuarioActual();
        
        if (roles.isEmpty()) {
            return repository.countByUsuarioIdAndLeidaFalse(usuarioId);
        }
        return repository.countByUsuarioIdOrRolesAndNoLeidas(usuarioId, roles);
    }

    private Integer obtenerIdUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getId();
        }
        return null;
    }

    private Collection<String> obtenerRolesUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<NotificacionEntity> listarPorUsuario(@PathVariable Integer usuarioId) {
        return repository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/no-leidas/count")
    public long contarNoLeidas(@PathVariable Integer usuarioId) {
        return repository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Integer id) {
        repository.findById(id).ifPresent(n -> {
            n.setLeida(true);
            repository.save(n);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public NotificacionEntity crear(@RequestBody NotificacionEntity notificacion) {
        return repository.save(notificacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
