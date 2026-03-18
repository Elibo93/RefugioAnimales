package es.refugio.refugio.infraestructure.web.dto.usuario;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponse(
        @Schema(description = "ID único del producto", example = "23") int id,

        @Schema(description = "Número del DNI del Persona", example = "12345678A") String dni,

        @Schema(description = "Nombre del Persona", example = "Juan") String nombre,

        @Schema(description = "Apellido del Persona", example = "Rodriguez") String apellido,

        @Schema(description = "Email del Persona", example = "Persona@email.com") String email,

        @Schema(description = "Número de teléfono del Persona", example = "123456789") String telefono,

        @Schema(description = "Dirección del Persona", example = "C/ El Pinar 34") String direccion,

        @Schema(description = "Fecha de nacimiento del Persona", example = "26/05/1993") String fechaNacimiento,

        @Schema(description = "Fecha de creación del Persona", example = "03/02/2026") LocalDateTime createdAt) {

}
















