package es.refugio.refugio.domain.model.tarea;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TareaHistorial {
    private Integer id;
    private TareaId tareaId;
    private EstadoTarea estadoAnterior;
    private EstadoTarea estadoNuevo;
    private Integer usuarioId;
    private LocalDateTime fechaCambio;
    private String observaciones;
}
