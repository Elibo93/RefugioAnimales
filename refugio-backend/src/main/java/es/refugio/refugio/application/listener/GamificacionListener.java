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

@Component
@RequiredArgsConstructor
@Slf4j
public class GamificacionListener {

    private final UsuarioMetricasRepository metricasRepository;
    private final LogroEngine logroEngine;
    private final VoluntarioRepository voluntarioRepository;

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
