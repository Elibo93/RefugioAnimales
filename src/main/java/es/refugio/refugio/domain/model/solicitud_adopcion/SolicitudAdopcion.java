package es.refugio.refugio.domain.model.solicitud_adopcion;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SolicitudAdopcion {

    private SolicitudAdopcionId id;
    private AnimalId animalId;
    private AdoptanteId adoptanteId;
    private LocalDateTime fecha;
    private EstadoSolicitud estado;
    private String comentario;

}
