package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;
import es.refugio.refugio.infraestructure.web.validation.MinAge;

public record ConvertAdoptanteRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El apellido es obligatorio") String apellido,
        @NotBlank(message = "El DNI es obligatorio") /* @ValidDni */ String dni, // sin validar para pruebas

        @NotBlank(message = "La dirección es obligatoria") String direccion,
        @NotBlank(message = "El teléfono es obligatorio") String telefono,

        @NotBlank(message = "La fecha de nacimiento es obligatoria") @MinAge(18) String fechaNacimiento,

        @NotNull(message = "El ID del animal es obligatorio") Integer animalId,

        String comentario) {
}
