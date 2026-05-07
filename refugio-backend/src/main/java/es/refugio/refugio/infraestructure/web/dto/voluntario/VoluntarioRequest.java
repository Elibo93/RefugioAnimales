package es.refugio.refugio.infraestructure.web.dto.voluntario;

public record VoluntarioRequest(
        Integer usuarioId,
        String nombre,
        String apellido,
        String disponibilidad,
        String especialidad,
        String dni,
        String telefono,
        String direccion
) {
}
