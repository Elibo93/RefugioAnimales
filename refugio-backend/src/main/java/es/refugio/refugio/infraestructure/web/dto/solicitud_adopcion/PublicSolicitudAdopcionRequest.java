package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PublicSolicitudAdopcionRequest(
        // Datos del Usuario
        @NotBlank(message = "{Usuario.valid.nombre.no_vacio}")
        String nombre,

        @NotBlank(message = "{Usuario.valid.apellido.no_vacio}")
        String apellido,

        @NotBlank(message = "{Usuario.valid.email.no_vacio}")
        @Email(message = "{Usuario.valid.email.valido}")
        String email,

        @NotBlank(message = "{Usuario.valid.password.no_vacio}")
        String contrasena,

        @NotBlank(message = "{Usuario.valid.telefono.no_vacio}")
        String telefono,

        // Datos del Adoptante
        @NotBlank(message = "El DNI es obligatorio")
        String dni,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        @NotBlank(message = "La fecha de nacimiento es obligatoria")
        String fechaNacimiento,

        // Datos de la Solicitud
        @NotNull(message = "El ID del animal es obligatorio")
        Integer animalId,

        String comentario
) {
}
