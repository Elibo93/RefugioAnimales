package es.refugio.frontend.web.dto;

public record PerfilLegalRecord(
        Integer usuarioId,
        String nombre,
        String apellido,
        String dni,
        String telefono,
        String direccion,
        String fechaNacimiento
) {}
