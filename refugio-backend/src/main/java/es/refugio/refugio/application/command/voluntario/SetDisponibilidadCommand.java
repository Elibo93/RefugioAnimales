package es.refugio.refugio.application.command.voluntario;

import java.time.LocalDate;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.model.voluntario.enums.TurnoDisponibilidad;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoDisponibilidad;

public record SetDisponibilidadCommand(
    VoluntarioId voluntarioId,
    LocalDate fecha,
    TurnoDisponibilidad turno,
    EstadoDisponibilidad estado
) {
    public SetDisponibilidadCommand {
        if (fecha != null && fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a hoy.");
        }
    }
}
