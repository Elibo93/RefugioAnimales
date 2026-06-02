package es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gamificacion_usuario_metricas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMetricasEntity {
    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "tareas_completadas")
    private int tareasCompletadas;

    @Column(name = "total_donado")
    private BigDecimal totalDonado;

    @Column(name = "fecha_primer_aporte")
    private LocalDateTime fechaPrimerAporte;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
}
