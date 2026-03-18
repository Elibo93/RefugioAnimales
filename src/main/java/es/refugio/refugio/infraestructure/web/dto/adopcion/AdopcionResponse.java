package es.refugio.refugio.infraestructure.web.dto.adopcion;

import java.time.LocalDateTime;

public record AdopcionResponse(
        int id,
        int PersonaId,
        int AnimalId,
        LocalDateTime fechaAdopcion
) {
}
















