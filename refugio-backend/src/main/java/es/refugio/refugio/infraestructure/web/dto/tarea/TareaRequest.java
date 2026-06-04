package es.refugio.refugio.infraestructure.web.dto.tarea;

import java.time.LocalDateTime;
import java.util.List;

public record TareaRequest(
        String descripcion,
        LocalDateTime fecha,
        String estado,
        LocalDateTime fechaLimite,
        String instrucciones,
        List<Integer> voluntarioIds,
        Integer voluntarioActorId
) {
}
