package es.refugio.refugio.infraestructure.web.dto.adoptante;

import es.refugio.refugio.infraestructure.web.validation.MinAge;

/**
 * DTO específico para la actualización de adoptantes.
 * Se elimina la validación estricta de DNI para permitir datos de prueba 
 * y mayor flexibilidad en el mantenimiento.
 */
public record AdoptanteUpdateRequest(
        Integer usuarioId,
        String dni,
        String direccion,
        @MinAge(18) String fechaNacimiento,
        String estadoValidacion
) {}
