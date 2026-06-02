package es.refugio.refugio.application.command.voluntario;

import es.refugio.refugio.domain.model.voluntario.VoluntarioId;

public record EditVoluntarioCommand(
                VoluntarioId id,
                String disponibilidad,
                String especialidad) {
}
