package es.refugio.animales.refugio.domain.model.adoptante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Adoptante {

    private AdoptanteId id;
    private Integer usuarioId; // Relación con la entidad Usuario (FK)
    private String dni;
    private String nombre;
    private String direccion;
    private String estadoValidacion; // "PENDIENTE", "APROBADO", "RECHAZADO"
    // Relaciones (IDs de las entidades relacionadas)
    private List<Integer> solicitudesIds;
    private List<Integer> adopcionesIds;

}
