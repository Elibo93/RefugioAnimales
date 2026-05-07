package es.refugio.refugio.infraestructure.web.dto.adoptante;

import java.time.LocalDateTime;

public record AdoptanteResponse(
        int id,
        Integer usuarioId,
        String fechaNacimiento,
        String estadoValidacion,
        LocalDateTime fechaRegistro) {
}