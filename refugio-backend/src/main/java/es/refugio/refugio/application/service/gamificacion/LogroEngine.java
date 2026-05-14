package es.refugio.refugio.application.service.gamificacion;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.gamificacion.Logro;
import es.refugio.refugio.domain.model.gamificacion.UsuarioLogro;
import es.refugio.refugio.domain.model.gamificacion.UsuarioMetricas;
import es.refugio.refugio.domain.repository.gamificacion.LogroRepository;
import es.refugio.refugio.domain.repository.gamificacion.UsuarioLogroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogroEngine {
    private final LogroRepository logroRepository;
    private final UsuarioLogroRepository usuarioLogroRepository;
    private final NotificacionService notificacionService;

    @Transactional
    public void checkLogros(UsuarioMetricas metricas) {
        log.info("Comprobando logros para el usuario {}", metricas.getUsuarioId());
        List<Logro> todosLosLogros = logroRepository.findAll();

        for (Logro logro : todosLosLogros) {
            // Si ya tiene el logro, saltar
            if (usuarioLogroRepository.existsByUsuarioIdAndLogroId(metricas.getUsuarioId(), logro.getId())) {
                continue;
            }

            boolean cumpleRequisito = false;
            switch (logro.getRequisitoTipo()) {
                case TAREAS_COMPLETADAS:
                    cumpleRequisito = metricas.getTareasCompletadas() >= logro.getRequisitoValor().intValue();
                    break;
                case TOTAL_DONADO:
                    cumpleRequisito = metricas.getTotalDonado().compareTo(logro.getRequisitoValor()) >= 0;
                    break;
                case ANTIGUEDAD_MESES:
                    // Lógica para antigüedad si fuera necesario
                    break;
            }

            if (cumpleRequisito) {
                otorgarLogro(metricas.getUsuarioId(), logro);
            }
        }
    }

    private void otorgarLogro(Long usuarioId, Logro logro) {
        log.info("¡Logro desbloqueado! Usuario: {}, Logro: {}", usuarioId, logro.getNombre());
        
        UsuarioLogro nuevoLogro = new UsuarioLogro(usuarioId, logro.getId(), LocalDateTime.now());
        usuarioLogroRepository.save(nuevoLogro);

        // Notificar al usuario (Toast especial)
        try {
            String titulo = "🏆 ¡Logro Desbloqueado!";
            String mensaje = String.format("Has ganado el mérito: %s. %s", logro.getNombre(), logro.getDescripcion());
            notificacionService.enviar(usuarioId.intValue(), titulo, mensaje, "LOGRO_DESBLOQUEADO", "/web/personas/" + usuarioId);
        } catch (Exception e) {
            log.warn("No se pudo enviar la notificación de logro al usuario {}", usuarioId);
        }
    }
}
