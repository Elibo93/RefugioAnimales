package es.refugio.refugio.infraestructure.web.dto.perfil_legal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con los datos del perfil legal")
public record PerfilLegalResponse(
    Integer usuarioId,
    String nombre,
    String apellido,
    String dni,
    String telefono,
    String direccion,
    String fechaNacimiento
) {}
