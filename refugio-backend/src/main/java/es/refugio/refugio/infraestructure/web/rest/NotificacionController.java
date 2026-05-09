package es.refugio.refugio.infraestructure.web.rest;

import es.refugio.refugio.infraestructure.db.jpa.entity.NotificacionEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionRepository repository;

    @GetMapping("/me")
    public List<NotificacionEntity> listarMisNotificaciones() {
        Integer usuarioId = obtenerIdUsuarioActual();
        java.util.Collection<String> roles = obtenerRolesUsuarioActual();
        
        System.out.println("DEBUG: Cargando notificaciones para ID=" + usuarioId + ", Roles=" + roles);
        
        if (usuarioId == null && (roles == null || roles.isEmpty())) {
            return java.util.Collections.emptyList();
        }
        
        if (roles == null || roles.isEmpty()) {
            return repository.findByUsuarioIdOrderByFechaDesc(usuarioId);
        }
        
        return repository.findByUsuarioIdOrRoles(usuarioId, roles);
    }

    @GetMapping("/me/count")
    public long contarMisNoLeidas() {
        Integer usuarioId = obtenerIdUsuarioActual();
        java.util.Collection<String> roles = obtenerRolesUsuarioActual();
        
        if (usuarioId == null && (roles == null || roles.isEmpty())) {
            return 0;
        }
        
        if (roles == null || roles.isEmpty()) {
            return repository.countByUsuarioIdAndLeidaFalse(usuarioId);
        }
        
        return repository.countByUsuarioIdOrRolesAndNoLeidas(usuarioId, roles);
    }

    private Integer obtenerIdUsuarioActual() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        
        Object principal = auth.getPrincipal();
        
        if (principal instanceof es.refugio.refugio.infraestructure.security.CustomUserDetails) {
            return ((es.refugio.refugio.infraestructure.security.CustomUserDetails) principal).getId();
        }
        
        if (principal instanceof String) {
            String s = (String) principal;
            if (s.equals("anonymousUser")) return null;
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return null;
            }
        }
        
        if (principal instanceof Number) {
            return ((Number) principal).intValue();
        }
        
        return null;
    }

    private java.util.Collection<String> obtenerRolesUsuarioActual() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                    .collect(java.util.stream.Collectors.toList());
        }
        return java.util.Collections.emptyList();
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
