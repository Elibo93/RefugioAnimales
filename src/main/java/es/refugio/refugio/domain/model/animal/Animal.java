package es.refugio.refugio.domain.model.animal;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Animal {

    private AnimalId id;
    private String nombre;
    private Especie especie;
    private String especiePersonalizada;
    private String raza;
    private Sexo sexo;
    private String chipId;
    private Integer edad;
    private Tamano tamano;
    private EstadoAnimal estado;
    private String descripcion;
    private String foto;
    private Double peso;
    private Integer nivelEnergia;
    private Boolean urgencia;
    private Integer visitas;
    private LocalDateTime fechaIngreso;

}