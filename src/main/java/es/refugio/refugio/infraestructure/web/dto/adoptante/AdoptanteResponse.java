package es.refugio.refugio.infraestructure.web.dto.adoptante;

import java.time.LocalDateTime;

public record AdoptanteResponse(
        int id,
        Integer usuarioId,
        String dni,
        String direccion,
        String estadoValidacion,
        LocalDateTime fechaRegistro) {
}