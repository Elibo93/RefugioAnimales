package es.refugio.refugio.infraestructure.web.dto.usuario;

import java.time.LocalDateTime;
import es.refugio.auth.domain.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponse(
        @Schema(description = "ID único del usuario", example = "23") 
        int id,

        @Schema(description = "Nombre del usuario", example = "Juan") 
        String nombre,

        @Schema(description = "Apellido del usuario", example = "Rodriguez") 
        String apellido,

        @Schema(description = "Email de acceso", example = "usuario@email.com") 
        String email,

        @Schema(description = "Número de teléfono", example = "123456789") 
        String telefono,

        @Schema(description = "Rol asignado en el sistema", example = "ROLE_ADOPTANTE") 
        Rol rol,

        @Schema(description = "Fecha de registro en la plataforma") 
        LocalDateTime createdAt) {
}