package es.refugio.refugio.infraestructure.web.dto.voluntario;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.voluntario.VoluntarioId;

public record VoluntarioResponse(
        VoluntarioId id,
        String nombre,
        String apellido,
        String especialidad,
        LocalDateTime createdAt) {

}
















