package es.refugio.refugio.domain.model.tarea;

import java.time.LocalDateTime;
import java.util.List;

import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Tarea {
    private TareaId id;
    private String descripcion;
    private LocalDateTime fecha;
    private EstadoTarea estado;
    private List<VoluntarioId> voluntarios;
}
