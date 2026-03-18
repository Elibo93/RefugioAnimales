package es.refugio.animales.refugio.domain.model.historial_medico;

import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class HistorialMedico {
    private HistorialMedicoId id;
    private AnimalId animalId;
    private LocalDateTime fecha;
    private String descripcion;
}
