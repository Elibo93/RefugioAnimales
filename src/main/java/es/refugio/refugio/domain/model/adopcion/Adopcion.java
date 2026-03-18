package es.refugio.refugio.domain.model.adopcion;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Adopcion {
    private AdopcionId id;
    private UsuarioId usuarioId;
    private AnimalId animalId;
    private LocalDateTime createdAt;

}
