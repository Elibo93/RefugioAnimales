package es.refugio.refugio.infraestructure.web.dto.preferencia;

import java.util.List;

public record PreferenciaAdopcionRequest(
        Integer usuarioId,
        List<String> especies,
        List<String> tamanos,
        List<String> sexos,
        Integer edadMax,
        Integer nivelEnergiaMax,
        Boolean notificacionesActivas,
        Boolean encuestaOmitida
) {}
