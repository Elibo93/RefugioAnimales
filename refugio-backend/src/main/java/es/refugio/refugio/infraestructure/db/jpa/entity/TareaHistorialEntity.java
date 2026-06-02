package es.refugio.refugio.infraestructure.db.jpa.entity;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tarea_historial")
public class TareaHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tarea_id", nullable = false)
    private Integer tareaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 50)
    private EstadoTarea estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 50)
    private EstadoTarea estadoNuevo;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "fecha_cambio", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime fechaCambio;

    @Column(length = 255)
    private String observaciones;
}
