package es.refugio.refugio.domain.model.voluntario;

import java.util.List;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoVoluntario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Voluntario {
    private VoluntarioId id;
    private UsuarioId usuarioId;
    private String disponibilidad;
    private String especialidad;
    private List<TareaId> tareas;
    private EstadoVoluntario estado;
}
