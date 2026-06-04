package es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gamificacion_usuario_logros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogroEntity {
    @EmbeddedId
    private UsuarioLogroId id;

    @Column(name = "fecha_desbloqueo", nullable = false)
    private LocalDateTime fechaDesbloqueo;
}
