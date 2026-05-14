package es.refugio.refugio.application.listener;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.event.TareaStatusChangedEvent;
import es.refugio.refugio.domain.repository.TareaHistorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TareaHistorialListener {

    private final TareaHistorialRepository tareaHistorialRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTareaStatusChanged(TareaStatusChangedEvent event) {
        log.info("Registrando historial para tarea {}: {} -> {}", 
                event.getTareaId().getValue(), event.getEstadoAnterior(), event.getEstadoNuevo());
        
        TareaHistorial historial = TareaHistorial.builder()
                .tareaId(event.getTareaId())
                .estadoAnterior(event.getEstadoAnterior())
                .estadoNuevo(event.getEstadoNuevo())
                .usuarioId(event.getUsuarioActorId())
                .fechaCambio(event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now())
                .observaciones(event.getObservaciones())
                .build();
        
        tareaHistorialRepository.save(historial);
    }
}
