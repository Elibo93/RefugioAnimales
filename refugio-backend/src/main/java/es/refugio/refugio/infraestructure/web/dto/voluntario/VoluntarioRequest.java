package es.refugio.refugio.infraestructure.web.dto.voluntario;

public record VoluntarioRequest(
        Integer usuarioId,
        String disponibilidad
) {
}
