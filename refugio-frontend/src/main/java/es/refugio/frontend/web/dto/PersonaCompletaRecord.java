package es.refugio.frontend.web.dto;

public record PersonaCompletaRecord(
        int id,
        String email,
        String username,
        String rol,
        String nombre,
        String apellido,
        String dni,
        String telefono,
        String direccion,
        String fechaNacimiento
) {}
