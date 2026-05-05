package es.refugio.refugio.infraestructure.web.dto.voluntario;

public record VoluntarioResponse(
        Integer id,
        Integer usuarioId,
        String disponibilidad,
        String especialidad
) {
}
