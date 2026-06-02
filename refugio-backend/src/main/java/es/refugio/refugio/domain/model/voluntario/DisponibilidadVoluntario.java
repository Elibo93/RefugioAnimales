package es.refugio.refugio.domain.model.voluntario;

import java.time.LocalDate;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoDisponibilidad;
import es.refugio.refugio.domain.model.voluntario.enums.TurnoDisponibilidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DisponibilidadVoluntario {
    private DisponibilidadVoluntarioId id;
    private VoluntarioId voluntarioId;
    private LocalDate fecha;
    private TurnoDisponibilidad turno;
    private EstadoDisponibilidad estado;
}
