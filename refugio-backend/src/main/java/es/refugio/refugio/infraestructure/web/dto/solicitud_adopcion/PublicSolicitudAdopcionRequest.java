package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;

/**
 * DTO simplificado para el registro de una solicitud de adopción tras un alta
 * pública.
 * Los datos personales y de usuario ya han sido gestionados por el frontend
 * en los microservicios correspondientes (Auth y PerfilLegal).
 */
public record PublicSolicitudAdopcionRequest(
        @NotNull(message = "El ID del usuario es obligatorio") Integer usuarioId,

        @NotNull(message = "El ID del animal es obligatorio") Integer animalId,

        String comentario,

        // Datos del Perfil Legal para creación automática
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El apellido es obligatorio") String apellido,
        @NotBlank(message = "El DNI es obligatorio") @ValidDni String dni,
        String telefono,
        String direccion,
        String fechaNacimiento) {
}
