package es.refugio.refugio.application.listener;

import es.refugio.refugio.application.service.gamificacion.LogroEngine;
import es.refugio.refugio.domain.event.donacion.DonacionCompletedEvent;
import es.refugio.refugio.domain.model.tarea.event.TareaStatusChangedEvent;
import es.refugio.refugio.domain.model.gamificacion.UsuarioMetricas;
import es.refugio.refugio.domain.repository.gamificacion.UsuarioMetricasRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener de eventos de dominio responsable de actualizar las métricas de gamificación
 * de los usuarios cuando ocurren eventos relevantes en el sistema (cambios de estado de tareas
 * y donaciones completadas). Delega la evaluación de logros al {@link LogroEngine}.
 *
 * <p>Todos sus métodos se ejecutan de forma asíncrona para no bloquear el flujo principal.
 *
 * @author Elisabeth
 * @author Diego
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GamificacionListener {

    private final UsuarioMetricasRepository metricasRepository;
    private final LogroEngine logroEngine;
    private final VoluntarioRepository voluntarioRepository;

    /**
     * Escucha el evento {@link TareaStatusChangedEvent} y actualiza las métricas de tareas
     * completadas para cada voluntario asignado a la tarea cuando ésta pasa a estado
     * {@code COMPLETADA} o {@code FINALIZADA}. Tras actualizar las métricas, lanza la evaluación
     * de logros correspondiente.
     *
     * @param event Evento publicado al cambiar el estado de una tarea.
     */
    @Async
    @EventListener
    @Transactional
    public void onTareaStatusChanged(TareaStatusChangedEvent event) {
        String nuevoEstado = String.valueOf(event.getEstadoNuevo());
        // Nos interesan las tareas que se marcan como completadas o finalizadas
        if (!"COMPLETADA".equals(nuevoEstado) && !"FINALIZADA".equals(nuevoEstado)) {
            return;
        }

        if (event.getVoluntarioIds() == null || event.getVoluntarioIds().isEmpty()) return;

        for (Integer voluntarioId : event.getVoluntarioIds()) {
            // Mapear voluntarioId a su usuarioId real
            Long usuarioId = voluntarioRepository.getById(new VoluntarioId(voluntarioId))
                    .map(voluntario -> Long.valueOf(voluntario.getUsuarioId().getValue()))
                    .orElse(Long.valueOf(voluntarioId));

            log.info("Actualizando métricas (estado {}) para el usuario {}", nuevoEstado, usuarioId);
            
            UsuarioMetricas metricas = metricasRepository.findByUsuarioId(usuarioId)
                    .orElse(new UsuarioMetricas());
            
            if (metricas.getUsuarioId() == null) {
                metricas.setUsuarioId(usuarioId);
            }

            metricas.incrementarTareas();
            metricasRepository.save(metricas);

            logroEngine.checkLogros(metricas);
        }
    }

    /**
     * Escucha el evento {@link DonacionCompletedEvent} y acumula el monto donado
     * en las métricas del usuario para habilitar la evaluación de logros relacionados
     * con la generosidad o el importe total donado.
     *
     * @param event Evento publicado cuando una donación se marca como completada.
     */
    @Async
    @EventListener
    @Transactional
    public void onDonacionCompleted(DonacionCompletedEvent event) {
        log.info("Actualizando métricas por donación recibida para el usuario {}", event.getUsuarioId());

        UsuarioMetricas metricas = metricasRepository.findByUsuarioId(event.getUsuarioId())
                .orElse(new UsuarioMetricas());

        if (metricas.getUsuarioId() == null) {
            metricas.setUsuarioId(event.getUsuarioId());
        }

        metricas.agregarDonacion(event.getMonto());
        metricasRepository.save(metricas);

        logroEngine.checkLogros(metricas);
    }
}
