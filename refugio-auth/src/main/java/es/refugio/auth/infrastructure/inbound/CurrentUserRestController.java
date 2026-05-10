package es.refugio.auth.infrastructure.inbound;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Endpoint que devuelve información del usuario actualmente autenticado.
 *
 * Utilizado por el frontend (microservicio separado) para saber quién está
 * logueado y personalizar la vista sin depender del SecurityContext local.
 *
 * GET /api/v1/me → 200 con datos del usuario si hay sesión válida
 *                → 401 si no hay sesión autenticada
 */
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class CurrentUserRestController {

    private final UsuarioEntityJpaRepository usuarioEntityJpaRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Sin autenticación o autenticación anónima
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }

        String email = auth.getName();

        Optional<UsuarioEntity> usuarioOpt = usuarioEntityJpaRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        UsuarioEntity u = usuarioOpt.get();

        // Obtener el rol directamente de la base de datos para reflejar cambios en tiempo real
        String rol = u.getRol().name();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", u.getId());
        result.put("username", u.getUsername());
        result.put("email", u.getEmail());
        result.put("rol", rol);

        return ResponseEntity.ok(result);
    }
}
