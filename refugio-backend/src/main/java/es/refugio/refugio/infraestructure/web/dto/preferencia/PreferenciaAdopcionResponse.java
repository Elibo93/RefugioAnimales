package es.refugio.refugio.infraestructure.web.dto.preferencia;

import java.time.LocalDateTime;
import java.util.List;

public record PreferenciaAdopcionResponse(
        Integer id,
        Integer usuarioId,
        List<String> especies,
        List<String> tamanos,
        List<String> sexos,
        Integer edadMax,
        Integer nivelEnergiaMax,
        Boolean notificacionesActivas,
        Boolean encuestaOmitida,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
