package es.refugio.refugio.application.command.tarea;

import java.time.LocalDateTime;
import java.util.List;

public record CreateTareaCommand(
        String descripcion,
        LocalDateTime fecha,
        String estado,
        LocalDateTime fechaLimite,
        String instrucciones,
        List<Integer> voluntarioIds,
        Integer voluntarioActorId
) {
}
