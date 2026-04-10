package es.refugio.refugio.domain.model.adopcion;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Adopcion {
    private AdopcionId id;
    private AdoptanteId adoptanteId;
    private AnimalId animalId;
    private Integer solicitudAdopcionId;
    private LocalDateTime fechaAdopcion;
    private EstadoAdopcion estado;
    private String contrato;
}
