package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;
import es.refugio.refugio.infraestructure.web.validation.MinAge;

public record ConvertAdoptanteRequest(
                @NotBlank(message = "{error.validation.nombre_obligatorio}") String nombre,
                @NotBlank(message = "{error.validation.apellido_obligatorio}") String apellido,
                @NotBlank(message = "{error.validation.dni_obligatorio}") @ValidDni String dni,

                @NotBlank(message = "{error.validation.direccion_obligatoria}") String direccion,
                @NotBlank(message = "{error.validation.telefono_obligatorio}") String telefono,

                @NotBlank(message = "{error.validation.fecha_nacimiento_obligatoria}") @MinAge(18) String fechaNacimiento,

                @NotNull(message = "{error.validation.animal_id_obligatorio}") Integer animalId,

                String comentario) {
}
