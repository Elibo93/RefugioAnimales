package es.refugio.refugio.domain.model.adoptante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;

@Data
@AllArgsConstructor
@Builder
public class Adoptante {

    private AdoptanteId id;
    private Integer usuarioId; // Relación con la entidad Usuario (FK)
    private String dni;
    private String nombre;
    private String direccion;
    private String fechaNacimiento;
    private EstadoValidacion estadoValidacion;
    // Relaciones (IDs de las entidades relacionadas)
    private LocalDateTime fechaRegistro;
    private List<Integer> solicitudesIds;
    private List<Integer> adopcionesIds;

}
