package es.refugio.refugio.infraestructure.web.dto.tarea;

import java.time.LocalDateTime;
import java.util.List;

public record TareaResponse(
        Integer id,
        String descripcion,
        LocalDateTime fecha,
        String estado,
        List<Integer> voluntarioIds
) {
}
