package es.refugio.refugio.domain.model.animal;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Animal {

    private AnimalId id;
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private String chipId;
    private Integer edad;
    private String tamano;
    private String estado;
    private String descripcion;
    private String foto;
    private LocalDateTime fechaIngreso;

}
