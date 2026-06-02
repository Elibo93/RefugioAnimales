package es.refugio.refugio.domain.model.preferencia;

import java.time.LocalDateTime;
import java.util.List;
import es.refugio.refugio.domain.model.animal.enums.Especie;
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
public class PreferenciaAdopcion {

    private PreferenciaAdopcionId id;
    private Integer usuarioId;
    private List<Especie> especies;
    private List<Tamano> tamanos;
    private List<Sexo> sexos;
    private Integer edadMax;
    private Integer nivelEnergiaMax;
    @Builder.Default
    private boolean notificacionesActivas = true;
    @Builder.Default
    private boolean encuestaOmitida = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
