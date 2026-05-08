package es.refugio.refugio.infraestructure.web.dto.adoptante;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para convertir un usuario público en adoptante y crear su primera solicitud.
 * Los datos personales (nombre, DNI, etc.) deben estar ya presentes en el PerfilLegal
 * o ser gestionados a través del endpoint de perfil legal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertirAdoptanteRequest {
    @NotNull(message = "El ID del animal es obligatorio")
    private Integer animalId;

    private String comentario;
}
