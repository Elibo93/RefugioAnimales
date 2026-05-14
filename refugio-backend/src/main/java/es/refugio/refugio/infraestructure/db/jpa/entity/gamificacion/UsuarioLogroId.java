package es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogroId implements Serializable {
    @Column(name = "usuario_id")
    private Long usuarioId;
    @Column(name = "logro_id")
    private Long logroId;
}
