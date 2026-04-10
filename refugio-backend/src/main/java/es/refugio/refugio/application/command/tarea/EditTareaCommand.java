package es.refugio.refugio.application.command.tarea;

import java.time.LocalDateTime;
import java.util.List;
import es.refugio.refugio.domain.model.tarea.TareaId;

public record EditTareaCommand(
        TareaId id,
        String descripcion,
        LocalDateTime fecha,
        String estado,
        List<Integer> voluntarioIds
) {
}
