package es.refugio.refugio.domain.model.tarea.event;

import java.time.LocalDateTime;
import java.util.List;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TareaStatusChangedEvent {
    private final TareaId tareaId;
    private final EstadoTarea estadoAnterior;
    private final EstadoTarea estadoNuevo;
    private final Integer usuarioActorId;
    private final LocalDateTime timestamp;
    private final String observaciones;
    private final List<Integer> voluntarioIds;
}
