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

/**
 * Listener de eventos de dominio que persiste una entrada de auditoría en el historial
 * de cambios de estado de las tareas cada vez que se publica un {@link TareaStatusChangedEvent}.
 *
 * <p>Se ejecuta en una transacción independiente ({@code REQUIRES_NEW}) para garantizar que
 * el registro del historial no se vea afectado por el rollback de la transacción principal.
 *
 * @author Elisabeth
 * @author Diego
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TareaHistorialListener {

    private final TareaHistorialRepository tareaHistorialRepository;

    /**
     * Recibe un evento de cambio de estado de una tarea y persiste el registro de auditoría
     * en el historial, incluyendo el estado anterior, el nuevo estado, el usuario actor y
     * la marca de tiempo del cambio.
     *
     * @param event Evento publicado al cambiar el estado de una tarea, que contiene toda
     *              la información necesaria para construir la entrada de historial.
     */
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
