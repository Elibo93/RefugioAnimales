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

/**
 * Motor de gamificación que evalúa y otorga logros a los usuarios en función de sus métricas acumuladas.
 * Recorre todos los logros disponibles en el sistema y, para cada uno, comprueba si el usuario cumple
 * el requisito asociado. Si lo cumple y aún no lo tenía, lo otorga y envía una notificación.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogroEngine {
    private final LogroRepository logroRepository;
    private final UsuarioLogroRepository usuarioLogroRepository;
    private final NotificacionService notificacionService;

    /**
     * Comprueba todos los logros definidos en el sistema para las métricas del usuario indicado.
     * Para cada logro aún no obtenido, evalúa si el usuario cumple su requisito y, en caso afirmativo,
     * lo otorga llamando a {@link #otorgarLogro(Long, Logro)}.
     *
     * @param metricas Objeto con las métricas actualizadas del usuario (tareas completadas, total donado, etc.).
     */
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

    /**
     * Persiste el logro obtenido por el usuario y envía una notificación in-app para informarle.
     * En caso de fallo en el envío de la notificación, el error se registra en el log como advertencia
     * sin interrumpir el flujo principal.
     *
     * @param usuarioId ID del usuario al que se le otorga el logro.
     * @param logro     Objeto {@link Logro} que representa el mérito desbloqueado.
     */
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
