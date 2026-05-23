package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;
import es.refugio.refugio.infraestructure.web.validation.MinAge;

/**
 * DTO simplificado para el registro de una solicitud de adopción tras un alta
 * pública.
 * Los datos personales y de usuario ya han sido gestionados por el frontend
 * en los microservicios correspondientes (Auth y PerfilLegal).
 */
public record PublicSolicitudAdopcionRequest(
        @NotNull(message = "{error.validation.usuario_id_obligatorio}") Integer usuarioId,

        @NotNull(message = "{error.validation.animal_id_obligatorio}") Integer animalId,

        String comentario,

        // Datos del Perfil Legal para creación automática
        @NotBlank(message = "{error.validation.nombre_obligatorio}") String nombre,
        @NotBlank(message = "{error.validation.apellido_obligatorio}") String apellido,
        @NotBlank(message = "{error.validation.dni_obligatorio}") @ValidDni String dni,
        String telefono,
        String direccion,
        @MinAge(18) String fechaNacimiento) {
}
