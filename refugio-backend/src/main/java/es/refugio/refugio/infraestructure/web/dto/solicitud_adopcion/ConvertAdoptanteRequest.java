package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConvertAdoptanteRequest(
        @NotBlank(message = "El DNI es obligatorio")
        String dni,
        
        @NotBlank(message = "La dirección es obligatoria")
        String direccion,
        
        @NotBlank(message = "La fecha de nacimiento es obligatoria")
        String fechaNacimiento,
        
        @NotNull(message = "El ID del animal es obligatorio")
        Integer animalId,
        
        String comentario
) {}
