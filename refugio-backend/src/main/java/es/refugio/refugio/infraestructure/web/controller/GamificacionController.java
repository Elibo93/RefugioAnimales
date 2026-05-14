package es.refugio.refugio.infraestructure.web.controller;

import es.refugio.refugio.domain.model.gamificacion.Logro;
import es.refugio.refugio.domain.model.gamificacion.UsuarioLogro;
import es.refugio.refugio.domain.model.gamificacion.UsuarioMetricas;
import es.refugio.refugio.domain.repository.gamificacion.LogroRepository;
import es.refugio.refugio.domain.repository.gamificacion.UsuarioLogroRepository;
import es.refugio.refugio.domain.repository.gamificacion.UsuarioMetricasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gamificacion")
@RequiredArgsConstructor
public class GamificacionController {

    private final UsuarioMetricasRepository metricasRepository;
    private final UsuarioLogroRepository usuarioLogroRepository;
    private final LogroRepository logroRepository;

    @GetMapping("/metricas/usuario/{usuarioId}")
    public ResponseEntity<UsuarioMetricas> getMetricas(@PathVariable Long usuarioId) {
        return metricasRepository.findByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(new UsuarioMetricas(usuarioId, 0, java.math.BigDecimal.ZERO, null, java.time.LocalDateTime.now())));
    }

    @GetMapping("/logros/usuario/{usuarioId}")
    public ResponseEntity<List<UsuarioLogro>> getLogrosUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(usuarioLogroRepository.findByUsuarioId(usuarioId));
    }

    @GetMapping("/logros")
    public ResponseEntity<List<Logro>> getAllLogros() {
        return ResponseEntity.ok(logroRepository.findAll());
    }
}
