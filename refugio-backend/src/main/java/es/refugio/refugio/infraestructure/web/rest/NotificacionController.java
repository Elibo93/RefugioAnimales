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
