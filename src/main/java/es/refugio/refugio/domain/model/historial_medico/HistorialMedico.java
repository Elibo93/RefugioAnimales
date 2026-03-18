package es.refugio.refugio.domain.model.historial_medico;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.animal.AnimalId;
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
