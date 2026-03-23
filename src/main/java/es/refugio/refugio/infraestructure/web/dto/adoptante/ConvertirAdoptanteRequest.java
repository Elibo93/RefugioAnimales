package es.refugio.refugio.infraestructure.web.dto.adoptante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertirAdoptanteRequest {
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String fechaNacimiento;

    @NotNull(message = "El ID del animal es obligatorio")
    private Integer animalId;
}
