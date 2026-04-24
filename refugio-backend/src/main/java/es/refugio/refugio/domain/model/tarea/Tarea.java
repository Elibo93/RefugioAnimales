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
    private LocalDateTime fechaLimite;
    private String instrucciones;
    private List<VoluntarioId> voluntarios;

    public String getPrioridad() {
        if (fechaLimite == null) return "BAJA";
        LocalDateTime now = LocalDateTime.now();
        if (fechaLimite.isBefore(now.plusDays(1))) {
            return "ALTA";
        } else if (fechaLimite.isBefore(now.plusDays(3))) {
            return "MEDIA";
        }
        return "BAJA";
    }
}
